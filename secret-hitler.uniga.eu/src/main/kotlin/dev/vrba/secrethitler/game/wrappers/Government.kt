package dev.vrba.secrethitler.game.wrappers

import dev.vrba.secrethitler.game.Player

data class Government
(
    var president: Player?,
    var chancellor: Player?
)
{
    data class PublicRepresentation(
        var president: String?,
        var chancellor: String?
    )

    fun toPublicRepresentation()  = PublicRepresentation(this.president?.username, this.chancellor?.username)
}