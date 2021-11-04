package com.dingtalk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dingboot.common.token.ITokenManager;
import com.aliyun.dingboot.common.util.AccessTokenUtil;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
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
import com.dingtalk.api.request.OapiV2DepartmentListparentbyuserRequest;
import com.dingtalk.api.request.OapiV2DepartmentListsubRequest;
import com.dingtalk.api.response.OapiV2DepartmentListparentbyuserResponse;
import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import com.dingtalk.config.AppConfig;
import com.dingtalk.constant.UrlConstant;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 主业务service
 */
@Slf4j(topic = "bizManager")
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
    public AddSpaceResponse createSpace(String spaceName, String unionId) {
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
            log.info("createSpace rsp:{}", JSON.toJSONString(addSpaceResponse));
            return addSpaceResponse;
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
            log.info("createFile request:{}", JSON.toJSONString(addFileRequest));
            AddFileResponse addFileResponse = client.addFileWithOptions(spaceId, addFileRequest, addFileHeaders, new RuntimeOptions());
            log.info("createFile rsp:{}", JSON.toJSONString(addFileResponse));
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
     * 获取钉盘文件下载信息
     *
     * @param spaceId 空间id
     * @param fileId  文件id
     * @param unionId 用户unionId
     * @throws Exception error
     */
    public GetDownloadInfoResponse getDownloadInfo(String spaceId, String fileId, String unionId) {
        try {
            String accessToken = tokenManager.getAccessToken(appConfig.getAppKey(), appConfig.getAppSecret());
            Client client = createClient();
            GetDownloadInfoHeaders getDownloadInfoHeaders = new GetDownloadInfoHeaders();
            getDownloadInfoHeaders.xAcsDingtalkAccessToken = accessToken;
            GetDownloadInfoRequest getDownloadInfoRequest = new GetDownloadInfoRequest()
                    .setUnionId(unionId);
            GetDownloadInfoResponse downloadInfoWithOptions = client.getDownloadInfoWithOptions(spaceId, fileId, getDownloadInfoRequest, getDownloadInfoHeaders, new RuntimeOptions());
            log.info("getDownloadInfo rsp:{}", JSON.toJSONString(downloadInfoWithOptions));
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
     * 下载文件
     *
     * @param url
     * @param path
     * @param headers
     */
    public void downloadFile(String url, String path, Map<String, String> headers) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .headers(Headers.of(headers))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Sink sink = null;
                BufferedSink bufferedSink = null;
                try {
                    File dest = new File(path);
                    sink = Okio.sink(dest);
                    bufferedSink = Okio.buffer(sink);
                    long l = bufferedSink.writeAll(response.body().source());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedSink != null) {
                        bufferedSink.close();
                    }
                    if (sink != null) {
                        sink.close();
                    }
                }
            }
        });
    }


    /**
     * 为部门成员添加"查看/可下载"权限
     *
     * @param deptIds 部门id列表
     * @param spaceId 空间id
     * @param fileId  文件id
     * @param userId  userId
     * @param unionId 授权者unionId
     * @throws Exception error
     */
    public AddPermissionResponse addPermissions(List<String> deptIds, String spaceId, String fileId, String unionId, String userId) {
        try {
            String accessToken = tokenManager.getAccessToken(appConfig.getAppKey(), appConfig.getAppSecret());
            Client client = createClient();
            AddPermissionHeaders addPermissionHeaders = new AddPermissionHeaders();
            addPermissionHeaders.xAcsDingtalkAccessToken = accessToken;
            List<AddPermissionRequest.AddPermissionRequestMembers> members = new ArrayList<>(deptIds.size());
            deptIds.forEach(deptId -> {
                AddPermissionRequest.AddPermissionRequestMembers member = new AddPermissionRequest.AddPermissionRequestMembers()
                        .setCorpId(appConfig.getCorpId())
                        .setMemberType("department")
                        .setMemberId(deptId);
                members.add(member);
            });

            AddPermissionRequest addPermissionRequest = new AddPermissionRequest()
                    .setRole("viewer")
                    .setMembers(members)
                    .setUnionId(unionId);
            log.info("addPermissions request:{}", JSON.toJSONString(addPermissionRequest));
            AddPermissionResponse addPermissionResponse = client.addPermissionWithOptions(spaceId, fileId, addPermissionRequest, addPermissionHeaders, new RuntimeOptions());
            log.info("addPermissions rsp:{}", JSON.toJSONString(addPermissionResponse));
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
     * 获取文件上传信息
     *
     * @param spaceId  空间id
     * @param parentId 父目录id
     * @param fileName 文件名
     * @param fileSize 文件大小
     * @param unionId  用户unionId
     * @param MediaId  媒体资源id，为null时新建，不为null时刷新
     * @return
     */
    public GetUploadInfoResponse getUploadInfo(String spaceId, String parentId, String fileName, Long fileSize, String unionId, String MediaId) {
        try {
            String accessToken = tokenManager.getAccessToken(appConfig.getAppKey(), appConfig.getAppSecret());

            Client client = createClient();
            GetUploadInfoHeaders getUploadInfoHeaders = new GetUploadInfoHeaders();
            getUploadInfoHeaders.xAcsDingtalkAccessToken = accessToken;
            GetUploadInfoRequest getUploadInfoRequest = new GetUploadInfoRequest()
                    .setUnionId(unionId)
                    .setFileName(fileName)
                    .setFileSize(fileSize)
                    .setMd5("fekafjekfe")
                    .setAddConflictPolicy("autoRename")
                    .setMediaId(MediaId);

            GetUploadInfoResponse uploadInfoWithOptions = client.getUploadInfoWithOptions(spaceId, parentId, getUploadInfoRequest, getUploadInfoHeaders, new RuntimeOptions());
            log.info("getUploadInfo rsp:{}", JSON.toJSONString(uploadInfoWithOptions));
            return uploadInfoWithOptions;
        } catch (TeaException err) {
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.error("getUploadInfo err! code:{}, msg:{}", err.code, err.message);
            }

        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.error("getUploadInfo err! code:{}, msg:{}", err.code, err.message);
            }
            _err.printStackTrace();
        }
        return null;
    }

    /**
     * 上传文件到钉盘
     *
     * @param stsUploadInfo 上传文件信息
     * @param filePath      本地文件
     * @return
     */
    public PutObjectResult mediaUpload(GetUploadInfoResponseBody.GetUploadInfoResponseBodyStsUploadInfo stsUploadInfo, String filePath) {
        try {
            // 获取access_token
            String accessToken = tokenManager.getAccessToken(appConfig.getAppKey(), appConfig.getAppSecret());

            CredentialsProvider credentialsProvider = new DefaultCredentialProvider(stsUploadInfo.getAccessKeyId(), stsUploadInfo.getAccessKeySecret(), stsUploadInfo.getAccessToken());
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            // 需要HTTPS
            clientConfiguration.setProtocol(com.aliyun.oss.common.comm.Protocol.HTTPS);
            OSSClient ossClient = new OSSClient(stsUploadInfo.getEndPoint(), credentialsProvider, clientConfiguration);
            PutObjectRequest putObjectRequest = new PutObjectRequest(stsUploadInfo.getBucket(), stsUploadInfo.getMediaId(), new File(filePath));
            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
            // 关闭OSSClient
            ossClient.shutdown();
            return putObjectResult;
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取部门列表
     *
     * @param deptId 部门id
     * @return deptList 部门列表
     */
    public List<OapiV2DepartmentListsubResponse.DeptBaseResponse> getDeptList(Long deptId){
        try {
            // 获取access_token
            String accessToken = tokenManager.getAccessToken(appConfig.getAppKey(), appConfig.getAppSecret());

            DingTalkClient client = new DefaultDingTalkClient(UrlConstant.DEPT_LIST_SUB);
            OapiV2DepartmentListsubRequest req = new OapiV2DepartmentListsubRequest();
            req.setDeptId(deptId);
            req.setLanguage("zh_CN");
            OapiV2DepartmentListsubResponse rsp = client.execute(req, accessToken);
            System.out.println(rsp.getBody());
            log.info("getDeptList rsp body:{}", rsp.getBody());
            if (rsp.getErrcode() == 0) {
                return rsp.getResult();
            }
        } catch (ApiException e) {
            e.printStackTrace();
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
