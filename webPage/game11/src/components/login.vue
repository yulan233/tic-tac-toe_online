<script setup>
import { reactive, ref } from 'vue'
import {useRouter} from 'vue-router'
import axios from 'axios'
let router = useRouter()
const mod=ref({
    name:'登陆',
    mod:true
})
const user=ref({
    username:'',
    password:'',
    repassword:''
})
const goLogin=()=>{
    if(!mod.value.mod){
        mod.value.mod=true
        mod.value.name='登陆'
    }else{
        // 登陆axios
        axios.post('http://localhost:8080/api/user/login',{
            username:user.value.username,
            password:user.value.password
        }).then(res=>{
            if(res.data.code==200){
                sessionStorage.setItem('user',JSON.stringify(res.data.data))
                sessionStorage.setItem('token',JSON.stringify(res.data.token))
                console.log(res.data);
                alert(res.data.msg)
                router.push('/usershow')
            }else{
                alert(res.data.msg)
            }
        })
    }
}
const goRegister=()=>{
    if(mod.value.mod){
        mod.value.mod=false
        mod.value.name='注册'
    }else{
        //验证密码
        if(user.value.password!=user.value.repassword){
            alert('两次密码不一致')
            return
        }
        // 注册axios
        axios.post('http://localhost:8080/api/user/register',{
            username:user.value.username,
            password:user.value.password
        }).then(res=>{
            if(res.data.code==200){
                alert(res.data.msg)
            }else{
                alert(res.data.msg)
            }
        })
    }
}
</script>
<template>
    <div class="login_body">
        <div class="login_title">{{mod.name}}</div>
        <input type="text" v-model="user.username" placeholder="用户名">
        <input type="password" v-model="user.password" placeholder="密码">
        <input :hidden="mod.mod" v-model="user.repassword" type="password" placeholder="确认密码">
        <div class="butoon_group">
            <button @click="goLogin">登陆</button>
            <button @click="goRegister()">注册</button>
        </div>
        
    </div>
</template>
<style scoped>
.login_body{
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}
</style>