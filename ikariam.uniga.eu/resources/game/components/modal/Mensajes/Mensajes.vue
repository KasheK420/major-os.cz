<template>
    <div class="mBorder">
        <Ventana1 :close='close' :titulo="$t('advisor.diplomat.title')">
        <div class="box">
            <div class="d-flex">
                <div class="selectContainer" :class="type==0 ? 'active' : ''" title="Mensajes Recibidos No leidos/Total" @click='change(0)'>
                    <div class="imagen"></div>
                    <div class="texto">{{$t('messages.inbox')}} ({{this.data.totalNoReaded}}/{{this.data.totalReaded}})</div>
                </div>
                <div class="selectContainer" :class="type==1 ? 'active' : ''" title="Mensajes Enviados" @click='change(1)'>
                    <div class="imagen"></div>
                    <div class="texto">{{$t('messages.outbox')}} ({{this.data.totalSended}})</div>
                </div>
            </div>
            <ListaMensaje :nextPage='nextPage' :more='data.more' :page='data.page' :read='read' :remove='remove' :type='type' :data='messages' v-if="messages.length>0"></ListaMensaje>
            <div v-else class="nomessage">{{$t('messages.noMessage')}}</div>
        </div>
        </Ventana1>
    </div>
</template>

<script>
import Ventana1 from 'Components/modal/Ventanas/Ventana1.vue'
import ListaMensaje from 'Components/modal/Mensajes/ListaMensaje.vue'
import axios from "axios";
import $notification from 'Stores/notification'

export default {
    name:'Mensajes',
    props:['info','close'],
    components:{
        Ventana1,
        ListaMensaje
    },
    data(){
        return {
            type:0,
            data:{}
        }
    },
    methods:{
        nextPage(type){
            var page = type ? this.data.page+1 : this.data.page-1
            this.getMessages(page,this.type)
        },
        getMessages(page,type){
            axios.post("user/getMessages",{
                type:type,
                page:page
            }).then(res => {
                this.data = res.data
                this.type = type
            })
            .catch(err => {
                $notification.commit('show',{advisor:4,type:false,message:err});
            });
        },
        read(n){
            if(this.data.totalNoReaded>0){
                this.data.totalNoReaded -= n
                this.data.totalReaded += n
            }
        },
        change(type){
            this.getMessages(1,type)
        },
        remove(){
            this.getMessages(this.data.page,this.type)
        }
    },
    computed:{
        messages(){
            return this.type==0 ? this.data.received : this.data.sended;
        }
    },
    beforeMount(){
        this.data = this.info;
    }
}
</script>

<style lang="scss" scoped>
@import "~Sass/modal";
    .box{
        font-size: 0.83rem;
        line-height: 0.83rem;
    }
    .imagen{
        background-image: url('~Img/icon/schriftrolle_closed.png');
        width: 17px;
        height: 17px;
        background-repeat: no-repeat;
        background-position: center;
        display: inline-block;
        margin-top: auto;
        margin-bottom: auto;
    }
    .active .imagen{
        background-image: url('~Img/icon/schriftrolle_offen.png');
    }
    .active{
        background: #dfc594;
    }
    .texto{
        display: inline-block;
        margin-top: auto;
        margin-bottom: auto;
        margin-left: 5px;
    }
    .selectContainer{
        display: flex;
        justify-content: center;
        flex: 1;
        padding: 5px 0px;
        cursor: pointer;
    }
    .nomessage{
        text-align: center;
        margin: 20px 0px 10px;
    }
</style>
