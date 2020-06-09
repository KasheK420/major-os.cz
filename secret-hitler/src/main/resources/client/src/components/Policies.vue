<template>
    <div class="flex flex-row mx-5" v-if="($store.state.private_state.availablePolicies || []).length > 0">
        <div class="border-2 bg-gray-800 p-5 rounded-lg flex flex-row items-center pulse border-white">
            <div class="wrapper cursor-pointer"
                 v-for="(policy, i) in $store.state.private_state.availablePolicies"
                 :style="'background-image: url(' + images.cross + ')'"
                 :key="i" @click="discard(policy)">
                <img :src="images[policy]" class="card bg-white m-2 border-4 border-transparent rounded-lg hover:border-white">
            </div>
            <div class="ml-3" v-if="$store.state.private_state.canUseVeto">
                <div class="bg-red-700 rounded shadow px-4 py-2 text-white hover:bg-red-800 cursor-pointer"
                     @click="veto"
                >VETO</div>
            </div>
        </div>
    </div>
</template>
<style scoped>
    img {
        width: 100px;
        transition: 0.3s all;
    }

    .wrapper {
        background-size: 50px 50px;
        background-repeat: no-repeat;
        background-position: center center;
    }

    .wrapper:hover > img.card {
        transform: scale(0.8);
        filter: grayscale(100%);
        opacity: 0.2;
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
    import liberal from "../assets/policies/liberal.png"
    import fascist from "../assets/policies/fascist.png"
    import cross from "../assets/policies/cross.svg"

    export default {
        name: "Policies",
        data: () => ({
            images: {
                Liberal: liberal,
                Fascist: fascist,
                cross: cross
            }
        }),
        methods: {
            discard(policy) {
                this.$socket.sendObj({
                    type: "discard_policy",
                    value: policy
                })
            },
            veto() {
                this.$socket.sendObj({
                    type: "veto"
                })
            }
        }
    }
</script>

