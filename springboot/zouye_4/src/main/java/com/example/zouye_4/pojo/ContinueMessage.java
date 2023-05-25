package com.example.zouye_4.pojo;

import lombok.Data;

import java.util.Map;

@Data
public class ContinueMessage {
    private String code;
    private String msg;
    private Map<String,String> data;
}
