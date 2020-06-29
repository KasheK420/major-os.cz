package dev.vrba.secrethitler.game.helpers

import dev.vrba.secrethitler.game.Player
import dev.vrba.secrethitler.game.state.GameState
import dev.vrba.secrethitler.game.wrappers.Role

object RoleAssigner
{
    fun assign(state: GameState): GameState
    {
        val pool = this.createRolesPool(state.players.size)

        // Assign the roles
        for ((index, _) in state.players.withIndex())
        {
            state.players[index].role = pool[index]
        }

        // Create personal points of view
        for ((index, player) in state.players.withIndex())
        {
            state.players[index].state.assignedRoles = createPersonalPointOfView(player, state)
        }

        return state
    }

    private fun createPersonalPointOfView(player: Player, state: GameState): MutableMap<String, Role>
    {
        val hitlerSeesFascists = state.players.size <= 6
        val players = state.players.filter { it != player }

        val view = when (player.role)
        {
            Role.Liberal -> players.map { it.username to Role.Hidden }.toMap()
            Role.Fascist -> players.map { it.username to if (it.role == Role.Liberal) Role.Hidden else it.role }.toMap()
            // Hitler sees other fascists, when there are less than 7 people playing
            Role.Hitler -> players.map { it.username to if (hitlerSeesFascists && it.role == Role.Fascist) Role.Fascist else Role.Hidden }.toMap()
            else -> emptyMap()
        }
            .toMutableMap()

        // Everybody sees his own role
        view[player.username] = player.role

        return view
    }

    private fun createRolesPool(players: Int): List<Role>
    {
        val hitler = listOf(Role.Hitler)
        val liberals = generateSequence { Role.Liberal }
        val fascists = generateSequence { Role.Fascist }

        val list = when (players)
        {
            5 -> hitler + liberals.take(3).toList() + fascists.take(1).toList()
            6 -> hitler + liberals.take(4).toList() + fascists.take(1).toList()
            7 -> hitler + liberals.take(4).toList() + fascists.take(2).toList()
            8 -> hitler + liberals.take(5).toList() + fascists.take(2).toList()
            9 -> hitler + liberals.take(5).toList() + fascists.take(3).toList()
            10 -> hitler + liberals.take(6).toList() + fascists.take(3).toList()
            else -> throw IllegalArgumentException("The number of players must be between 5 and 10")
        }

        return list.shuffled()
    }
}