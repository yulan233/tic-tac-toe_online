package com.example.zouye_4.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.zouye_4.dao.GameInfoTwoDao;
import com.example.zouye_4.entity.GameInfoTwo;
import com.example.zouye_4.entity.User;
import com.example.zouye_4.pojo.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/api/gameInfo")
public class GameInfotwoController {
    @Autowired
    GameInfoTwoDao gameInfoTwoDao;

    @ResponseBody
    @RequestMapping("/getGameInfoById")
    public ResultDto<Object> getGameInfoById(@RequestBody User user1){
        ResultDto<Object> resultDto=new ResultDto<>();
//        System.out.println(user1);
        QueryWrapper<GameInfoTwo> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("first_id",user1.getId()).or().eq("second_id",user1.getId()).orderByDesc("time").last("limit 5");
        List<GameInfoTwo> list= gameInfoTwoDao.selectList(queryWrapper);
//        System.out.println(list);
        resultDto.setData(list);
        resultDto.setCode("200");
        return resultDto;
    }

    @ResponseBody
    @RequestMapping("/getGameInfoByUsername")
    public ResultDto<Object> getGameInfoByUsername(@RequestBody User user1){
        ResultDto<Object> resultDto=new ResultDto<>();
//        System.out.println(user1);
        QueryWrapper<GameInfoTwo> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("first_name",user1.getUsername()).or().eq("second_name",user1.getUsername()).orderByDesc("time");
        List<GameInfoTwo> list= gameInfoTwoDao.selectList(queryWrapper);
//        System.out.println(list);
        resultDto.setData(list);

        resultDto.setCode("200");
        return resultDto;
    }
}
