import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        chat: [],
        connected: false,
        errors: [],
        public_state: {},
        private_state: null,
    },
    mutations: {
        CLEAR_PRIVATE_STATE(state) {
            state.private_state = null;
            state.chat = [];
        },
        // These mutations are handled by the internal websocket (this.$socket)
        SOCKET_ONOPEN(state) {
            state.connected = true;
        },
        SOCKET_ONMESSAGE(state, payload) {
            switch (payload.type) {
                case "public_state_updated":
                    Vue.set(state, "public_state", payload.state);
                    break;

                case "private_state_updated":
                    Vue.set(state, "private_state", payload.state);
                    break;

                case "chat_message":
                    delete payload['type'];
                    state.chat.push(payload);
                    break;

                case "error":
                    window.alert(payload.message)
            }
        },
        SOCKET_ONRECONNECT() {},
        SOCKET_ONCLOSE(state) {
            state.chat = [];
            state.connected = false;
            state.private_state = null
        },
    },
})
