package com.dingtalk.controller;

import com.alibaba.fastjson.JSON;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.dingtalk.config.AppConfig;
import com.dingtalk.model.RpcServiceResult;
import com.dingtalk.service.UserManager;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 企业内部用户（免）登录
 */
@RestController
@Slf4j
public class LoginController {

    @Resource
    private UserManager userManager;


    @Autowired
    private AppConfig appConfig;

    /**
     * 欢迎页面, 检查后端服务是否启动
     *
     * @return
     */
    @RequestMapping(value = "/welcome", method = RequestMethod.GET)
    public String welcome() {
        return "welcome";
    }

    /**
     * 根据免登授权码, 获取登录用户身份
     *
     * @param authCode 免登授权码
     * @return
     */
    @GetMapping(value = "/login")
    public RpcServiceResult login(@RequestParam(value = "authCode") String authCode) {
        log.info("login request!!! authCode:{}", authCode);
        try {
            // 1. 获取用户
            OapiV2UserGetResponse.UserGetResponse userGetResponse = userManager.login(authCode);
            // 2. 返回用户身份
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("userId", userGetResponse.getUserid());
            resultMap.put("userName", userGetResponse.getName());
            resultMap.put("unionId", userGetResponse.getUnionid());
            log.info("login user:{}", JSON.toJSONString(resultMap));
            return RpcServiceResult.getSuccessResult(resultMap);
        } catch (ApiException e) {
            e.printStackTrace();
            return RpcServiceResult.getFailureResult(e.getErrCode(), e.getErrMsg());
        }  catch (Exception ex) {
            ex.printStackTrace();
            return RpcServiceResult.getFailureResult("-1", "login exception");
        }
    }

    /**
     * 获取corpId
     * @return
     */
    @RequestMapping(value = "/getCorpId", method = RequestMethod.GET)
    public String getCorpId() {
        return appConfig.getCorpId();
    }
}
