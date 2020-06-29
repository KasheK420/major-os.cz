package dev.vrba.secrethitler.game.state

import dev.vrba.secrethitler.game.wrappers.Claim
import dev.vrba.secrethitler.game.wrappers.Party
import dev.vrba.secrethitler.game.wrappers.Role

data class PlayerState
(
    // Username -> Role
    var assignedRoles: MutableMap<String, Role> = emptyMap<String, Role>().toMutableMap(),
    var availablePolicies: List<Party> = emptyList(),
    var availableClaims: List<Claim> = emptyList(),
    var availablePlayerTargets: List<String> = emptyList(),
    var policiesPeek: List<Party> = emptyList(),
    var investigatedPartyMembership: Pair<String, Party>? = null,
    var canUseVeto: Boolean = false
)
