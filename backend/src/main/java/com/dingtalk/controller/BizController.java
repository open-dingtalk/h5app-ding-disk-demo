package com.dingtalk.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dingtalkdrive_1_0.models.*;
import com.aliyun.oss.model.PutObjectResult;
import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import com.dingtalk.model.RpcServiceResult;
import com.dingtalk.service.BizManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 主业务Controller
 */
@RestController
@RequestMapping("/biz")
public class BizController {

    @Autowired
    BizManager bizManager;

    @PostMapping("/createSpace")
    public RpcServiceResult createSpace(@RequestBody String params){
        JSONObject object = JSONObject.parseObject(params);
        String spaceName = object.getString("spaceName");
        String unionId = object.getString("unionId");
        AddSpaceResponse space = bizManager.createSpace(spaceName, unionId);
        if(space == null){
            return RpcServiceResult.getFailureResult("-1", "创建空间失败");
        }
        return RpcServiceResult.getSuccessResult(space.getBody());
    }

    @PostMapping("/upload")
    public RpcServiceResult upload(HttpSession session,
                                   @RequestParam String spaceId,
                                   @RequestParam String unionId,
                                   @RequestParam String type,
                                   @RequestParam MultipartFile file) throws IOException {
        String parentId = "0";
        //文件存储路径 TODO
        String filePath = session.getServletContext().getRealPath("");
        System.out.println("filepath:" + filePath);
        String filename = file.getOriginalFilename();
        filePath += filename;
        file.transferTo(new File(filePath));
        // 获取文件上传信息
        GetUploadInfoResponse uploadInfo = bizManager.getUploadInfo(spaceId, parentId, filename, file.getSize(), unionId, null);
        if(uploadInfo == null){
            return RpcServiceResult.getFailureResult("-1", "获取上传信息失败");
        }
        GetUploadInfoResponseBody body = uploadInfo.getBody();
        GetUploadInfoResponseBody.GetUploadInfoResponseBodyStsUploadInfo stsUploadInfo = body.getStsUploadInfo();
        PutObjectResult putObjectResult = bizManager.mediaUpload(stsUploadInfo, filePath);
        if(putObjectResult == null){
            return RpcServiceResult.getFailureResult("-1", "上传文件失败");
        }
        // 添加文件到钉盘空间
        AddFileResponse addFileResponse = bizManager.createFile(spaceId, parentId, stsUploadInfo.getMediaId(), filename, unionId);
        if(addFileResponse == null){
            return RpcServiceResult.getFailureResult("-1", "添加文件异常");
        }
        return RpcServiceResult.getSuccessResult(addFileResponse.getBody());
    }

    @PostMapping("/addPermissions")
    public RpcServiceResult addPermissions(@RequestBody String params){
        System.out.println("addPermissions: " + params);
        JSONObject object = JSONObject.parseObject(params);
        String spaceId = object.getString("spaceId");
        String fileId = object.getString("fileId");
        String unionId = object.getString("unionId");
        String userId = object.getString("userId");
        List<String> deptIds = Arrays.asList(object.getString("deptIds"));
        AddPermissionResponse addPermissionResponse = bizManager.addPermissions(deptIds, spaceId, fileId, unionId, userId);
        if(addPermissionResponse == null){
            return RpcServiceResult.getFailureResult("-1", "添加权限失败");
        }
        return RpcServiceResult.getSuccessResult(addPermissionResponse.headers);
    }

    @PostMapping("/getDeptList")
    public RpcServiceResult getDeptList(@RequestBody String params){
        System.out.println("getDeptList:" + params);
        Long deptId = Long.parseLong(params);
        List<OapiV2DepartmentListsubResponse.DeptBaseResponse> deptList = bizManager.getDeptList(deptId);
        if(deptList == null){
            return RpcServiceResult.getFailureResult("-1", "获取部门列表失败");
        }
        if(deptList.isEmpty()){
            return RpcServiceResult.getFailureResult("-2", "没有更多子部门");
        }
        return RpcServiceResult.getSuccessResult(deptList);
    }

    @PostMapping("/download")
    public RpcServiceResult download(@RequestBody String params){
        JSONObject object = JSONObject.parseObject(params);
        String spaceId = object.getString("spaceId");
        String fileId = object.getString("fileId");
        String unionId = object.getString("unionId");
        GetDownloadInfoResponse downloadInfo = bizManager.getDownloadInfo(spaceId, fileId, unionId);
        if(downloadInfo == null){
            return RpcServiceResult.getFailureResult("-1", "下载失败");
        }

        System.out.println("download info:" + JSON.toJSONString(downloadInfo));
        // todo 下载
//        bizManager.downloadFile();
        return RpcServiceResult.getSuccessResult("");
    }

}
