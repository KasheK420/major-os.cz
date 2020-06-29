<template>
    <div id="app">
        <div v-if="!this.$store.state.connected"
             class="flex flex-col justify-center items-center px-2 py-3 bg-red-900 text-red-300 fullscreen">
            <img :src="images.disconnected" class="m-10">
            <div class="text-2xl">
                Disconnected from the WebSocket server
            </div>
        </div>
        <div v-for="error in $store.state.errors" :key="error.id">
            <b>{{ error.id }}</b>
            {{ error.message }}
        </div>

        <Game v-if="this.$store.state.public_state.started"/>
        <Lobby v-else/>

        <footer class="fixed flex bg-gray-900 shadow w-full justify-between items-center py-2 px-5">
            <b class="text-gray-300 font-normal">&copy; 2020, Jiří Vrba</b>
            <div class="flex justify-between items-center">
                <a class="text-teal-600 hover:text-teal-3000" href="https://discord.gg/D7cYuM" target="_blank">Discord server</a>
                <a href="https://www.gitlab.com/jirkavrba/secret-hitler" class="text-teal-600 hover:text-teal-300 ml-3" target="_blank">Source
                    code</a>
                <a href="https://creativecommons.org/licenses/by-nc-sa/4.0/" rel="nofollow" class="ml-3"><img
                        src="https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-black.svg"
                        alt="License: CC BY-NC-SA 4.0"
                        data-canonical-src="https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-black.svg"
                        style="max-width:100%;"></a>
            </div>
        </footer>
    </div>
</template>
<style>
    .fullscreen {
        width: 100vw;
        height: 100vh;
        position: fixed;
    }

    .fullscreen img {
        width: 150px;
    }

    footer {
        left: 0;
        bottom: 0;
    }
</style>
<script>
    import "./assets/css/tailwind.min.css";
    import disconnected from "./assets/disconnected.svg"

    import Lobby from "./components/Lobby";
    import Game from "./components/Game";

    export default {
        name: 'App',
        data: () => ({
            images: {
                disconnected
            }
        }),
        components: {
            Lobby,
            Game
        } ,
    }
</script>
