<script setup>
import { ref } from 'vue'
import axios, { Axios } from 'axios'
import { useStore } from '../store'
import { useRouter } from 'vue-router'
let router = useRouter()
const store = useStore();
let websocket = store.WebSocket
const user = ref({
    username: '',
    id: '',
    token: '',
    winRate: '',
    totalGames: '',
    winGames: ''
})
const changUser = ref({
    username: '',
    password: '',
    repassword: ''
})
const gameInfoList = ref([])
const getUser = () => {
    //从sessionStorage中获取用户信息

    let user1 = JSON.parse(sessionStorage.getItem('user'))
    //填充到user中
    user.value.token = JSON.parse(sessionStorage.getItem('token'))
    user.value.username = user1.username
    user.value.id = user1.id
    axios.post('http://localhost:8080/api/user/getUserMessage',{
        id:user.value.id
    }).then(res=>{
        if (res.data.code == 200){
            user.value.winGames=res.data.data.winGames
            user.value.totalGames=res.data.data.totalGames
            user.value.winRate=res.data.data.winRate
        }else{
            alert(res.data.msg)
        }
    })

}
const getGameInfo = () => {
    // console.log(user.value);
    axios.post('http://localhost:8080/api/gameInfo/getGameInfoById', {
        id: user.value.id
    }, {
        headers: {
            userid: user.value.id,
            token: user.value.token
        }
    }).then(res => {
        if (res.data.code == 200) {
            // console.log(res.data);
            // alert(res.data.msg)
            gameInfoList.value = res.data.data
            for (const key in gameInfoList.value) {
                if (Object.hasOwnProperty.call(gameInfoList.value, key)) {
                    const element = gameInfoList.value[key];
                    if (element.winner == element.firstId) {
                        element.winner = element.firstName
                    }
                    if (element.winner == element.secondId) {
                        element.winner = element.secondName
                    }
                }
            }
        } else if(res.data.code=="tokenError"){
            alert(res.data.msg)
            router.push('/');
        }else{
            alert(res.data.res)
        }
    })
}
//用户操作，修改密码改名
const changUsername_ref = ref(false)
const changPassword_ref = ref(false)
const changUsername = () => {
    const some = {
        token: user.value.token,
        id: user.value.id,
        username: changUser.value.username
    }
    axios.post('http://localhost:8080/api/user/changeUsername', some,
        {
            headers: {
                userid: user.value.id,
                token: user.value.token
            }
        }
    ).then(res => {
        changUsername_ref.value = false
        if (res.data.code == 200) {
            alert(res.data.msg)
            user.value.username = changUser.value.username
            let user2 = JSON.parse(sessionStorage.getItem('user'))
            user2.username = changUser.value.username
            sessionStorage.setItem("user", JSON.stringify(user2))
            changUser.value.username = ''
        }else if(res.data.code=="tokenError"){
            alert(res.data.msg)
            router.push('/');
        }else{
            alert(res.data.msg)
        }

    })
}
const changPassword = () => {
    if (changUser.value.password == changUser.value.repassword) {
        const some = {
            token: user.value.token,
            id: user.value.id,
            password: changUser.value.password
        }
        axios.post('http://localhost:8080/api/user/changePassword', some,
            {
                headers: {
                    userid: user.value.id,
                    token: user.value.token
                }
            }
        ).then(res => {
            changPassword_ref.value = false
            if (res.data.code == 200) {
                alert(res.data.msg)
                changUser.value.password = ''
                changUser.value.repassword = ''
            }else if(res.data.code=="tokenError"){
            alert(res.data.msg)
            router.push('/');
        }else{
            alert(res.data.msg)
        }

        })
    } else {
        alert("两次密码不对")
    }
}
const get_websocket = () => {
    if (window.WebSocket) {
        if (websocket == null) {
            websocket = new WebSocket("ws://localhost:8080/api/websocket/" + user.value.id + "/" + user.value.token)
        }
        // websocket=new WebSocket()
        websocket.onerror = function () {

        };

        //连接成功建立回调方法
        websocket.onopen = function () {

        }
        //接收消息回调方法
        websocket.onmessage = function (event) {
            const roomMessage = JSON.parse(event.data)
            console.log(roomMessage);
            if (roomMessage.code === 'gogo') {
                store.roomMessage = roomMessage
                store.WebSocket = websocket
                router.push('/game')
            }
        }

        //连接关闭回调方法
        websocket.onclose = function () {

        }

        //监听窗口关闭事件
        window.onbeforeunload = function () {
            websocket.close()
        }
    } else {
        alert('当前浏览器 Not support websocket')
    }
}
const matchDialog = ref(false)
const send = () => {
    matchDialog.value = true;
    //发送匹配消息
    const sendMessage = {
        code: 'match',
        data: {
        }
    }
    websocket.send(JSON.stringify(sendMessage))
}
const close = () => {
    if (websocket != null) {
        websocket.close()
        websocket = null
    }
}
const closeMatch = () => {
    if (websocket != null) {
        matchDialog.value = false;
        //发送匹配消息
        const sendMessage = {
            code: 'closeMatch',
            data: {
            }
        }
        websocket.send(JSON.stringify(sendMessage))
    }
}
getUser()
getGameInfo()
get_websocket()
</script>
<template>
    <div class="usershow_body">
        <div class="usershow_user">
            <el-card class="box-card">
                <template #header>
                    <div class="game_user card-header">
                        <span>名字：{{ user.username }}</span>
                    </div>
                </template>
                <div class="item text">总场数：{{ user.totalGames }}</div>
                <div class="item text">胜场数：{{ user.winGames }}</div>
                <div class="item text">胜率：{{ user.winRate }}</div>
            </el-card>
            <el-card class="box-card">
                <template #header>
                    <div class="game_user card-header">
                        <span>最近场次</span>
                    </div>
                </template>
                <el-table :data="gameInfoList" style="width: 100%" v-if="gameInfoList.length != 0">
                    <el-table-column prop="firstName" label="先手" />
                    <el-table-column prop="secondName" label="后手" />
                    <el-table-column prop="winner" label="胜负" />
                    <el-table-column prop="time" label="时间" width="160" />
                </el-table>
            </el-card>
        </div>
        <div class="usershow_gamematch">
            <el-card class="box-card">
                <template #header>
                    <div class="game_user card-header">
                        <span>个人设置</span>
                    </div>
                </template>
                <el-button @click="changUsername_ref = true">修改名字</el-button>
                <el-button @click="changPassword_ref = true">修改密码</el-button>
            </el-card>
            <el-card class="box-card">
                <template #header>
                    <div class="game_user card-header">
                        <span>井字棋</span>
                    </div>
                </template>
                <el-button color="#626aef" size="large" @click="send()">开始匹配</el-button>
                <el-button color="#626aef" size="large" @click="close()">停止</el-button>
            </el-card>
        </div>
    </div>
    <el-dialog v-model="changUsername_ref" title="修改用户名" width="30%" :before-close="handleClose">
        <el-input v-model="changUser.username" placeholder="Please input" />
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="changUsername_ref = false">取消</el-button>
                <el-button type="primary" @click="changUsername()">确认</el-button>
            </span>
        </template>
    </el-dialog>
    <el-dialog v-model="changPassword_ref" title="修改密码" width="30%" :before-close="handleClose">
        <el-input v-model="changUser.password" type="password" placeholder="Please input password" show-password />
        <el-input v-model="changUser.repassword" type="password" placeholder="Please input repassword" show-password />
        <template #footer>
            <span class="dialog-footer">

                <el-button @click="changPassword_ref = false">取消</el-button>
                <el-button type="primary" @click="changPassword()">确认</el-button>
            </span>
        </template>
    </el-dialog>
    <el-dialog v-model="matchDialog" :show-close="false">
        <template #header="{ close, titleId, titleClass }">
            <div class="my-header">
                <h4 :id="titleId" :class="titleClass">正在匹配中...</h4>
                <div class="closeMatch">
                    <el-button type="danger" @click="closeMatch()">
                        停止匹配
                    </el-button>
                </div>

            </div>
        </template>
        <div v-loading="true"></div>
    </el-dialog>
</template>
<style scoped>
.box-card {
    margin: 10px;
    width: 90%;
}

.card-header {
    font-size: large;
    font-weight: bold;
}

.usershow_body {
    width: 100%;
    height: 1000px;
    display: flex;
}

.usershow_user {
    width: 50%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.usershow_gamematch {
    display: flex;
    width: 50%;
    height: 500px;
    flex-direction: column;
    align-items: center;
}

.my-header {
    display: flex;
    flex-direction: row;
    justify-content: space-around;
}

.closeMatch {
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>