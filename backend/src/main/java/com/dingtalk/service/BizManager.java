package com.dingtalk.service;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.dingboot.common.token.ITokenManager;
import com.aliyun.dingboot.common.util.AccessTokenUtil;
import com.aliyun.tea.*;
import com.aliyun.teautil.*;
import com.aliyun.teautil.models.*;
import com.aliyun.dingtalkdrive_1_0.*;
import com.aliyun.dingtalkdrive_1_0.models.*;
import com.aliyun.teaopenapi.*;
import com.aliyun.teaopenapi.models.*;
import com.aliyun.dingtalkdrive_1_0.Client;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMediaUploadRequest;
import com.dingtalk.api.request.OapiSmartworkHrmEmployeeQueryonjobRequest;
import com.dingtalk.api.request.OapiV2DepartmentListparentbyuserRequest;
import com.dingtalk.api.response.OapiMediaUploadResponse;
import com.dingtalk.api.response.OapiSmartworkHrmEmployeeQueryonjobResponse;
import com.dingtalk.api.response.OapiV2DepartmentListparentbyuserResponse;
import com.dingtalk.config.AppConfig;
import com.dingtalk.constant.UrlConstant;
import com.taobao.api.ApiException;
import com.taobao.api.FileItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 主业务service
 */
@Slf4j
@Service
public class BizManager {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private ITokenManager tokenManager;

    /**
     * 新建钉盘共享空间
     *
     * @param spaceName 空间名称
     * @param unionId   用户unionId
     * @return 空间信息
     * @throws TeaException error
     */
    public AddSpaceResponse createSpace(String spaceName, String unionId) throws Exception {
        try {
            // 获取access_token
            String accessToken = tokenManager.getAccessToken(appConfig.getAppKey(), appConfig.getAppSecret());
            // 构建请求
            Client client = createClient();
            AddSpaceHeaders addSpaceHeaders = new AddSpaceHeaders();
            addSpaceHeaders.xAcsDingtalkAccessToken = accessToken;
            AddSpaceRequest addSpaceRequest = new AddSpaceRequest()
                    .setName(spaceName)
                    .setUnionId(unionId);
            AddSpaceResponse addSpaceResponse = client.addSpaceWithOptions(addSpaceRequest, addSpaceHeaders, new RuntimeOptions());
            if (HttpStatus.OK.value() == Integer.parseInt(addSpaceResponse.headers.get("HttpCode"))) {
                return addSpaceResponse;
            }
        } catch (TeaException err) {
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                log.error("createSpace TeaException! code:{}, msg:{}", err.code, err.message);
            }
        } catch (Exception err) {
            TeaException err2 = new TeaException(err.getMessage(), err);
            if (!com.aliyun.teautil.Common.empty(err2.code) && !com.aliyun.teautil.Common.empty(err2.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.error("createSpace err! code:{}, msg:{}", err2.code, err2.message);
            }
        }
        return null;
    }

    /**
     * 钉盘创建文件
     *
     * @param spaceId  空间id
     * @param parentId 父目录id
     * @param mediaId  媒体文件id
     * @param fileName 文件名
     * @param unionId  用户unionId
     * @return file info
     * @throws Exception error
     */
    public AddFileResponse createFile(String spaceId, String parentId,
                                      String mediaId, String fileName, String unionId) {
        try {
            // 获取access_token
            String accessToken = tokenManager.getAccessToken(appConfig.getAppKey(), appConfig.getAppSecret());
            // 构建请求
            Client client = createClient();
            AddFileHeaders addFileHeaders = new AddFileHeaders();
            addFileHeaders.xAcsDingtalkAccessToken = accessToken;
            AddFileRequest addFileRequest = new AddFileRequest()
                    .setParentId(parentId)
                    .setFileType("file")
                    .setFileName(fileName)
                    .setMediaId(mediaId)
                    .setAddConflictPolicy("autoRename")
                    .setUnionId(unionId);

            AddFileResponse addFileResponse = client.addFileWithOptions(spaceId, addFileRequest, addFileHeaders, new RuntimeOptions());
            return addFileResponse;
        } catch (TeaException err) {
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.error("createFile err! code:{}, msg:{}", err.code, err.message);
            }

        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.error("createFile err! code:{}, msg:{}", err.code, err.message);
            }
            _err.printStackTrace();
        }
        return null;
    }

    /**
     * 下载钉盘文件
     *
     * @param spaceId 空间id
     * @param fileId  文件id
     * @param unionId 用户unionId
     * @throws Exception error
     */
    public GetDownloadInfoResponse download(String spaceId, String fileId, String unionId) {
        try {
            String accessToken = tokenManager.getAccessToken(appConfig.getAppKey(), appConfig.getAppSecret());
            Client client = createClient();
            GetDownloadInfoHeaders getDownloadInfoHeaders = new GetDownloadInfoHeaders();
            getDownloadInfoHeaders.xAcsDingtalkAccessToken = accessToken;
            GetDownloadInfoRequest getDownloadInfoRequest = new GetDownloadInfoRequest()
                .setUnionId(unionId);
            GetDownloadInfoResponse downloadInfoWithOptions = client.getDownloadInfoWithOptions(spaceId, fileId, getDownloadInfoRequest, getDownloadInfoHeaders, new RuntimeOptions());
            return downloadInfoWithOptions;
        } catch (TeaException err) {
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.error("download err! code:{}, msg:{}", err.code, err.message);
            }
        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.error("download err! code:{}, msg:{}", err.code, err.message);
            }
            _err.printStackTrace();
        }
        return null;
    }

    /**
     * 为同部门成员添加"查看/可下载"权限
     *
     * @param spaceId  空间id
     * @param fileId   文件id
     * @param userId userId
     * @param unionId  授权者unionId
     * @throws Exception error
     */
    public AddPermissionResponse addPermissions(String spaceId, String fileId, String unionId, String userId){
        try {
            String accessToken = tokenManager.getAccessToken(appConfig.getAppKey(), appConfig.getAppSecret());

            Long deptId = getDeptId(userId);

            Client client = createClient();
            AddPermissionHeaders addPermissionHeaders = new AddPermissionHeaders();
            addPermissionHeaders.xAcsDingtalkAccessToken = accessToken;
            AddPermissionRequest.AddPermissionRequestMembers members0 = new AddPermissionRequest.AddPermissionRequestMembers()
                    .setCorpId(appConfig.getCorpId())
                    .setMemberType("department")
                    .setMemberId(deptId.toString());
            AddPermissionRequest addPermissionRequest = new AddPermissionRequest()
                    .setRole("viewer")
                    .setMembers(Arrays.asList(members0))
                    .setUnionId(unionId);

            AddPermissionResponse addPermissionResponse = client.addPermissionWithOptions(spaceId, fileId, addPermissionRequest, addPermissionHeaders, new RuntimeOptions());
            return addPermissionResponse;
        } catch (TeaException err) {
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.error("addPermissions err! code:{}, msg:{}", err.code, err.message);
            }

        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.error("addPermissions err! code:{}, msg:{}", err.code, err.message);
            }
            _err.printStackTrace();
        }
        return null;
    }

    /**
     * 上传媒体文件
     *
     * @param type     文件类型
     * @param filePath 文件路径
     * @return mediaId 媒体文件id
     */
    public String mediaUpload(String type, String filePath) {
        try {
            // 获取access_token
            String accessToken = tokenManager.getAccessToken(appConfig.getAppKey(), appConfig.getAppSecret());
            DingTalkClient client = new DefaultDingTalkClient(UrlConstant.MEDIA_UPLOAD);
            OapiMediaUploadRequest req = new OapiMediaUploadRequest();
            req.setType(type);
            // 要上传的媒体文件
            FileItem item = new FileItem(filePath);
            req.setMedia(item);
            OapiMediaUploadResponse rsp = client.execute(req, accessToken);
            log.info("mediaUpload rsp:{}", rsp.getBody());
            if (rsp.getErrcode() == 0) {
                return rsp.getMediaId();
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取所在部门id
     *
     * @param userId
     * @return deptId
     */
    public Long getDeptId(String userId) throws ApiException {
        // 1. 获取access_token
        String accessToken = tokenManager.getAccessToken(appConfig.getAppKey(), appConfig.getAppSecret());

        DingTalkClient client = new DefaultDingTalkClient(UrlConstant.LIST_PARENT_BY_USER);
        OapiV2DepartmentListparentbyuserRequest req = new OapiV2DepartmentListparentbyuserRequest();
        req.setUserid(userId);
        OapiV2DepartmentListparentbyuserResponse rsp = client.execute(req, accessToken);
        log.info("getDeptId rsp body:{}", rsp.getBody());
        Long deptId = null;
        if (rsp.isSuccess()) {
            OapiV2DepartmentListparentbyuserResponse.DeptListParentByUserResponse result = rsp.getResult();
            List<OapiV2DepartmentListparentbyuserResponse.DeptParentResponse> parentList = result.getParentList();
            // 第一个为所在直属部门
            deptId = parentList.get(0).getParentDeptIdList().get(0);
            return deptId;
        }
        return null;
    }


    /**
     * 使用 Token 初始化账号Client
     *
     * @return Client
     * @throws Exception error
     */
    public Client createClient() throws Exception {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        return new Client(config);
    }

}
