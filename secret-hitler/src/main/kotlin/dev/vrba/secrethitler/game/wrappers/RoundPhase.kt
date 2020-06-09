package dev.vrba.secrethitler.game.wrappers

enum class RoundPhase
{
    PreparingTheGame,
    PresidentIsSelectingTheNextPresidentialCandidate,
    PresidentIsSelectingTheNextChancellorCandidate,
    PlayersAreVotingForTheNewGovernment,
    PresidentIsSelectingAPolicyToDiscard,
    ChancellorIsSelectingAPolicyToDiscard,
    PresidentIsChoosingWhetherToVetoTheLegislativeSession,
    WaitingForGovernmentClaims,
    PresidentIsLookingAtTheTopThreePolicies,
    PresidentIsSelectingPlayerForExecution,
    PresidentIsSelectingPlayerForInvestigation,
    PresidentIsInvestigatingLoyalty
}