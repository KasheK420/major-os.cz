package dev.vrba.secrethitler.game

import com.fasterxml.jackson.databind.ObjectMapper
import dev.vrba.secrethitler.game.state.PlayerState
import dev.vrba.secrethitler.game.wrappers.Role
import io.javalin.websocket.WsContext

data class Player
(
    val username: String,
    val socket: WsContext,
    var alive: Boolean = true,
    var role: Role = Role.Hidden,
    var state: PlayerState = PlayerState(),
    var ready: Boolean = false
)
{
    data class PublicRepresentation(val username: String, val alive: Boolean, val ready: Boolean)

    fun toPublicRepresentation() = PublicRepresentation(this.username, this.alive, this.ready)

    fun propagatePrivateState()
    {
        val message = emptyMap<String, Any>().toMutableMap()

        message["type"] = "private_state_updated"
        message["state"] = this.state

        this.socket.send(ObjectMapper().writeValueAsString(message))
    }
}
