<template>
        <div class="policy-peek flex flex-col justify-center items-center" v-if="$store.state.private_state.policiesPeek.length !== 0">
            <div class="bg-gray-800 text-gray-500 p-10 rounded-lg shadow-xl text-center">
                <h1 class="text-gray-300 text-xl mb-3">The top 3 policies in the drawing pile are</h1>
                <div class="border-2 bg-gray-800 p-5 rounded-lg flex flex-row items-center border-white">
                    <div class="grid grid-cols-3">
                        <div v-for="(policy, i) in $store.state.private_state.policiesPeek" :key="i" class="m-2">
                            <img :src="images[policy]" class="rounded-lg m-1">
                        </div>
                    </div>
                </div>

                <button @click="acknowledge" class="text-gray-300 block bg-gray-700 mt-5 py-2 px-4 mx-auto hover:bg-gray-600 rounded-lg">Continue</button>
            </div>
        </div>
</template>
<style scoped>
    img {
        width: 90px;
    }

    .policy-peek {
        position: fixed;
        top: 0;
        left: 0;
        width: 100vw;
        height: 100vh;
        z-index: 10;
        background: rgba(0, 0, 0, 0.3)
    }

    @keyframes pulse {
        from {
            box-shadow: 0 0 0 0 rgba(255, 255, 255, 1);
        }
        to {
            box-shadow: 0 0 0 50px rgba(255, 255, 255, 0);
        }
    }
</style>
<script>
    import liberal from "../assets/policies/liberal.png";
    import fascist from "../assets/policies/fascist.png";

    export default {
        name: "PoliciesPeek",
        data: () => ({
            images: {
                Liberal: liberal,
                Fascist: fascist,
            }
        }),
        methods: {
            acknowledge() {
                this.$socket.sendObj({
                    type: "acknowledge"
                })
            }
        }
    }
</script>