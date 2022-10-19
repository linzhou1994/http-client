package com.http.controller;


import com.alibaba.fastjson.JSONObject;
import com.http.client.LoginParam;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("login")
public class LoginController {


    @PostMapping("postLogin")
    public String postLogin(@RequestBody LoginParam param){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url","postLogin");
        jsonObject.put("name","rlt"+param.getName());
        jsonObject.put("password",param.getPassword());
        return jsonObject.toJSONString();
    }
    @PostMapping("post")
    public String post(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url","post");
        return jsonObject.toJSONString();
    }
    @GetMapping("getLogin")
    public String getLogin(String name, String password){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("url","getLogin");
        jsonObject.put("name",name);
        jsonObject.put("password",password);
        return jsonObject.toJSONString();
    }
    @GetMapping("getInt")
    public Integer getInt(){
        return 1994;
    }
    @PostMapping("getDouble")
    public Double getDouble(){
        return 12345.12D;
    }
}
