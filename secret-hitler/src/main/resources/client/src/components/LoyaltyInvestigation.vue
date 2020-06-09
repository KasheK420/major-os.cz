<template>
    <div v-if="display">
        <div class="investigation flex flex-col justify-center items-center">
            <div class="bg-gray-800 text-gray-500 p-10 rounded-lg shadow-xl text-center">
                <h1 class="text-2xl text-gray-200">{{ username }}</h1>
                <p>is member of the</p>
                <h1 :class="'text-3xl ' + (party === 'Fascist' ? 'text-red-400' : 'text-blue-400')">{{ party }}</h1>
                <p>party</p>

                <button @click="acknowledge" class="text-gray-300 block bg-gray-700 mt-5 py-2 px-4 mx-auto hover:bg-gray-600 rounded-lg">Continue</button>
            </div>
        </div>
    </div>
</template>
<style>
    .investigation {
        position: fixed;
        top: 0;
        left: 0;
        width: 100vw;
        height: 100vh;
        z-index: 10;
        background: rgba(0, 0, 0, 0.3)
    }

    button {
        outline: none !important;
    }
</style>
<script>
    export default {
        name: "LoyaltyInvestigation",
        methods: {
            acknowledge() {
                this.$socket.sendObj({
                    type: "acknowledge"
                })
            }
        },
        computed: {
            display() {
                return this.$store.state.private_state.investigatedPartyMembership !== null
            },
            username() {
                return this.$store.state.private_state.investigatedPartyMembership.first
            },
            party() {
                return this.$store.state.private_state.investigatedPartyMembership.second
            }
        }
    }
</script>