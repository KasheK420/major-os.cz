<template>
    <div class="flex flex-col items-center bg-gray-900 justify-center text-center pt-3" style="min-height:100vh;">
        <img :src="logo" alt="Secret Hitler logo" class="block mx-auto" width="300px">
        <div v-if="(this.$store.state.public_state.players || []).length === 0 " class="text-4xl mb-4 text-gray-700 mt-4">
            There are no connected players yet!
        </div>
        <div class="my-8 w-1/2 mx-auto grid grid-cols-5">
            <div v-for="(player, i) in $store.state.public_state.players" :key="i" class="d-flex flex-col justify-center items-center">
                <div :class="'border-2 rounded-lg border-transparent py-10 m-2 text-lg ' +
    (player.ready ? 'bg-green-900 text-green-500 border-green-500' : 'bg-gray-800 text-gray-600') ">{{ player.username }}</div>
            </div>
            <div v-for="i in 10 - ($store.state.public_state.players || []).length" :key="10 - i" class="d-flex flex-col justify-center items-center">
                <div class="border-2 rounded-lg border-gray-800 border-dashed py-10 text-transparent m-2 text-lg">?</div>
            </div>
        </div>

        <hr v-if="(this.$store.state.public_state.players || []).length >= 5 && this.$store.state.private_state !== null">
        <div v-if="(this.$store.state.public_state.players || []).length >= 5 && this.$store.state.private_state !== null" class="my-8">
            <button @click="ready" class="text-3xl bg-green-500 border-2 border-green-500 py-3 px-10 text-green-300 rounded-lg hover:bg-green-800 hover:bg-text-green-500">Ready</button>
        </div>

        <!-- If the private state is null, it indicates, that the player has not joined the game yet -->
        <hr>
        <div v-if="this.$store.state.private_state === null" class="d-flex flex-col">
            <div class="text-3xl mt-5 text-gray-200">Join the game</div>
            <div class="my-2 mt-5">
                <input type="text" v-model="username"
                        class="shadow-lg p-3 outline-none bg-gray-800 text-gray-400 focus:text-gray-200 hover:text-gray-200 rounded-lg hover:bg-gray-700 focus:bg-gray-700 rounded-r-none" placeholder="username" autofocus @keyup.enter="join">
                <button class="py-3 px-5 bg-teal-800 text-teal-300 rounded-lg rounded-l-none outline-none shadow-lg hover:bg-teal-700 focus:bg-teal-700 border-l-4 border-teal-400"
                        style="outline: none !important"
                        @click="join">Connect
                </button>
            </div>
        </div>
        <div v-else class="flex flex-col">
            <div class="text-3xl my-5 text-teal-300">Connected</div>
            <div class="my-2">
                <button class="py-3 px-5 bg-gray-600 rounded-lg text-gray-800 border-gray-800 transition-all hover:bg-gray-300 outline-none"
                        style="outline: none !important;"
                        @click="leave">Disconnect
                </button>
            </div>
        </div>
        <hr class="my-10">
        <div class="text-center">
            <a href="https://www.gitlab.com/jirkavrba/secret-hitler" target="_blank" class="text-teal-800 hover:text-teal-400">Gitlab repository</a>
        </div>
    </div>
</template>
<script>
    import logo from "../assets/logo.png";

    export default {
        name: "Lobby",
        data: () => ({
            username: "", // TODO: random username?
            logo: logo,
        }),
        methods: {
            join() {
                if (this.username.trim().length > 0) {
                    this.$socket.sendObj({
                        type: "join_game",
                        username: this.username,
                    });
                }
            },
            leave() {
                this.$socket.sendObj({
                    type: "leave_game"
                })
                this.$store.commit("CLEAR_PRIVATE_STATE")
            },
            ready() {
                this.$socket.sendObj({
                    type: "ready"
                })
            }
        },
    }
</script>