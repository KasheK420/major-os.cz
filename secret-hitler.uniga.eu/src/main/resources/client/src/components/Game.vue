<template>
    <div>
        <div v-if="$store.state.public_state.started && $store.state.private_state === null">
            <div style="position: fixed; width: 100vw; height: 100vh;" class="flex flex-col justify-center items-center bg-gray-900">

                <h1 class="text-5xl text-gray-500">The game already started</h1>
                <p class="text-gray-700 text-xl mt-4">Observer mode not implemented yet</p>
            </div>
        </div>
        <div :class="$store.state.public_state.winners === null ? '' : 'blur'" v-else>
            <div class="flex flex-col justify-start items-stretch bg-gray-900" style="width: 100vw; min-height: 100vh;">
                <img :src="logo" alt="Secret Hitler logo" width="100px" class="mt-5 ml-10">
                <div class="grid grid-cols-12 m-10">
                    <Players class="col-span-3"/>
                    <div class="flex flex-col col-span-5 justify-start">
                        <div class="flex flex-row justify-between items-center">
                            <ElectionCounter/>
                            <GamePhase/>
                        </div>

                        <EnactedPolicies/>

                        <div class="flex flex-row justify-between">
                            <Vote/>
                            <Policies/>
                            <Claims/>
                            <Winners/>

                            <!-- This will force the card piles to align to the right, if no other control elements are shown -->
                            <div/>

                            <CardPiles/>
                        </div>
                    </div>
                    <Chat class="col-span-4"/>
                </div>
            </div>
        </div>
        <PoliciesPeek/>
        <LoyaltyInvestigation/>
    </div>
</template>
<style>
</style>
<script>
    import logo from "../assets/logo.png"
    import Players from "./Players";
    import Chat from "./Chat";
    import EnactedPolicies from "./EnactedPolicies";
    import ElectionCounter from "./ElectionCounter";
    import GamePhase from "./GamePhase";
    import Vote from "./Vote";
    import Policies from "./Policies";
    import CardPiles from "./CardPiles";
    import Claims from "./Claims";
    import Winners from "./Winners";
    import PoliciesPeek from "./PoliciesPeek";
    import LoyaltyInvestigation from "./LoyaltyInvestigation";

    export default {
        name: "Game",
        components: {
            LoyaltyInvestigation,
            PoliciesPeek,
            Winners,
            Claims,
            CardPiles,
            ElectionCounter,
            Players,
            EnactedPolicies,
            Chat,
            GamePhase,
            Vote,
            Policies
        },
        data: () => ({
            logo: logo
        })
    }
</script>