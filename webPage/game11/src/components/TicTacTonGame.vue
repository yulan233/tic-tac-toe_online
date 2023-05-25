<script setup>
import { reactive, ref } from 'vue'
import axios from 'axios';
import { useStore } from '../store'
import { useRouter } from 'vue-router'
let router = useRouter()
const store = useStore()
let websocket = store.WebSocket
let roomId = store.roomMessage.data.roomId
let testClose_tag=true
const user = ref({
    username: '',
    id: '',
    token: '',
    winRate: '',
    frist: true,
    isCan: true
})
const opponent = ref({
    username: '',
    id: '',
    winRate: '',
    frist: true,
    isCan: true
})

//接收消息回调方法
websocket.onmessage = function (event) {
    const roomMessage = JSON.parse(event.data)
    console.log(roomMessage);
    console.log(111);
    if (roomMessage.code == 'first') {

        user.value.frist = true
        user.value.isCan = true
        opponent.value.frist = false
        opponent.value.isCan = false
    }
    if (roomMessage.code == 'second') {

        user.value.frist = false
        user.value.isCan = false
        opponent.value.frist = true
        opponent.value.isCan = true
    }
    if (roomMessage.code == 'stop') {
        testClose_tag=false
        alert(roomMessage.msg)
        router.push("/usershow")
    }
    if (roomMessage.code == 'continue') {
        const i = parseInt(roomMessage.data.x) * 3 + parseInt(roomMessage.data.y)
        if (opponent.value.frist) {
            grid.value[i].status = 1
        } else {
            grid.value[i].status = 2
        }
        user.value.isCan = true
        opponent.value.isCan = false
    }
    if (roomMessage.code == 'stop1') {
        testClose_tag=false
        alert(roomMessage.msg)
        router.push("/usershow")
    }
    if (roomMessage.code == 'stop2') {
        testClose_tag=false
        const i = parseInt(roomMessage.data.x) * 3 + parseInt(roomMessage.data.y)
        if (opponent.value.frist) {
            grid.value[i].status = 1
        } else {
            grid.value[i].status = 2
        }
        alert(roomMessage.msg)
        router.push("/usershow")
    }
    if (roomMessage.code == 'test') {
        alert(roomMessage.msg)
    }
}

const getUserAndInit = () => {
    //从sessionStorage中获取用户信息
    let user1 = JSON.parse(sessionStorage.getItem('user'))
    //填充到user中
    // user.value.frist=false
    // user.value.isCan=false
    user.value.token = JSON.parse(sessionStorage.getItem('token'))
    user.value.winRate = user1.winRate
    user.value.username = user1.username
    user.value.id = user1.id
    const opid = store.roomMessage.data.sid
    axios.post('http://localhost:8080/api/user/getUserMessage', {
        id: opid
    }).then(res => {
        if (res.data.code == 200) {
            console.log(res.data);
            opponent.value.username = res.data.data.username
            opponent.value.id = res.data.data.id
            opponent.value.winRate = res.data.data.winRate
            if (Number(user.value.id) < Number(opponent.value.id)) {
                // console.log(111);
                const sendMessage = {
                    code: 'start',
                    data: {
                        roomId: roomId
                    }
                }
                console.log(sendMessage);
                websocket.send(JSON.stringify(sendMessage))
            }
        }else if(res.data.code=="tokenError"){
            alert(res.data.res)
            router.push('/');
        }else{
            alert(res.data.res)
        }
    })
}

const grid = ref([
    {
        value: 0,
        status: 0
    },
    {
        value: 1,
        status: 0
    },
    {
        value: 2,
        status: 0
    },
    {
        value: 3,
        status: 0
    },
    {
        value: 4,
        status: 0
    },
    {
        value: 5,
        status: 0
    },
    {
        value: 6,
        status: 0
    },
    {
        value: 7,
        status: 0
    },
    {
        value: 8,
        status: 0
    },
])

const ClickGrid = (i) => {
    // console.log(i);
    if (grid.value[i].status == 0 && user.value.frist && user.value.isCan) {
        // console.log(i);
        grid.value[i].status = 1
        user.value.isCan = false
        opponent.value.isCan = true
        const sendMessage = {
            code: 'lozi',
            data: {
                x: parseInt(i / 3),
                y: i % 3,
                roomId: roomId
            }
        }
        websocket.send(JSON.stringify(sendMessage))

    }
    if (grid.value[i].status == 0 && !user.value.frist && user.value.isCan) {
        grid.value[i].status = 2
        user.value.isCan = false
        opponent.value.isCan = true
        const sendMessage = {
            code: 'lozi',
            data: {
                x: parseInt(i / 3),
                y: i % 3,
                roomId: roomId
            }
        }
        websocket.send(JSON.stringify(sendMessage))
    }

}
const testFasong = () => {
    const opid = store.roomMessage.data.sid
    const sendMessage = {
        code: 'test',
        data: {
            id: opid,
        }
    }
    websocket.send(JSON.stringify(sendMessage))
}
//设置一个掉线检测每三秒执行一次，查看对面是否断线
getUserAndInit()
setInterval(()=>{
    const opid = store.roomMessage.data.sid
    const sendMessage = {
        code: 'testClose',
        data: {
            id: opid,
            roomId:roomId
        }
    }
    if(testClose_tag){
        websocket.send(JSON.stringify(sendMessage))
    }
},3000)

</script>
<template>
    <div class="game_body">
        <div class="jie_shao">
            <el-card class="box-card">
                <template #header>
                    <div class="game_user card-header">
                        <span>名字：{{ user.username }}</span>
                    </div>
                </template>
                <div class="item text">胜率：{{ user.winRate }}</div>
                <div class="item text">先手：{{ user.frist }}</div>
                <div class="item text">你的回合：{{ user.isCan }}</div>
            </el-card>
            <el-card class="box-card">
                <template #header>
                    <div class="game_opponent card-header">
                        <span>名字：{{ opponent.username }}</span>
                    </div>
                </template>
                <div class="item text">胜率：{{ opponent.winRate }}</div>
                <div class="item text">先手：{{ opponent.frist }}</div>
                <div class="item text">你的回合：{{ opponent.isCan }}</div>
            </el-card>

        </div>
        <div class="gameLayout">
            <img v-for="(item, index) in grid" :id="item.value" class="grid" @click="ClickGrid(item.value)"
                :src="item.status == 0 ? ('../src/assets/gridz-export.png') : (item.status == 1 ? ('../src/assets/grid.png') : ('../src/assets/gridz.png'))" />
        </div>
    </div>
    <div id="huihe_message">
        <el-button @click="testFasong()">testFasong</el-button>
    </div>
</template>
<style scoped>
.game_body {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}

.gameLayout {
    width: 40%;
    background-color: #5fcde4;
    display: flex;
    flex-wrap: wrap;
}

.grid {
    width: 30%;
    height: 30%;
    margin: 1.6%;
}

.jie_shao {
    width: 30%;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: space-around;
    margin-right: 10px;
}

.box-card {
    margin: 10px;
}

.card-header {
    font-size: large;
    font-weight: bold;
}
</style>