<template>
    <div class="flex flex-col justify-between items-stretch bg-red">
        <div class="m-5 mb-0 rounded-b-none p-5 bg-blue-900 flex flex-row justify-center rounded-lg border-b-4 border-gray-800">
            <div v-for="i in 5" :key="i" class="flex items-center justify-center">
                <div class="m-2 policy" v-if="$store.state.public_state.enactedPolicies.liberal >= i">
                    <img :src="images.liberal" alt="Liberal policy" class="border-2 border-blue-400 rounded-lg">
                </div>
                <div class="p-2 m-2 policy border-2 rounded-lg bg-blue-900 border-dashed border-blue-400 flex justify-center items-center" v-else>
                    <div v-if="i === 5">
                        <img :src="abilities.liberal" title="Liberals will win the game">
                    </div>
                </div>
            </div>
        </div>
        <div class="m-5 mt-0 p-5 bg-red-900 rounded-t-none flex flex-row justify-center rounded-lg">
            <div v-for="i in 6" :key="i" class="flex items-center justify-center">
                <div class="m-2 policy" v-if="$store.state.public_state.enactedPolicies.fascist >= i">
                    <img :src="images.fascist" alt="Fascist policy" class="border-2 border-red-400 rounded-lg">
                </div>
                <div class="m-2 p-2 policy border-2 rounded-lg bg-red-900 border-dashed border-red-400 flex justify-center items-center" v-else>
                    <div v-if="$store.state.public_state.presidentialAbilities[i] === 'ExamineTopThreePolicies'">
                        <img :src="abilities.peek" title="Look at the top three policies">
                    </div>
                    <div v-if="$store.state.public_state.presidentialAbilities[i] === 'InvestigatePartyMembership'">
                        <img :src="abilities.investigate" title="Investigate party membership of a player">
                    </div>
                    <div v-if="$store.state.public_state.presidentialAbilities[i] === 'SelectTheNextPresidentialCandidate'">
                        <img :src="abilities.candidate" title="Select the next presidential candidate">
                    </div>
                    <div v-if="$store.state.public_state.presidentialAbilities[i] === 'ExecutePlayer'">
                        <img :src="abilities.execute" title="Execute a player">
                    </div>
                    <div v-if="i === 6">
                        <img :src="abilities.fascist" title="Fascists will win the game">
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<style>
    .policy {
        width: 80px;
        height: 114px;
    }
</style>
<script>
    import fascist from "../assets/policies/fascist.png"
    import liberal from "../assets/policies/liberal.png"
    import investigate from "../assets/abilities/investigate.svg"
    import execute from "../assets/abilities/execute.svg"
    import candidate from "../assets/abilities/candidate.svg"
    import peek from "../assets/abilities/peek.svg"
    import fascistWin from "../assets/abilities/fascist.svg"
    import liberalWin from "../assets/abilities/liberal.svg"

    export default {
        name: "EnactedPolicies",
        data: () => ({
            images: {
                fascist: fascist,
                liberal: liberal,
            },
            abilities: {
                investigate: investigate,
                execute: execute,
                candidate: candidate,
                peek: peek,
                fascist: fascistWin,
                liberal: liberalWin,
            }
        })
    }
</script>
