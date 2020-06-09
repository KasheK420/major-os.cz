import Vue from 'vue'
import App from './App.vue'
import store from './store'
import VueNativeSocket from 'vue-native-websocket'
import Autoscroll from '@codekraft-studio/vue-autoscroll';

const parts = window.location.href.split("/")

// Debug server must be explicitly remapped
const hostname = parts[2].replace(":8080", ":8000")

Vue.config.productionTip = false
Vue.use(
    VueNativeSocket,
    "ws://" + hostname + "/game",
    {
        store: store,
        format: 'json',
        reconnection: true,
        reconnectionDelay: 3000,
    }
);
Vue.use(Autoscroll);


new Vue({
    store,
    render: h => h(App)
}).$mount('#app')
