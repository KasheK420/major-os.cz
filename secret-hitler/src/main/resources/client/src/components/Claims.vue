<template>
    <div class="flex flex-row mx-5" v-if="($store.state.private_state.availableClaims || []).length > 0">
        <div class="border-2 bg-gray-800 p-5 rounded-lg flex flex-row items-center pulse border-white">
            <div :class="'grid grid-cols-' + (4 - Math.floor($store.state.private_state.availableClaims.length / 2))">
                <div v-for="(_claim, i) in $store.state.private_state.availableClaims" :key="i">

                    <div class="m-2 p-1 border-gray-900 border-2 bg-gray-900 rounded-lg cursor-pointer hover:bg-gray-800 hover:border-gray-200 flex flex-row hover:shadow"
                         @click="claim(_claim.options.filter(option => option === 'Fascist').length)">
                        <div v-for="(option, i) in _claim.options" :key="i">
                            <img :src="images[option]" :alt="option" class="rounded m-1">
                        </div>
                    </div>
                </div>
            </div>
            <div class="ml-5 rounded shadow bg-gray-600 px-4 py-2 text-gray-800 cursor-pointer hover:bg-gray-500"
                 @click="claim(null)">Skip</div>
        </div>
    </div>
</template>
<style scoped>
    img {
        width: 40px;
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

    export default {
        name: "Claims",
        data: () => ({
            images: {
                Liberal: liberal,
                Fascist: fascist,
            }
        }),
        methods: {
            claim(value) {
                this.$socket.sendObj({
                    type: "claim",
                    value: value
                })
            }
        }
    }
</script>