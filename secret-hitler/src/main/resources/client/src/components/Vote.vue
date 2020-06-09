<template>
    <div class="flex flex-row mx-5" v-if="display">
        <div :class="'border-2 bg-gray-800 p-5 rounded-lg flex flex-row items-center ' +
            (currentVote === null ? 'pulse border-white' : 'border-gray-800 has-selected-card')">
            <img :src="images.ja" alt="ja" :class="'card m-2 border-4 border-transparent rounded-lg hover:border-white ' + (
                 currentVote === 'ja' ? 'border-white bg-white shadow selected' : '' )" @click="vote('ja')">

            <img :src="images.nein" alt="nein" :class="'card m-2 border-4 border-transparent rounded-lg hover:border-white ' + (
                 currentVote === 'nein' ? 'shadow border-white bg-white selected' : '' )" @click="vote('nein')">
        </div>
    </div>
</template>
<style scoped>
    img {
        width: 100px;
        cursor: pointer;
        transition: 0.3s all;
    }

    .has-selected-card .card {
        opacity: 0.5;
    }

    .pulse {
        animation: pulse infinite ease-in-out 1.5s;
    }

    .selected {
        opacity: 1 !important;
        transform: scale(1.1);
    }

    @keyframes pulse {
        from {
            box-shadow: 0 0 0 0 rgba(255, 255, 255, 1);
        }
        to {
            box-shadow: 0 0 0 30px rgba(255, 255, 255, 0);
        }
    }
</style>
<script>
    import ja from "../assets/ballot/ja.png"
    import nein from "../assets/ballot/nein.png"

    export default {
        name: "Vote",
        data: () => ({
            currentVote: null,
            images: {
                ja: ja,
                nein: nein,
            }
        }),
        mounted() {
            this.currentVote = null;
        },
        methods: {
            vote(value) {
                this.currentVote = value;
                this.$socket.sendObj({
                    type: "vote",
                    value: value
                })
            }
        },
        watch: {
            // Reset the locally stored vote on every game phase change
            phase: function () {
                this.currentVote = null
            }
        },
        computed: {
            phase() {
                return this.$store.state.public_state.phase
            },
            display() {
                return this.$store.state.public_state.phase === "PlayersAreVotingForTheNewGovernment" &&
                    Object.keys(this.$store.state.public_state.votes).length <
                    this.$store.state.public_state.players.filter(player => player.alive).length;
            },
        }
    }
</script>