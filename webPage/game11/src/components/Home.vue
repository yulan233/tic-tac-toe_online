<script setup>
import { reactive, ref } from 'vue'
import {useRouter} from 'vue-router'
import login from './login.vue'
let router = useRouter()
//路由到login


</script>
<template>
    <div class="home_body">
        <div class="home_title">井字棋</div>
        <div class="home_user">
            <login></login>
        </div>
    </div>
</template>
<style scoped>
.home_body{
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}
.home_title{
    margin-top: 100px;
    font-size: 50px;
    color: #42b983;
}
</style>