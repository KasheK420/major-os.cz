<template>
    <div class="flex flex-row justify-start ml-5 items-center">
        <div class="flex flex-row justify-start rounded-lg p-2 items-center bg-gray-800 shadow">
            <div v-for="i in 3" :key="i"
                 :class="($store.state.public_state.electionCounter === (i - 1) ? 'bg-white' : 'bg-gray-700') + ' rounded-full m-1'"
                 style="width: 15px; height: 15px;">
            </div>
            <div class="rounded-full m-1 bg-red-700"
                 style="width: 15px; height: 15px;">
            </div>
        </div>
    </div>
</template>
<script>
    export default {
        name: "ElectionCounter"
    }
</script>