package com.dingtalk.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dingtalkdrive_1_0.models.*;
import com.aliyun.oss.model.PutObjectResult;
import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import com.dingtalk.model.RpcServiceResult;
import com.dingtalk.service.BizManager;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * 主业务Controller
 */
@Slf4j
@RestController
@RequestMapping("/biz")
public class BizController {

    @Autowired
    BizManager bizManager;

    @Autowired
    BlockingQueue<Long> blockingQueue;

    @PostMapping("/createSpace")
    public RpcServiceResult createSpace(@RequestBody String params) {
        JSONObject object = JSONObject.parseObject(params);
        String spaceName = object.getString("spaceName");
        String unionId = object.getString("unionId");
        AddSpaceResponse space = bizManager.createSpace(spaceName, unionId);
        if (space == null) {
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
        //文件存储路径
        String filePath = session.getServletContext().getRealPath("");
        log.info("filepath upload:{}", filePath);
        String filename = file.getOriginalFilename();
        filePath += filename;
        file.transferTo(new File(filePath));
        // 获取文件上传信息
        GetUploadInfoResponse uploadInfo = bizManager.getUploadInfo(spaceId, parentId, filename, file.getSize(), unionId, null);
        if (uploadInfo == null) {
            return RpcServiceResult.getFailureResult("-1", "获取上传信息失败");
        }
        GetUploadInfoResponseBody body = uploadInfo.getBody();
        GetUploadInfoResponseBody.GetUploadInfoResponseBodyStsUploadInfo stsUploadInfo = body.getStsUploadInfo();
        PutObjectResult putObjectResult = bizManager.mediaUpload(stsUploadInfo, filePath);
        if (putObjectResult == null) {
            return RpcServiceResult.getFailureResult("-1", "上传文件失败");
        }
        // 添加文件到钉盘空间
        AddFileResponse addFileResponse = bizManager.createFile(spaceId, parentId, stsUploadInfo.getMediaId(), filename, unionId);
        if (addFileResponse == null) {
            return RpcServiceResult.getFailureResult("-1", "添加文件异常");
        }
        return RpcServiceResult.getSuccessResult(addFileResponse.getBody());
    }

    @PostMapping("/addPermissions")
    public RpcServiceResult addPermissions(@RequestBody String params) {
        JSONObject object = JSONObject.parseObject(params);
        String spaceId = object.getString("spaceId");
        String fileId = object.getString("fileId");
        String unionId = object.getString("unionId");
        String userId = object.getString("userId");
        List<String> deptIds = Arrays.asList(object.getString("deptIds"));
        AddPermissionResponse addPermissionResponse = bizManager.addPermissions(deptIds, spaceId, fileId, unionId, userId);
        if (addPermissionResponse == null) {
            return RpcServiceResult.getFailureResult("-1", "添加权限失败");
        }
        return RpcServiceResult.getSuccessResult(addPermissionResponse.headers);
    }

    @PostMapping("/getDeptList")
    public RpcServiceResult getDeptList(@RequestBody String params) {
        Long deptId = Long.parseLong(params);
        List<OapiV2DepartmentListsubResponse.DeptBaseResponse> deptList = bizManager.getDeptList(deptId);
        if (deptList == null) {
            return RpcServiceResult.getFailureResult("-1", "获取部门列表失败");
        }
        if (deptList.isEmpty()) {
            return RpcServiceResult.getFailureResult("-2", "没有更多子部门");
        }
        return RpcServiceResult.getSuccessResult(deptList);
    }

    @GetMapping("/download")
    public void download(HttpSession session, HttpServletResponse response,
                         @RequestParam String spaceId,
                         @RequestParam String unionId,
                         @RequestParam String fileId) throws IOException, InterruptedException {
        String fileName = "download.png";
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        GetDownloadInfoResponse downloadInfoResponse = bizManager.getDownloadInfo(spaceId, fileId, unionId);
        if (downloadInfoResponse == null) {
            log.info("responseWriter get download info fail!!!");
        }
        GetDownloadInfoResponseBody.GetDownloadInfoResponseBodyDownloadInfo downloadInfo = downloadInfoResponse.getBody().getDownloadInfo();
        log.info("download info:{}", JSON.toJSONString(downloadInfo));
        // 下载到本地到文件路径
        String filePath = session.getServletContext().getRealPath("");

        filePath = filePath + fileName;
        log.info("filepath download:{}", filePath);
        downloadFile(downloadInfo, filePath);
        Long size = blockingQueue.poll(1, TimeUnit.MINUTES);
        if(size != null && size > 0){
            responseWriter(response, filePath, fileName);
        }
    }

    /**
     * 下载文件
     *
     * @param downloadInfo 登录信息
     * @param filepath     文件路径
     */
    public void downloadFile(GetDownloadInfoResponseBody.GetDownloadInfoResponseBodyDownloadInfo downloadInfo, String filepath) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadInfo.getResourceUrl())
                .headers(Headers.of((Map<String, String>) downloadInfo.getHeaders()))
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
                    File dest = new File(filepath);
                    sink = Okio.sink(dest);
                    bufferedSink = Okio.buffer(sink);
                    long size = bufferedSink.writeAll(response.body().source());
                    log.info("downloadFile filesize:{}", size);
                    if (size > 0) {
                        blockingQueue.offer(size);
                    }
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
     * 文件下载给浏览器
     *
     * @param response response
     * @param filePath 文件路径
     * @param fileName 文件名
     * @throws IOException
     */
    public void responseWriter(HttpServletResponse response, String filePath, String fileName) throws IOException {
        log.info("responseWriter is null :{}", response == null);
        File file = new File(filePath);
        if (!file.exists()) {
            log.info("responseWriter file exists");
        }
        InputStream in = new FileInputStream(filePath);
        int len = 0;
        byte[] buffer = new byte[1024];
        OutputStream out = response.getOutputStream();
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        in.close();
    }
}
