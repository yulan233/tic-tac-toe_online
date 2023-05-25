package com.example.zouye_4.controller;
 

import com.alibaba.fastjson.JSON;
import com.example.zouye_4.dao.GameInfoDao;
import com.example.zouye_4.entity.GameInfo;
import com.example.zouye_4.pojo.ContinueMessage;
import com.example.zouye_4.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
 

@Component
@Slf4j
@Service
@ServerEndpoint("/api/websocket/{sid}/{token}")
public class WebSocketServer {
    //当前在线连接数
    private static int onlineCount = 0;
    //存放每个客户端对应的MyWebSocket对象
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    private static CopyOnWriteArraySet<WebSocketServer> webSocketSetForMatch = new CopyOnWriteArraySet<WebSocketServer>();

    private static gameRooms rooms=new gameRooms();

 
    private Session session;

    public String getSid() {
        return sid;
    }

    //接收sid
    private String sid = "";
    //对手id
    private String opponentSid="";
    //设置自己的状态
    private String stateCode="0";

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getStateCode() {
        return stateCode;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid,@PathParam("token") String token) {
        System.out.println("token:"+token);
        this.session = session;
        if(webSocketSet.removeIf(item -> item.sid.equals(sid))){
            subOnlineCount();
        }
        webSocketSetForMatch.removeIf(item -> item.sid.equals(sid));

        webSocketSet.add(this);     //加入set中
        this.sid = sid;
        addOnlineCount();           //在线数加1
        try {
            ContinueMessage message=new ContinueMessage();
            message.setCode("conn_success");
            sendMessage(message);
            log.info("有新窗口开始监听:" + sid + ",当前在线人数为:" + getOnlineCount());
        } catch (IOException e) {
            log.error("websocket IO Exception");
        }
    }
 
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        stateCode="-1";
        if (webSocketSet.remove(this)){
            //从set中删除
            subOnlineCount();           //在线数减1
            log.info("释放的sid为："+sid);
            log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
        }
    }
 
    /**
     * 收到客户端消息后调用的方法
     * @ Param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
//        ContinueMessage resultDto=JSON.parseObject(message, ContinueMessage.class);
//        System.out.println(message);
//        System.out.println(resultDto.getData());
//        Map map=JSON.parseObject(resultDto.getData(),Map.class);
//        System.out.println(map.get("id"));
        ContinueMessage message1=JSON.parseObject(message,ContinueMessage.class);
        //token验证
        //匹配对战
        if (message1.getCode().equals("match")) {
            stateCode = "1";
            if (webSocketSetForMatch.size()>0){
                for (WebSocketServer item :
                        webSocketSetForMatch) {
                    if(item.getSid().equals(sid)){
                        continue;
                    }
                    webSocketSetForMatch.remove(item);
                    //设置双方都为对战状态
                    item.stateCode="2";
                    stateCode="2";
                    //设置发送的信息告诉双方将要开始了
                    String roomId="";
                    if (rooms!=null){
                        roomId=rooms.setRoomsForTwo(this,item);
                    }
                    ContinueMessage message2=new ContinueMessage();
                    message2.setCode("gogo");
                    Map<String,String> map=new HashMap<>();
                    map.put("sid",sid);
                    map.put("roomId",roomId);
                    message2.setData(map);
                    //发送
                    try {
                        sendInfo(message2,item.getSid());
                        map=new HashMap<>();
                        map.put("sid",item.sid);
                        map.put("roomId",roomId);
                        message2.setData(map);
                        sendInfo(message2,sid);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }else{
                //没有匹配到人添加到匹配队列
                webSocketSetForMatch.add(this);
            }
        }
        if(message1.getCode().equals("start")){

            if(rooms.getRooms().get(message1.getData().get("roomId"))!=null){
                rooms.getRooms().get(message1.getData().get("roomId")).initGame();
            }
        }
        if (message1.getCode().equals("closeMatch")){
            stateCode="0";
            if (webSocketSetForMatch.size()>0){
                webSocketSetForMatch.remove(this);
            }
        }
        if (message1.getCode().equals("lozi")){
            System.out.println(message1);
            int x= Integer.parseInt(message1.getData().get("x"));
            int y= Integer.parseInt(message1.getData().get("y"));
            if(rooms.getRooms().get(message1.getData().get("roomId"))!=null){
                rooms.getRooms().get(message1.getData().get("roomId")).setLozi(x,y,sid);
                if (rooms.getRooms().get(message1.getData().get("roomId")).isStop()){
                    rooms.getRooms().remove(message1.getData().get("roomId"));
                }
            }
        }
        if(message1.getCode().equals("testClose")){
            if(rooms.getRooms().get(message1.getData().get("roomId"))!=null){
                boolean OPclosed=rooms.getRooms().get(message1.getData().get("roomId")).closeTest(message1.getData().get("id"));
                if (OPclosed){
                    rooms.getRooms().remove(message1.getData().get("roomId"));
                }
            }
        }
        if (message1.getCode().equals("test")){
            ContinueMessage message2=new ContinueMessage();
            message2.setCode("test");
            message2.setMsg("testmsg");
            try {
                sendInfo(message2,message1.getData().get("id"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        log.info("收到来自窗口" + sid + "的信息:" + message);
        //群发消息
//        for (WebSocketServer item : webSocketSet) {
//            try {
//                if (!Objects.equals(item.sid, sid))
//                    item.sendMessage(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
 
    /**
     * @ Param session
     * @ Param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(ContinueMessage message) throws IOException {
        this.session.getBasicRemote().sendText(JSON.toJSONString(message));
    }
 
    /**
     * 群发自定义消息
     */
    public static void sendInfo(ContinueMessage message, @PathParam("sid") String sid) throws IOException {
        log.info("推送消息到窗口" + sid + "，推送内容:" + message);

        for (WebSocketServer item : webSocketSet) {
            try {
                //为null则全部推送
                if (sid == null) {
//                    item.sendMessage(message);
                } else if (item.sid.equals(sid)) {
                    item.sendMessage(message);
                }
            } catch (IOException e) {
                continue;
            }
        }
    }
 
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }
 
    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }
 
    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
 
    public static CopyOnWriteArraySet<WebSocketServer> getWebSocketSet() {
        return webSocketSet;
    }
}