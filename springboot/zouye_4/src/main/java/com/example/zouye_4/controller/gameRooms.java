package com.example.zouye_4.controller;

import com.example.zouye_4.dao.GameInfoDao;
import com.example.zouye_4.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class gameRooms {
    private Map<String, gameRoom> rooms=new HashMap<String, gameRoom>();

    public Map<String, gameRoom> getRooms() {
        return rooms;
    }

    @Autowired
    GameInfoDao gameInfoDao;
    @Autowired
    UserDao userDao;

    public String setRoomsForTwo(WebSocketServer socketServer1, WebSocketServer socketServer2) {
        System.out.println(gameInfoDao);
        String roomId;
        List<WebSocketServer> two=new ArrayList<>();
        //将两个人放入房间
        two.add(socketServer1);
        two.add(socketServer2);
        String sid1=socketServer1.getSid();
        String sid2=socketServer2.getSid();
        //生成房间号
        if (Integer.parseInt(sid1)<Integer.parseInt(sid2)){
            roomId=sid1+sid2;
        }else{
            roomId=sid2+sid1;
        }
        gameRoom Room=new gameRoom();
        Room.setRoomId(roomId);
        Room.setGamers(two);
        rooms.put(roomId,Room);
        return roomId;
    }
}
