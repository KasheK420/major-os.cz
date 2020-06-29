package dev.vrba.secrethitler.game.state

import dev.vrba.secrethitler.game.Player
import dev.vrba.secrethitler.game.wrappers.*

data class GameState
(
    var started: Boolean = false,
    var players: MutableList<Player> = emptyList<Player>().toMutableList(),
    var phase: RoundPhase = RoundPhase.PreparingTheGame,
    var government: Government = Government(null, null),
    var lastGovernment: Government = Government(null, null),
    var enactedPolicies: EnactedPolicies = EnactedPolicies(0, 0),
    var drawingPile: MutableList<Party> = createDrawingPile(),
    var discardPile: MutableList<Party> = emptyList<Party>().toMutableList(),
    var policiesPool: MutableList<Party> = emptyList<Party>().toMutableList(),
    var electionCounter: Int = 0,
    var votes: MutableMap<String, Vote> = emptyMap<String, Vote>().toMutableMap(),
    var showVotes: Boolean = false,
    var presidentialAbilities: MutableMap<Int, Ability> = emptyMap<Int, Ability>().toMutableMap(),
    var abilitiesUsed: Int = 0,
    var presidentHasClaimed: Boolean = false,
    var chancellorHasClaimed: Boolean = false,
    var winners: Party? = null
)
{
    data class PublicRepresentation
    (
        val started: Boolean = false,
        val players: List<Player.PublicRepresentation>,
        val phase: String,
        val government: Government.PublicRepresentation,
        val lastGovernment: Government.PublicRepresentation,
        val enactedPolicies: EnactedPolicies,
        val drawingPileCards: Int,
        val discardPileCards: Int,
        val electionCounter: Int,
        val votes: Map<String, Vote>,
        val showVotes: Boolean,
        val presidentialAbilities: Map<Int, Ability>,
        val winners: Party?
    )

    fun toPublicRepresentation() = PublicRepresentation(
        this.started,
        this.players.map { it.toPublicRepresentation() },
        this.phase.toString(),
        this.government.toPublicRepresentation(),
        this.lastGovernment.toPublicRepresentation(),
        this.enactedPolicies,
        this.drawingPile.size,
        this.discardPile.size,
        this.electionCounter,
        // If everybody has voted, show the votes
        if (this.votes.keys.size == this.players.count {it.alive}) this.votes
        else this.votes.map { it.key to Vote.Hidden }.toMap(),
        this.showVotes,
        this.presidentialAbilities,
        this.winners
    )

    companion object
    {
        // Create a deck containing 6 liberal and 11 fascist policies
        private fun createDrawingPile(): MutableList<Party>
        {
            val drawingPile: MutableList<Party> = emptyList<Party>().toMutableList()

            drawingPile.addAll(generateSequence { Party.Liberal }.take(6))
            drawingPile.addAll(generateSequence { Party.Fascist }.take(11))

            return drawingPile.shuffled().toMutableList()
        }
    }
}
