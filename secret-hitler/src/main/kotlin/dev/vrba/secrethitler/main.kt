package dev.vrba.secrethitler

import dev.vrba.secrethitler.game.Game
import io.javalin.Javalin

object SecretHitler
{
    private val game = Game()
    private val server = Javalin.create {
        // Cannot enable SSL as I am a dumb shit and don't know how to setup Jetty SSL
        it.enforceSsl = false
        it.showJavalinBanner = false

        // Map resources/public as the web root
        it.addStaticFiles("/public")
    }

    fun start()
    {
        this.game.reset()
        this.server.start(8000)
        this.server.ws("/game")
        { context ->
            run {
                context.onConnect { this.game.connect(it) }
                context.onClose { this.game.disconnect(it) }
                context.onMessage { this.game.handleMessage(it) }
            }
        }
    }
}

fun main()
{
    SecretHitler.start()
}
