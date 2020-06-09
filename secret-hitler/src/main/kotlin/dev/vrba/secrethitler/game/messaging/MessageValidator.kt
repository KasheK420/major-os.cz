package dev.vrba.secrethitler.game.messaging

import dev.vrba.secrethitler.game.Player
import dev.vrba.secrethitler.game.exceptions.MessageValidationException
import dev.vrba.secrethitler.game.state.GameState
import dev.vrba.secrethitler.game.wrappers.RoundPhase
import io.javalin.websocket.WsContext
import io.javalin.websocket.WsMessageContext
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException

class MessageValidator
{
    @Throws(MessageValidationException::class)
    fun validate(socket: WsMessageContext, state: GameState): JSONObject
    {
        try
        {
            val parser = JSONParser()
            val content: String = socket.message()

            // Message must be a valid json
            val message = parser.parse(content) as JSONObject
            val messageType = message["type"]

            // The only exception for validating whether the message is from a game participant
            // is the JOIN_GAME message which cannot be from a game participant by design
            if (messageType != "join_game" && !this.isFromGameParticipant(socket, state))
            {
                throw MessageValidationException("The message must be from a game participant.")
            }

            if (messageType == null)
            {
                throw MessageValidationException("The given message does not contain a valid message type.")
            }

            val sender = state.players.find { it.socket.sessionId == socket.sessionId }

            when (messageType)
            {
                "join_game" ->
                {
                    if (state.started)
                        throw MessageValidationException("The game already started.")

                    if (message["username"] == null || (message["username"] as String).length == 0)
                        throw MessageValidationException("Invalid or missing username.")

                    if ((message["username"] as String).length > 20)
                        throw MessageValidationException("Username shouldn't be longer than 20 characters.")

                    if (state.players.size >= 10)
                        throw MessageValidationException("There are no empty seats.")


                    if (sender is Player)
                        throw MessageValidationException("This websocket session is already in use by another player. WTF?")


                    if (state.players.any { it.username == message["username"] })
                        throw MessageValidationException("That username is already taken.")
                }

                "leave_game" ->
                {
                    if (state.started)
                        throw MessageValidationException("The game already started.")

                    if (sender !is Player)
                        throw MessageValidationException("The player has not joined the game yet")
                }

                "ready" ->
                {
                    if (state.started)
                        throw MessageValidationException("The game has already started")

                    if (state.players.size < 5)
                        throw MessageValidationException("There needs to be at least 5 players to be able to start the game")
                }

                "select_player" ->
                {
                    if (!state.started)
                        throw MessageValidationException("The game has not started yet")

                    if (message["username"] === null || !state.players.map { it.username }.contains(message["username"] as String))
                        throw MessageValidationException("Invalid username sent in the message payload")

                    if (state.government.president !== sender)
                        throw MessageValidationException("Only president can send this message type")

                    val allowedPhases = listOf(
                        RoundPhase.PresidentIsSelectingTheNextChancellorCandidate,
                        RoundPhase.PresidentIsSelectingTheNextPresidentialCandidate,
                        RoundPhase.PresidentIsSelectingPlayerForExecution,
                        RoundPhase.PresidentIsSelectingPlayerForInvestigation
                    )

                    if (!allowedPhases.contains(state.phase))
                        throw MessageValidationException("The message was sent in an invalid round phase")

                    if (!state.government.president?.state?.availablePlayerTargets?.contains(message["username"] as String)!!)
                        throw MessageValidationException("The selected username is not a valid username")
                }

                "chat_message" ->
                {
                    if (!state.started)
                        throw MessageValidationException("The game has not started yet")

                    if (message["message"] == null || (message["message"] as String).trim().isEmpty())
                        throw MessageValidationException("The message cannot be empty")
                }

                "vote" ->
                {
                    if (!state.started)
                        throw MessageValidationException("The game has not started yet")

                    if (sender?.alive == false)
                        throw MessageValidationException("Players must be alive in order to be able to vote.")

                    if (state.phase != RoundPhase.PlayersAreVotingForTheNewGovernment)
                        throw MessageValidationException("Invalid game phase, cannot vote now")

                    if (state.votes.size == state.players.filter { it.alive }.size)
                        throw MessageValidationException("All players have already voted")

                    if (message["value"] == null || !listOf("ja", "nein").contains(message["value"] as String))
                        throw MessageValidationException("The value must be either 'ja' or 'nein'")
                }

                "discard_policy" ->
                {
                    if (!state.started)
                        throw MessageValidationException("The game has not started yet.")

                    val allowedPhases = listOf(
                        RoundPhase.PresidentIsSelectingAPolicyToDiscard,
                        RoundPhase.ChancellorIsSelectingAPolicyToDiscard
                    )

                    if (!allowedPhases.contains(state.phase))
                        throw MessageValidationException("Message sent during an incorrect game phase")

                    if (state.phase == RoundPhase.PresidentIsSelectingAPolicyToDiscard && state.government.president != sender)
                        throw MessageValidationException("The message is expected to be from the president")

                    if (state.phase == RoundPhase.ChancellorIsSelectingAPolicyToDiscard && state.government.chancellor != sender)
                        throw MessageValidationException("The message is expected to be from the chancellor")

                    if (!listOf("Fascist", "Liberal").contains(message["value"]))
                        throw MessageValidationException("Invalid policy argument")

                    if (!state.policiesPool.map { it.toString() }.contains(message["value"]))
                        throw MessageValidationException("That type of policy is not contained in the policy pool")
                }

                "claim" ->
                {
                    if (state.phase != RoundPhase.WaitingForGovernmentClaims)
                        throw MessageValidationException("Claim sent during an invalid phase.")

                    if (sender != state.government.president && sender != state.government.chancellor)
                        throw MessageValidationException("Claim must be either from president or chancellor")

                    if (sender == state.government.chancellor && state.chancellorHasClaimed)
                        throw MessageValidationException("The chancellor has already claimed something")

                    if (sender == state.government.president && state.presidentHasClaimed)
                        throw MessageValidationException("The president has already claimed something")

                    // Null indicates that the user has skipped the claim
                    if (message["value"] != null && message["value"] !is Long)
                    {
                        throw MessageValidationException("The message property value has an invalid type")
                    }

                    if (message["value"] != null && !(0L..3L).contains(message["value"]))
                    {
                        throw MessageValidationException("The message property value is invalid")
                    }
                }

                "veto" ->
                {
                        if (state.phase != RoundPhase.ChancellorIsSelectingAPolicyToDiscard)
                            throw MessageValidationException("Veto sent during an invalid phase.")

                        if (state.enactedPolicies.fascist < 5)
                            throw MessageValidationException("At least 5 fascist policies must be enacted.")

                        if (sender != state.government.chancellor)
                            throw MessageValidationException("The sender must be chancellor")
                }

                "acknowledge" ->
                {
                    val allowedPhases = listOf(
                        RoundPhase.PresidentIsLookingAtTheTopThreePolicies,
                        RoundPhase.PresidentIsInvestigatingLoyalty
                    )

                    if (!allowedPhases.contains(state.phase))
                        throw MessageValidationException("Acknowledge sent during an invalid phase.")

                    if (state.government.president != sender)
                        throw MessageValidationException("The acknowledgment must be sent by the president")
                }

                else -> throw MessageValidationException("Unknown message type")

            }

            return message
        }
        catch (_: ParseException)
        {
            throw MessageValidationException("Message is not a valid JSON")
        }
    }

    private fun isFromGameParticipant(socket: WsContext, state: GameState): Boolean =
        state.players.any { it.socket.sessionId == socket.sessionId }
}