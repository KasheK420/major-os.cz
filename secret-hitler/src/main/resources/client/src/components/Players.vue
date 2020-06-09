<template>
    <div :class="'flex flex-col items-stretch p-3 shadow rounded-lg ' +
        (($store.state.private_state.availablePlayerTargets || []).length > 0 ? ' pulse bg-gray-700' : 'bg-gray-800')">
        <div v-for="(player, i) in $store.state.public_state.players" :key="i" :class="player.alive ? '' : 'dead'">
            <div :class="'my-2 p-3 flex flex-row items-center rounded-lg border-2 ' +
                (($store.state.private_state.availablePlayerTargets || []).includes(player.username) ?
                ('border-gray-900 bg-gray-900 hover:shadow cursor-pointer ' +
                    (
                        $store.state.public_state.phase === 'PresidentIsSelectingPlayerForExecution'
                        ? 'hover:bg-red-900 hover:border-red-500'
                        : 'hover:bg-gray-800 hover:border-gray-200')
                    )
                : 'border-transparent bg-gray-800')"
                 @click="select(player.username)"
            >
                <div class="mr-1" v-if="$store.state.public_state.showVotes">
                    <img :src="ballot[$store.state.public_state.votes[player.username]] || ballot.default"
                         alt="Ballot card" class="rounded mr-2 ballot">
                </div>
                <img class="ml-2 mb-1 text-sm m-2 border-4 border-gray-600 rounded-full"
                     :src="roles[$store.state.private_state.assignedRoles[player.username]]" width="50px">
                <div class="flex flex-col mr-5 flex-1">
                    <div class="ml-1 mb-0 p-1 text-sm text-gray-200">{{ player.username }}</div>
                    <div :class="'ml-2 mb-1 text-sm uppercase ' +
                      ($store.state.private_state.assignedRoles[player.username] !== 'Hidden' ? 'text-teal-300 ' : 'text-gray-600')">
                        {{
                        $store.state.private_state.assignedRoles[player.username] }}
                    </div>
                </div>
                <div class="flex flex-col justify-center items-center">
                    <div v-if="$store.state.public_state.lastGovernment.president === player.username"
                         class="border-2 text-sm border-gray-700 inline-block rounded-lg py-1 px-2 text-gray-700 my-1">
                        President
                    </div>
                    <div v-if="$store.state.public_state.lastGovernment.chancellor === player.username"
                         class="border-2 text-sm border-gray-700 inline-block rounded-lg py-1 px-2 text-gray-700 my-1">
                        Chancellor
                    </div>
                    <div v-if="$store.state.public_state.government.president === player.username"
                         class="border-teal-800 border-2 text-sm bg-teal-800 inline-block rounded-lg py-1 px-2 text-teal-400 my-1">
                        President
                    </div>
                    <div v-if="$store.state.public_state.government.chancellor === player.username"
                         class="border-purple-800 border-2 text-sm bg-purple-800 inline-block rounded-lg py-1 px-2 text-purple-400 my-1">
                        Chancellor
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<style scoped>
    img.ballot {
        width: 35px;
    }

    .dead {
        opacity: 0.5;
        filter: grayscale(100%) blur(1px);
    }

    .pulse {
        animation: pulse infinite ease-in-out 1.5s;
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
    import hidden from "./../assets/roles/hidden.png"
    import hitler from "./../assets/roles/hitler.png"
    import fascist from "./../assets/roles/fascist.png"
    import liberal from "./../assets/roles/liberal.png"

    import notVoted from "../assets/ballot/not-voted.png"
    import hidden_ from "../assets/ballot/hidden.png"
    import ja from "../assets/ballot/ja.png"
    import nein from "../assets/ballot/nein.png"


    export default {
        name: "Players",
        data: () => ({
            roles: {
                Hidden: hidden,
                Hitler: hitler,
                Fascist: fascist,
                Liberal: liberal,
            },
            ballot: {
                Hidden: hidden_,
                Ja: ja,
                Nein: nein,
                default: notVoted
            }
        }),
        methods: {
            select(player) {
                if (this.$store.state.private_state.availablePlayerTargets.includes(player)) {
                    this.$socket.sendObj({
                        type: "select_player",
                        username: player,
                    })
                }
            }
        }
    }
</script>