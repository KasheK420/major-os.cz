<template>
    <div v-if="$store.state.public_state.winners !== null" :class="'m-5 p-10 rounded winners flex-grow mt-0 pulse ' + $store.state.public_state.winners">
        <img :src="images[$store.state.public_state.winners]" class="rounded-full" style="width: 100px;">
        <h1 class="text-3xl mt-5">The {{ $store.state.public_state.winners === "Fascist" ? "fascists" : "liberals"}} won this game</h1>
    </div>
</template>
<style>
    .winners {
        /*position: fixed;*/
        /*width: 100vw;*/
        /*height: 100vh;*/
        display: flex;
        flex-flow: column nowrap;
        justify-content: center;
        align-items: center;
        z-index: 100;
        left: 0;
        top: 0;
        text-shadow: 2px 2px #1a202c;
    }

    .winners.Liberal {
        background: rgba(42, 67, 101, 0.1);
        color: #63b3ed;
    }

    .winners.Fascist {
        background: rgba(116, 42, 42, 0.1);
        color: #fc8181;
    }

    .pulse.Liberal {
        animation: pulseLiberal infinite ease-in-out 1.5s;
    }

    .pulse.Fascist {
        animation: pulseFascist infinite ease-in-out 1.5s;
    }

    @keyframes pulseFascist {
        from {
            box-shadow: 0 0 0 0 rgba(116, 42, 42, 1);
        }
        to {
            box-shadow: 0 0 0 30px rgba(255, 255, 255, 0);
        }
    }

    @keyframes pulseLiberal {
        from {
            box-shadow: 0 0 0 0 rgba(42, 67, 101, 1);
        }
        to {
            box-shadow: 0 0 0 30px rgba(255, 255, 255, 0);
        }
    }

</style>
<script>
    import liberal from "../assets/roles/liberal.png"
    import fascist from "../assets/roles/fascist.png"

    export default {
        name: "Winners",
        data: () => ({
            images: {
                Liberal: liberal,
                Fascist: fascist
            }
        })
    }
</script>