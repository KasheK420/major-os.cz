<template>
    <div class="flex flex-col items-stretch bg-gray-800 p-3 shadow rounded-lg">
        <div class="flex-1 overflow-y-scroll chat-overflow" style="height: 70vh; max-height: 70vh"
             v-autoscroll="'bottom'">
            <div v-for="(message, i) in $store.state.chat" :key="i">
                <div :class="'m-1 mb-2 mr-3 p-2 px-3 rounded ' + messageColor(message)">
                    <div class="text-gray-500 text-sm" v-if="!message.server">{{ message.sender }}</div>
                    <div class="text-gray-200 flex items-center">
                        {{ message.content }}
                        <div class="inline-block flex items-center" v-if="message.claim">
                            <div v-for="(policy, i) in message.claim.options" :key="i" :class="'claim rounded-full ' + (policy === 'Fascist' ? 'bg-red-800' : 'bg-blue-800')">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="my-4 border-t-2 border-gray-700"></div>
        <input type="text"
               class="w-full px-2 py-3 bg-gray-700 hover:bg-gray-900 text-gray-100 border-gray-700 border-2 hover:border-gray-500 rounded-lg shadow"
               style="outline: none !important" placeholder="Chat message..."
               v-model="message" @keyup.enter="send">
    </div>
</template>
<style>
    .chat-overflow {
        overflow: invisible;
        --webkit--scrollbar-color: dark;
    }

    .claim {
        display: inline-block;
        width: 15px;
        height: 15px;
        margin-left: 5px;
    }
</style>
<script>
    import fascist from "../assets/abilities/fascist.svg"
    import liberal from "../assets/abilities/liberal.svg"

    export default {
        name: "Chat",
        data: () => ({
            message: "",
            images: {
                fascist: fascist,
                liberal: liberal,
            }
        }),
        methods: {
            send() {
                if (this.message.trim().length !== 0) {
                    this.$socket.sendObj({
                        type: "chat_message",
                        message: this.message
                    })

                    this.message = ""
                }
            },
            messageColor(message) {
                if (message.server) {
                    if (typeof message.party !== "undefined") {
                        const color = message.party === "fascist" ? 'red' : 'blue';
                        return `border-0 border-l-4 border-${color}-300 bg-${color}-900 text-${color}-400`;
                    }

                    return 'bg-gray-900 border-0 border-l-4 border-gray-500';
                }

                return 'bg-gray-700';
            }
        }
    }
</script>