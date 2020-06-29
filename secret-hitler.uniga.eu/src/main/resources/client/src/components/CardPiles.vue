<template>
    <div class="mr-5 bg-gray-800 self-start p-2 rounded-lg shadow flex flex-row">
        <div class="flex flex-row items-center justify-between">
            <img :src="images.drawing" alt="Drawing pile" title="Drawing pile" class="m-1">
            <div class="mx-1 text-gray-300">{{ $store.state.public_state.drawingPileCards }}</div>
        </div>
        <div class="flex flex-row items-center justify-between">
            <img :src="images.discard" alt="Drawing pile" title="Drawing pile" class="m-1">
            <div class="mx-1 text-gray-300">{{ $store.state.public_state.discardPileCards }}</div>
        </div>
    </div>
</template>
<style scoped>
    img {
        width: 20px;
    }
</style>
<script>
    import drawing from "../assets/piles/drawing.svg"
    import discard from "../assets/piles/discard.svg"

    export default {
        name: "CardPiles",
        data: () => ({
            images: {
                drawing: drawing,
                discard: discard,
            }
        })
    }
</script>