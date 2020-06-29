<template>
    <div class="mr-5 text-gray-300 text-lg">
        {{ phases[$store.state.public_state.phase] }}
    </div>
</template>
<script>
    export default {
        name: "GamePhase",
        data: () => ({
            phases: {
                PreparingTheGame: "Preparing the game",
                PresidentIsSelectingTheNextPresidentialCandidate: "President is selecting next presidential candidate",
                PresidentIsSelectingTheNextChancellorCandidate: "President is selecting next chancellor candidates",
                PlayersAreVotingForTheNewGovernment: "Players are voting for the new government",
                ViewingTheVotingResults: "The vote results are",
                PresidentIsSelectingAPolicyToDiscard: "President is selecting a policy to discard",
                ChancellorIsSelectingAPolicyToDiscard: "Chancellor is selecting a policy to discard",
                PresidentIsChoosingWhetherToVetoTheLegislativeSession: "President is responding to the veto offer",
                WaitingForGovernmentClaims: "Waiting for the government claims",
                PresidentIsLookingAtTheTopThreePolicies: "The president is looking at the top three policies",
                PresidentIsSelectingPlayerForExecution: "President is selecting player for execution",
                PresidentIsSelectingPlayerForInvestigation: "President is selecting player for investigation",
                PresidentIsInvestigatingLoyalty: "President is investigating loyalty",
            }
        })
    }
</script>
