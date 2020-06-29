import axios from 'axios';
import moment from 'moment-timezone'
import Vue from 'vue'
import VueChatScroll from 'vue-chat-scroll'

Vue.use(VueChatScroll)


var pusher = new Pusher('b11b790e5dea909e2b7f', {
    cluster: 'us2',
    disableStats: true,
  });

Vue.prototype.$chUser  = pusher.subscribe(document.querySelector('meta[name="user-notification"]').getAttribute('content'));
Vue.prototype.$chChat  = pusher.subscribe('chat');

axios.defaults.headers.common = {
    'X-Requested-With': 'XMLHttpRequest',
    'X-CSRF-TOKEN' : document.querySelector('meta[name="csrf-token"]').getAttribute('content')
}
axios.defaults.baseURL = '/api/';

moment.tz.setDefault('America/Guayaquil')
