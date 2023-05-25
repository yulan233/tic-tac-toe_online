<script setup>
import { reactive, ref } from 'vue'
import TicTacTonGame from './TicTacTonGame.vue'

var websocket
const get_websocket = (i) => {
  if (window.WebSocket) {
    websocket = new WebSocket("ws://localhost:8080/api/websocket/" + i)
    // websocket=new WebSocket()
    websocket.onerror = function () {
      setMessageInnerHTML("WebSocket连接发生错误");
    };

    //连接成功建立回调方法
    websocket.onopen = function () {
      setMessageInnerHTML("WebSocket连接成功");
    }
    //接收消息回调方法
    websocket.onmessage = function (event) {
      console.log(event.data);
      i = true
      if (event.data && i) {
        setMessageInnerHTML(event.data);
        i = false
      }

      //  setechart()
    }

    //连接关闭回调方法
    websocket.onclose = function () {
      setMessageInnerHTML("WebSocket连接关闭");
    }

    //监听窗口关闭事件
    window.onbeforeunload = function () {
      closeWebSocket();
    }
  } else {
    alert('当前浏览器 Not support websocket')
  }
}



//将消息显示在网页上
function setMessageInnerHTML(innerHTML) {
  document.getElementById('message').innerHTML += innerHTML + '<br/>';
}

//关闭WebSocket连接
const closeWebSocket = function closeWebSocket() {
  websocket.close();
}

//发送消息
const send = function send() {
  var message = document.getElementById('text').value;
  websocket.send(message);
  setMessageInnerHTML(message + "&#13;");
}
defineProps({
  msg: String
})

const count = ref(0)
const user = reactive({
  username: "user1",
  winRate: "50%",
  token: "kakaka"
})
const opponent = ref({
  username: "user2",
  winRate: "50%",
  token: "kakaka"
})



</script>

<template>
  <div class="body">
    <div class="head">
      <div class="user my">username:{{ user.username }}<br />winRate:{{ user.winRate }}</div>
      <div class="user opponent">opponent:{{ opponent.username }}<br />winRate:{{ opponent.winRate }}</div>
    </div>
    <div class="websocketTest">
      <div id="main" style="width: 500px;height:200px;"></div>
      <button @click="get_websocket(1)">1</button>
      <button @click="get_websocket(2)">2</button>
      Welcome<br /><input id="text" type="text" />
      <button @Click="send()">发送消息</button>
      <hr />
      <button @Click="closeWebSocket()">关闭WebSocket连接</button>
      <hr />
      <div id="message"></div>
      
    </div>
    <TicTacTonGame class="gameArea"></TicTacTonGame>
  </div>


</template>

<style scoped>
a {
  color: #42b983;
}

.body {
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.head {
  background-color: #42b983;
  /* height: 40px; */
  width: 100%;
  display: flex;
  justify-content: space-around;
}

.user {
  margin: 10px 0 10px 0;
}

.gameArea {
  margin-top: 10px;
}
</style>
