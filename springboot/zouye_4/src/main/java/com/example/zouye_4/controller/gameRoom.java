package com.example.zouye_4.controller;

import com.example.zouye_4.dao.GameInfoDao;
import com.example.zouye_4.dao.UserDao;
import com.example.zouye_4.entity.GameInfo;
import com.example.zouye_4.entity.User;
import com.example.zouye_4.pojo.ContinueMessage;
import com.example.zouye_4.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class gameRoom {
    GameInfoDao gameInfoDao=(GameInfoDao) SpringUtil.getBean("gameInfoDao");;

    UserDao userDao=(UserDao) SpringUtil.getBean("userDao");

    private String roomId="";

    private List<WebSocketServer> gamers;

    private String first_id="";
    private String second_id="";
    private String step="";
    private int[][] grid=new int[3][3];
    private boolean isStop=false;

    public boolean isStop() {
        return isStop;
    }

    //初始化一盘游戏
    public void initGame(){
        Random random=new Random();
        //随机先后手
        ContinueMessage message=new ContinueMessage();
        if(random.nextBoolean()){
            try {
                System.out.println("initGame");
                message.setCode("first");
                first_id=gamers.get(0).getSid();
                WebSocketServer.sendInfo(message,gamers.get(0).getSid());
                message.setCode("second");
                second_id=gamers.get(1).getSid();
                WebSocketServer.sendInfo(message,gamers.get(1).getSid());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                message.setCode("second");
                second_id=gamers.get(0).getSid();
                WebSocketServer.sendInfo(message,gamers.get(0).getSid());
                message.setCode("first");
                first_id=gamers.get(1).getSid();
                WebSocketServer.sendInfo(message,gamers.get(1).getSid());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //落子
    public void setLozi(int x,int y,String id){
        boolean iswin=false;
        String opid="";
        ContinueMessage message=new ContinueMessage();
        System.out.println(x+""+y+""+id);
        //落子并判断是否赢了
        if(first_id.equals(id)){
            if(grid[x][y]==0){
                grid[x][y]=1;
                iswin=isWin(1);
            }
            opid=second_id;
        }
        if(second_id.equals((id))){
            if(grid[x][y]==0){
                grid[x][y]=2;
                iswin=isWin(2);
            }
            opid=first_id;
        }
        step=step+ (x * 3 + y);
        //如果赢了或输了，不一样的回复
        if (iswin){
            message.setCode("stop");
            Map<String,String> map=new HashMap<>();
            map.put("x", String.valueOf(x));
            map.put("y", String.valueOf(y));
            map.put("winner",id);
            message.setData(map);
            message.setMsg(id+"赢了");
            if(gameInfoDao!=null){
                //更新对局表
                GameInfo gameInfo=new GameInfo();
                gameInfo.setFirstId(Integer.parseInt(first_id));
                gameInfo.setSecondId(Integer.parseInt(second_id));
                gameInfo.setStep(step);
                gameInfo.setWinner(id);
                gameInfoDao.insert(gameInfo);
            }
            if(userDao!=null){
                //更新用户信息
                User user=new User();
                user=userDao.selectById(id);
                assert false;
                user.setTotalGames(user.getTotalGames()+1);
                user.setWinGames(user.getWinGames()+1);
                user.setWinRate(user.getWinGames()/(float)user.getTotalGames());
                userDao.updateById(user);
                if (!id.equals(first_id)) {
                    user=userDao.selectById(first_id);

                }else{
                    user=userDao.selectById(second_id);
                }
                user.setTotalGames(user.getTotalGames()+1);
                user.setWinRate(user.getWinGames()/(float)user.getTotalGames());
                userDao.updateById(user);
            }


            for (int i = 0; i < 2; i++) {
                gamers.get(i).setStateCode("0");
            }
            try {
                WebSocketServer.sendInfo(message,id);
                WebSocketServer.sendInfo(message,opid);
            } catch (IOException e) {
                e.printStackTrace();
            }
            isStop=true;
        }else if(fullGrid()){
            message.setCode("stop2");
            Map<String,String> map=new HashMap<>();
            map.put("x", String.valueOf(x));
            map.put("y", String.valueOf(y));
            message.setData(map);
            message.setMsg("平局");
            if(gameInfoDao!=null){
                //更新对局表
                GameInfo gameInfo=new GameInfo();
                gameInfo.setFirstId(Integer.parseInt(first_id));
                gameInfo.setSecondId(Integer.parseInt(second_id));
                gameInfo.setStep(step);
                gameInfo.setWinner("平局");
                gameInfoDao.insert(gameInfo);
            }
            //更新用户信息
            if(userDao!=null){
                User user=new User();
                user=userDao.selectById(id);
                assert false;
                user.setTotalGames(user.getTotalGames()+1);
                user.setWinRate(user.getWinGames()/(float)user.getTotalGames());
                userDao.updateById(user);
                if (!id.equals(first_id)) {
                    user=userDao.selectById(first_id);

                }else{
                    user=userDao.selectById(second_id);
                }
                user.setTotalGames(user.getTotalGames()+1);
                user.setWinRate(user.getWinGames()/(float)user.getTotalGames());
                userDao.updateById(user);
            }
            //设置状态
            for (int i = 0; i < 2; i++) {
                gamers.get(i).setStateCode("0");
            }

            try {
                WebSocketServer.sendInfo(message,id);
                WebSocketServer.sendInfo(message,opid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            message.setCode("continue");
            Map<String,String> map=new HashMap<>();
            map.put("x", String.valueOf(x));
            map.put("y", String.valueOf(y));
            message.setData(map);
            try {
                WebSocketServer.sendInfo(message,opid);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //测试这个id是否断线
    public boolean closeTest(String id){
        boolean isClosed=false;
        for (WebSocketServer item :
                gamers) {
            if (Objects.equals(item.getStateCode(), "-1")|| !Objects.equals(item.getStateCode(), "2")) {
                isClosed=true;
                isStop=true;
                ContinueMessage message=new ContinueMessage();
                message.setCode("stop1");
                message.setMsg("对方断线");
                if (gameInfoDao!=null){
                    GameInfo gameInfo=new GameInfo();
                    gameInfo.setFirstId(Integer.parseInt(first_id));
                    gameInfo.setSecondId(Integer.parseInt(second_id));
                    gameInfo.setStep(step);
                    gameInfo.setWinner("断线");
                    gameInfoDao.insert(gameInfo);
                }
                //发送断线的消息
                try {
                    if (first_id.equals(item.getSid())){
                        WebSocketServer.sendInfo(message,second_id);
                    }else{
                        WebSocketServer.sendInfo(message,first_id);
                    }
                    for (int i = 0; i < 2; i++) {
                        if(!gamers.get(i).getSid().equals(item.getSid())){
                            gamers.get(i).setStateCode("0");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isClosed;
    }
    //是否赢得判断函数
    private boolean isWin(int i){
        boolean isWin=false;
        for (int j = 0; j < 3; j++) {
            if(grid[0][j]==i&&grid[1][j]==i&&grid[2][j]==i){
                isWin=true;
                return isWin;
            }
        }
        for (int j = 0; j < 3; j++) {
            if(grid[j][0]==i&&grid[j][1]==i&&grid[j][2]==i){
                isWin=true;
                return isWin;
            }
        }
        if(grid[0][0]==i&&grid[1][1]==i&&grid[2][2]==i){
            isWin=true;
            return isWin;
        }
        if(grid[0][2]==i&&grid[1][1]==i&&grid[2][0]==i){
            isWin=true;
            return isWin;
        }
        return isWin;
    }
    //平局
    public boolean fullGrid(){
        boolean isFullGrid=true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[i][j]==0){
                    isFullGrid=false;
                    return isFullGrid;
                }
            }
        }
        return isFullGrid;
    }
    public List<WebSocketServer> getGamers() {
        return gamers;
    }

    public void setGamers(List<WebSocketServer> gamers) {
        this.gamers = gamers;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }


}
