package com.example.zouye_4.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultDto<T> {
    //错误码
    private String code;
    //提示信息
    private String msg;
    //token
    private String token;
    //数据
    private T data;
}
