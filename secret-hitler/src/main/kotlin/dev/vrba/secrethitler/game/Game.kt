package dev.vrba.secrethitler.game

import com.fasterxml.jackson.databind.ObjectMapper
import dev.vrba.secrethitler.game.exceptions.MessageValidationException
import dev.vrba.secrethitler.game.messaging.MessageExecutor
import dev.vrba.secrethitler.game.messaging.MessageValidator
import dev.vrba.secrethitler.game.state.GameState
import io.javalin.websocket.WsContext
import io.javalin.websocket.WsMessageContext

class Game
{
    private var state = GameState()
    private var clients: MutableList<WsContext> = emptyList<WsContext>().toMutableList()
    private val validator = MessageValidator()
    private val executor = MessageExecutor(this)

    fun reset()
    {
        this.clients.clear()
        this.state = GameState()
    }

    fun connect(client: WsContext)
    {
        this.clients.add(client)
        this.broadcastPublicState()
    }

    fun disconnect(client: WsContext)
    {
        val player = this.state.players.find { it.socket.sessionId == client.sessionId }

        if (player is Player)
        {
            this.state.players.remove(player)
        }

        // Reset game if a participant has left
        if (player is Player && this.state.started)
        {
            try
            {
                this.clients.forEach { it.session.close() }
                this.clients.clear()
            }
            catch (e: ConcurrentModificationException)
            {
                // This doesn't need to be handled as the connection is closed by the receiver anyway
            }

            this.reset()
        }

        this.clients.remove(client)
        this.broadcastPublicState()
    }

    fun handleMessage(context: WsMessageContext)
    {
        try
        {
            val parsedMessage = this.validator.validate(context, this.state)

            if (parsedMessage["type"] == "chat_message")
            {
                this.broadcastChatMessage(context, parsedMessage["message"] as String)
                return
            }

            this.state = this.executor.execute(parsedMessage, context, this.state)

            this.broadcastPublicState()
            this.broadcastPrivateStates()
        }
        catch (_ : ConcurrentModificationException) {}
        catch (exception: MessageValidationException)
        {
            val reply = emptyMap<String, String>().toMutableMap()

            reply["type"] = "error"
            reply["message"] = exception.message ?: "Unexpected validation exception caught"

            context.send(ObjectMapper().writeValueAsString(reply))
        }
    }

    private fun broadcastPublicState()
    {
        val state = this.state.toPublicRepresentation()
        val message = emptyMap<String, Any>().toMutableMap()

        message["type"] = "public_state_updated"
        message["state"] = state

        val json = ObjectMapper().writeValueAsString(message)

        for (client in this.clients)
        {
            client.send(json)
        }
    }

    private fun broadcastPrivateStates()
    {
        for (player in this.state.players)
        {
            player.propagatePrivateState()
        }
    }

    private fun broadcastChatMessage(context: WsMessageContext, content: String)
    {
        if (content.length > 80)
        {
            throw MessageValidationException("Message should not be longer than 80 chars. Because reasons.")
        }

        val username = this.state.players.find { it.socket.sessionId == context.sessionId }?.username ?: "<~>"
        val message = emptyMap<String, Any>().toMutableMap()

        message["type"] = "chat_message"
        message["sender"] = username
        message["content"] = content

        val json = ObjectMapper().writeValueAsString(message)

        for (player in this.state.players)
        {
            player.socket.send(json)
        }
    }

    fun sendServerMessage(content: String, attributes: Map<String, Any> = emptyMap())
    {
        val message = emptyMap<String, Any>().toMutableMap()

        message["type"] = "chat_message"
        message["sender"] = "Server"
        message["server"] = true
        message["content"] = content

        for ((key, value) in attributes)
        {
            message[key] = value
        }

        val json = ObjectMapper().writeValueAsString(message)

        for (player in this.state.players)
        {
            player.socket.send(json)
        }
    }
}