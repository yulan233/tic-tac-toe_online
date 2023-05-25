package com.example.zouye_4.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.zouye_4.dao.UserDao;
import com.example.zouye_4.entity.User;
import com.example.zouye_4.pojo.ResultDto;

import com.example.zouye_4.util.UtilId;
import com.example.zouye_4.util.TokenManage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/api/user")
@CrossOrigin
public class userController {

    @Autowired
    TokenManage tokenManage;
    @Autowired
    UserDao userDao;

    @ResponseBody
    @RequestMapping("/login")
    public ResultDto<Object> login(@RequestBody User user){
//        System.out.println(user);
        User user1=userDao.selectOne(new QueryWrapper<User>().eq("username", user.getUsername()).eq("password", user.getPassword()));
        ResultDto<Object> resultDto=new ResultDto<Object>();
        if(user1==null){
            resultDto.setMsg("用户名或密码错误");
        }else{
            user1.setPassword("");
            UtilId utilId=new UtilId();
            resultDto.setToken(utilId.getGUID());
            System.out.println(tokenManage);
            tokenManage.setToken(user1.getId(),resultDto.getToken());
            resultDto.setCode("200");
            resultDto.setMsg("登录成功");
        }
        resultDto.setData(user1);
        return resultDto;
    }

    @ResponseBody
    @RequestMapping("/register")
    public ResultDto<Object> register(@RequestBody User user){
//        System.out.println(user);
        ResultDto<Object>  resultDto=new ResultDto<>();
        User user1=userDao.selectOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        if(user1==null){
            user.setTotalGames(0);
            user.setWinGames(0);
            user.setWinRate(0);
            userDao.insert(user);
            resultDto.setCode("200");
            resultDto.setMsg("注册成功");
        }else{
            resultDto.setCode("201");
            resultDto.setMsg("注册重名");
        }
        resultDto.setMsg("register");
        return resultDto;
    }
    @ResponseBody
    @RequestMapping("/changeUsername")
    public ResultDto<Object> changeUsername(@RequestBody Map<String,Object> map){
        String token=map.get("token").toString();

        User user=new User();
        user.setId(Integer.parseInt(map.get("id").toString()));
        user.setUsername(map.get("username").toString());
        userDao.updateById(user);
        ResultDto<Object> resultDto=new ResultDto<>();
        resultDto.setCode("200");
        return resultDto;
    }
    @ResponseBody
    @RequestMapping("/changePassword")
    public ResultDto<Object> changePassword(@RequestBody Map<String,Object> map){
        String token=map.get("token").toString();
        User user=new User();
        user.setId(Integer.parseInt(map.get("id").toString()));
        user.setPassword(map.get("password").toString());
        userDao.updateById(user);
        ResultDto<Object> resultDto=new ResultDto<>();
        resultDto.setCode("200");
        return resultDto;
    }
    @ResponseBody
    @RequestMapping("/getUserMessage")
    public ResultDto<Object> getUserMessage(@RequestBody User user){
        System.out.println(user);
        User user1=null;
        if(user.getUsername()!=null){
            user1=userDao.selectOne(new QueryWrapper<User>().eq("username", user.getUsername()));
        }else{
            user1=userDao.selectById(user.getId());
        }
        ResultDto<Object> resultDto=new ResultDto<Object>();
        if(user1==null){
            resultDto.setMsg("用户名或id错误");
        }else{
            user1.setPassword("");
            resultDto.setCode("200");
            resultDto.setMsg("信息获取成功");
        }
        resultDto.setData(user1);
        return resultDto;
    }
}
