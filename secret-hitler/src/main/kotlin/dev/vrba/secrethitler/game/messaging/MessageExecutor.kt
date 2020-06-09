package dev.vrba.secrethitler.game.messaging

import dev.vrba.secrethitler.game.Game
import dev.vrba.secrethitler.game.Player
import dev.vrba.secrethitler.game.helpers.RoleAssigner
import dev.vrba.secrethitler.game.state.GameState
import dev.vrba.secrethitler.game.wrappers.*
import io.javalin.websocket.WsMessageContext
import org.json.simple.JSONObject

class MessageExecutor(private val game: Game)
{

    fun execute(message: JSONObject, socket: WsMessageContext, _state: GameState): GameState
    {
        var state = _state
        val sender = state.players.find { it.socket.sessionId == socket.sessionId }

        when (message["type"])
        {
            "join_game" ->
            {
                val username = message["username"] as String
                val player = Player(username, socket)

                state.players.add(player)
            }

            "leave_game" ->
            {
                state.players.removeIf { it.socket.sessionId == socket.sessionId }

                // Reset the game if the player was participating in the game
                // TODO: A way to reestablish the disconnection
                if (state.started)
                {
                    // Generate a fresh state
                    return GameState()
                }
            }

            "ready" ->
            {
                sender?.ready = true

                if (state.players.all { it.ready })
                {
                    state = this.startGame(state)
                }
            }

            "select_player" ->
            {
                val target = state.players.find { it.username == message["username"] }

                if (state.phase == RoundPhase.PresidentIsSelectingTheNextChancellorCandidate)
                {
                    state.government.chancellor = state.players.find { it.username == message["username"] }

                    state.government.president?.state?.availablePlayerTargets = emptyList()
                    state.government.president?.propagatePrivateState()

                    this.game.sendServerMessage(
                        "${state.government.president?.username} has selected " +
                            "${state.government.chancellor?.username} as the chancellor candidate."
                    )

                    state.phase = RoundPhase.PlayersAreVotingForTheNewGovernment
                    state.votes = emptyMap<String, Vote>().toMutableMap()
                    state.showVotes = true
                }

                if (state.phase == RoundPhase.PresidentIsSelectingPlayerForExecution)
                {
                    target?.alive = false
                    sender?.state?.availablePlayerTargets = emptyList()

                    this.game.sendServerMessage("${target?.username} was executed.")

                    this.movePresidentialCandidacyToNextPlayer(state)
                }

                if (state.phase == RoundPhase.PresidentIsSelectingPlayerForInvestigation)
                {
                    // TODO: refactor this shit
                    val username = target?.username ?: ""
                    val party = if (target?.state?.assignedRoles?.get(target.username) == Role.Liberal) Party.Liberal else Party.Fascist
                    val role = if (party == Party.Liberal) Role.Liberal else Role.Fascist

                    sender?.state?.investigatedPartyMembership = Pair(username, party)
                    sender?.state?.assignedRoles?.set(username, role)

                    state.phase = RoundPhase.PresidentIsInvestigatingLoyalty

                    this.game.sendServerMessage(state.government.president?.username + " investigated party membership of $username.")
                }

                if (state.phase == RoundPhase.PresidentIsSelectingTheNextPresidentialCandidate)
                {
                    sender?.state?.availablePlayerTargets = emptyList()
                    target?.state?.availablePlayerTargets = this.eligibleChancellorCandidates(state)

                    state.phase = RoundPhase.PresidentIsSelectingTheNextChancellorCandidate
                    state.presidentHasClaimed = false
                    state.chancellorHasClaimed = false
                    state.government.president = target

                    this.game.sendServerMessage("${sender?.username} selected ${target?.username} as the next president")

                    sender?.propagatePrivateState()
                }
            }

            "vote" ->
            {
                state.votes[sender!!.username] = if (message["value"] == "ja") Vote.Ja else Vote.Nein

                if (state.votes.size == state.players.count { it.alive })
                {
                    val elected = state.votes.values.count { it == Vote.Ja } >
                        state.votes.values.count { it == Vote.Nein }

                    if (!elected)
                    {
                        this.game.sendServerMessage("The election has failed and the government was rejected.")
                        state.electionCounter++

                        if (state.electionCounter >= 3)
                        {
                            this.game.sendServerMessage("The country was thrown into a chaos and the top policy from the drawing pile will be enacted")

                            val policy = state.drawingPile.last()
                            state.drawingPile = state.drawingPile.take(state.drawingPile.size - 1).toMutableList()

                            when (policy)
                            {
                                Party.Fascist -> state.enactedPolicies.fascist++
                                Party.Liberal -> state.enactedPolicies.liberal++
                            }

                            val party = if (policy == Party.Fascist) "fascist" else "liberal"
                            this.game.sendServerMessage(
                                "A $party policy has been enacted",
                                mapOf("party" to party)
                            )

                            state.lastGovernment = Government(null, null)
                            state.electionCounter = 0
                        }

                        val players = state.players

                        val currentPresident = players.indexOf(state.government.president)
                        val nextPresident = players[(currentPresident + 1) % players.size]

                        state.lastGovernment = state.government
                        state.government = Government(nextPresident, null)
                        state.phase = RoundPhase.PresidentIsSelectingTheNextChancellorCandidate

                        nextPresident.state.availablePlayerTargets = this.eligibleChancellorCandidates(state)
                        nextPresident.propagatePrivateState()
                    }
                    else
                    {
                        this.game.sendServerMessage("The election has succeeded and the government was accepted.")

                        state.phase = RoundPhase.PresidentIsSelectingAPolicyToDiscard
                        state.lastGovernment = Government(null, null)

                        // Take the first 3 cards of the drawing pile and put them into the policies pool
                        state.policiesPool.clear()
                        state.policiesPool.addAll(state.drawingPile.take(3))
                        state.drawingPile = state.drawingPile.drop(3).toMutableList()

                        // Send the available policies to the president
                        state.government.president?.state?.availablePolicies = state.policiesPool
                        state.government.president?.propagatePrivateState()
                    }
                }
            }

            "veto" ->
            {
                this.game.sendServerMessage("The chancellor voted to veto the election")

                state.phase = RoundPhase.PresidentIsChoosingWhetherToVetoTheLegislativeSession

            }

            "discard_policy" ->
            {
                val party = if (message["value"] == "Fascist") Party.Fascist else Party.Liberal

                state.policiesPool.remove(party)
                state.discardPile.add(party)

                if (state.phase == RoundPhase.PresidentIsSelectingAPolicyToDiscard)
                {
                    state.government.president?.state?.availablePolicies = emptyList()
                    state.government.chancellor?.state?.availablePolicies = state.policiesPool

                    state.government.president?.propagatePrivateState()
                    state.government.chancellor?.propagatePrivateState()

                    state.phase = RoundPhase.ChancellorIsSelectingAPolicyToDiscard

                    if (state.enactedPolicies.fascist >= 5)
                    {
                        state.government.chancellor?.state?.canUseVeto = true
                    }
                }
                else
                {
                    val policy = state.policiesPool.first()

                    state.government.chancellor?.state?.availablePolicies = emptyList()
                    state.government.chancellor?.state?.canUseVeto = false
                    state.government.chancellor?.propagatePrivateState()

                    if (policy == Party.Liberal)
                    {
                        state.enactedPolicies.liberal++
                    }
                    else
                    {
                        state.enactedPolicies.fascist++
                    }

                    state.government.president?.state?.availableClaims = listOf(
                        Claim(listOf(Party.Liberal, Party.Liberal, Party.Liberal)),
                        Claim(listOf(Party.Liberal, Party.Liberal, Party.Fascist)),
                        Claim(listOf(Party.Liberal, Party.Fascist, Party.Fascist)),
                        Claim(listOf(Party.Fascist, Party.Fascist, Party.Fascist))
                    )

                    state.government.chancellor?.state?.availableClaims = listOf(
                        Claim(listOf(Party.Liberal, Party.Liberal)),
                        Claim(listOf(Party.Liberal, Party.Fascist)),
                        Claim(listOf(Party.Fascist, Party.Fascist))
                    )

                    state.showVotes = false
                    state.phase = RoundPhase.WaitingForGovernmentClaims
                    state.electionCounter = 0

                    this.game.sendServerMessage(
                        "A ${policy.toString().toLowerCase()} policy has been enacted.",
                        mapOf("party" to policy.toString().toLowerCase())
                    )
                }
            }

            "claim" ->
            {
                val presidential = sender == state.government.president

                val fascist = generateSequence { Party.Fascist }
                val liberal = generateSequence { Party.Liberal }

                if (presidential)
                {
                    state.presidentHasClaimed = true
                    state.government.president?.state?.availableClaims = emptyList()

                    if (message["value"] != null)
                    {
                        val claim = (message["value"] as Long).toInt()

                        this.game.sendServerMessage(
                            state.government.president?.username + " (president) has claimed:",
                            mapOf("claim" to Claim((liberal.take(3 - claim) + fascist.take(claim)).toList()))
                        )
                    }
                }
                else
                {
                    state.chancellorHasClaimed = true
                    state.government.chancellor?.state?.availableClaims = emptyList()

                    if (message["value"] != null)
                    {
                        val claim = (message["value"] as Long).toInt()

                        this.game.sendServerMessage(
                            state.government.chancellor?.username + " (chancellor) has claimed:",
                            mapOf("claim" to Claim((liberal.take(2 - claim) + fascist.take(claim)).toList()))
                        )
                    }
                }

                if (state.presidentHasClaimed && state.chancellorHasClaimed)
                {
                    val index = state.enactedPolicies.fascist

                    // A presidential ability should be put into action
                    if (state.presidentialAbilities.containsKey(index) && state.abilitiesUsed < index)
                    {
                        state.abilitiesUsed = index

                        when (state.presidentialAbilities[state.enactedPolicies.fascist])
                        {
                            Ability.ExamineTopThreePolicies ->
                            {
                                val policies = state.drawingPile.take(3)

                                state.phase = RoundPhase.PresidentIsLookingAtTheTopThreePolicies

                                state.government.president?.state?.policiesPeek = policies
                            }

                            Ability.ExecutePlayer ->
                            {
                                state.phase = RoundPhase.PresidentIsSelectingPlayerForExecution
                                state.government.president?.state?.availablePlayerTargets =
                                    state.players.filter { it.alive }.map { it.username }
                            }

                            Ability.InvestigatePartyMembership ->
                            {
                                state.phase = RoundPhase.PresidentIsSelectingPlayerForInvestigation
                                state.government.president?.state?.availablePlayerTargets =
                                    state.players.filter { it.alive }.map { it.username }
                            }

                            Ability.SelectTheNextPresidentialCandidate ->
                            {
                                state.phase = RoundPhase.PresidentIsSelectingTheNextPresidentialCandidate
                                state.government.president?.state?.availablePlayerTargets =
                                    state.players.filter { it.alive && it != sender }.map { it.username }
                            }
                        }
                    }
                    else
                    {
                        this.movePresidentialCandidacyToNextPlayer(state)
                    }
                }
            }

            "acknowledge" ->
            {
                when (state.phase)
                {
                    RoundPhase.PresidentIsLookingAtTheTopThreePolicies ->
                    {
                        state.government.president?.state?.policiesPeek = emptyList()
                    }

                    RoundPhase.PresidentIsInvestigatingLoyalty ->
                    {
                        state.government.president?.state?.investigatedPartyMembership = null
                        state.government.president?.state?.availablePlayerTargets = emptyList()
                    }
                    else ->
                    {
                    }
                }


                this.movePresidentialCandidacyToNextPlayer(state)
            }
        }

        this.checkIfAnyPartyHasWonTheGame(state)

        if (state.drawingPile.size <= 3)
        {
            state.drawingPile.addAll(state.discardPile)
            state.drawingPile.shuffle()
            state.discardPile.clear()
        }

        return state
    }

    private fun startGame(_state: GameState): GameState
    {
        var state = _state
        val president = state.players.random()

        state = RoleAssigner.assign(state)
        state.started = true
        state.phase = RoundPhase.PresidentIsSelectingTheNextChancellorCandidate
        state.government = Government(president, null)
        state.showVotes = false
        state.presidentialAbilities = when (state.players.size)
        {
            5, 6 -> mapOf(
                3 to Ability.ExamineTopThreePolicies,
                4 to Ability.ExecutePlayer,
                5 to Ability.ExecutePlayer
            ).toMutableMap()

            7, 8 -> mapOf(
                2 to Ability.InvestigatePartyMembership,
                3 to Ability.SelectTheNextPresidentialCandidate,
                4 to Ability.ExecutePlayer,
                5 to Ability.ExecutePlayer
            ).toMutableMap()

            else -> mapOf(
                1 to Ability.InvestigatePartyMembership,
                2 to Ability.InvestigatePartyMembership,
                3 to Ability.SelectTheNextPresidentialCandidate,
                4 to Ability.ExecutePlayer,
                5 to Ability.ExecutePlayer
            ).toMutableMap()
        }

        this.game.sendServerMessage(president.username + " was randomly chosen as the first president.")

        president.state.availablePlayerTargets = this.eligibleChancellorCandidates(state)
        president.propagatePrivateState()

        return state
    }

    private fun eligibleChancellorCandidates(state: GameState): List<String>
    {
        return state.players
            .filter {
                it.alive &&
                    it != state.government.president &&
                    it != state.lastGovernment.president &&
                    (
                        state.players.filter { player -> player.alive }.size <= 5 ||
                            it != state.lastGovernment.chancellor
                        )
            }
            .map { it.username }
    }

    private fun movePresidentialCandidacyToNextPlayer(state: GameState): GameState
    {
        val players = state.players.filter { it.alive }
        val president = players[(players.indexOf(state.government.president) + 1) % players.size]

        state.lastGovernment = state.government
        state.government = Government(president, null)
        state.phase = RoundPhase.PresidentIsSelectingTheNextChancellorCandidate
        state.chancellorHasClaimed = false
        state.presidentHasClaimed = false

        president.state.availablePlayerTargets = this.eligibleChancellorCandidates(state)
        president.propagatePrivateState()

        return state
    }

    private fun checkIfAnyPartyHasWonTheGame(state: GameState): GameState
    {
        if (!state.started) return state

        val hitler = state.players.find { it.role == Role.Hitler }
        var won = false
        var message = ""

        if (
            state.enactedPolicies.fascist == 6 ||
            (
                state.enactedPolicies.fascist >= 3 &&
                    state.government.chancellor == hitler &&
                    state.phase == RoundPhase.PresidentIsSelectingAPolicyToDiscard
                )
        )
        {
            message = if (state.enactedPolicies.fascist == 6) "Sixth fascist policy has been enacted"
            else "Hitler was elected as a chancellor after at least three fascist policies have been enacted"

            state.winners = Party.Fascist
            won = true
        }

        if (state.enactedPolicies.liberal == 5 || !hitler!!.alive)
        {
            message = if (state.enactedPolicies.liberal == 5) "Fifth liberal policy has been enacted"
            else "Hitler was executed"

            state.winners = Party.Liberal
            won = true
        }

        if (won)
        {
            val uncoveredRoles = state.players.map { it.username to it.role }.toMap().toMutableMap()

            // Reveal roles to all players
            for (player in state.players)
            {
                player.state.assignedRoles = uncoveredRoles
                player.state.availableClaims = emptyList()
                player.state.availablePolicies = emptyList()
                player.state.availablePlayerTargets = emptyList()
                player.propagatePrivateState()
            }

            this.game.sendServerMessage(
                message,
                mapOf("party" to state.winners as Any)
            )
        }

        return state
    }
}