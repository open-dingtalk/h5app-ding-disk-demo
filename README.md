# 钉盘功能-Demo

> - 此demo主要展示钉盘相关功能，包括创建钉盘空间、上传文件到钉盘、为部门授权钉盘文件、下载钉盘文件等。
> - 项目结构
    >   - rear-end：后端模块，springboot构建，功能接口功能包括：新建钉盘空间、获取文件上传信息、上传文件、添加文件、获取部门列表、添加权限、获取文件下载信息、下载文件等。
>   - front-end：前端模块，react构建，场景功能包括：jsapi获取免登授权码、展示页面、上传文件、展示部门、下载文件等。
>



### 研发环境准备

1. 需要有一个钉钉注册企业，如果没有可以创建：https://oa.dingtalk.com/register_new.htm#/

2. 成为钉钉开发者，参考文档：https://developers.dingtalk.com/document/app/become-a-dingtalk-developer

3. 登录钉钉开放平台后台创建一个H5应用： https://open-dev.dingtalk.com/#/index

4. 配置应用

   配置开发管理，参考文档：https://developers.dingtalk.com/document/app/configure-orgapp

    - **此处配置“应用首页地址”和“PC端首页地址”需公网地址，若无公网ip，可使用钉钉内网穿透工具：**

      https://developers.dingtalk.com/document/resourcedownload/http-intranet-penetration

![image-20210706171740868](https://img.alicdn.com/imgextra/i4/O1CN01C9ta8k1L3KzzYEPiH_!!6000000001243-2-tps-953-517.png)



配置相关权限：https://developers.dingtalk.com/document/app/address-book-permissions

本demo使用接口相关权限：

“成员信息读取权限”、“通讯录部门信息读权限”、“钉盘应用盘空间写权限”、“钉盘应用文件写权限”、“钉盘应用授权信息写权限”、“钉盘应用文件下载信息读权限”、“钉盘应用文件上传信息读权限”

![image-20210706172027870](https://img.alicdn.com/imgextra/i3/O1CN016WCr6428wDdBhkWi6_!!6000000007996-2-tps-1358-571.png)



### 运行

**下载本项目至本地**

```shell
git clone https://github.com/open-dingtalk/h5app-ding-disk-demo.git
```

### 获取相应参数

获取到以下参数，修改后端application.yaml

```yaml
app:
  app_key: *****
  app_secret: *****
  agent_id: *****
  corp_id: *****
```

参数获取方法：登录开发者后台

1. 获取corpId：https://open-dev.dingtalk.com/#/index
2. 进入应用开发-企业内部开发-点击进入应用-基础信息-获取appKey、appSecret、agentId

### 修改前端页面

**打开项目，命令行中执行以下命令，编译打包生成build文件**

```shell
cd front-end
npm install
npm run build
```

**将打包好的静态资源文件放入后端**

![image-20210706173224172](https://img.alicdn.com/imgextra/i2/O1CN01QLp1Qw1TCVrPddfjZ_!!6000000002346-2-tps-322-521.png)

### 启动项目

- 启动springboot
- 移动端钉钉点击工作台，找到应用，进入应用

### 页面展示

![](https://img.alicdn.com/imgextra/i2/O1CN014PdBxs1hXx38ahiGM_!!6000000004288-2-tps-300-278.png)

主页面，点击创建空间

![](https://img.alicdn.com/imgextra/i2/O1CN018HEf5c20vHvgcI25f_!!6000000006911-2-tps-299-238.png)

点击上传图片按钮，选择图片

![](https://img.alicdn.com/imgextra/i1/O1CN01qCodzj1Mb3oAvLe0B_!!6000000001452-2-tps-300-87.png)

 上传完成

 ![](https://img.alicdn.com/imgextra/i2/O1CN018WrPiE1gPQ3M6AEjp_!!6000000004134-2-tps-300-189.png)

点击授权按钮展示部门列表，选择部门进行授权

![](https://img.alicdn.com/imgextra/i2/O1CN01HA0Tkd1a92C1StxeK_!!6000000003286-2-tps-249-52.png)

点击下载按钮，跳转到浏览器下载钉盘的图片



### **参考文档**

1. 获取企业内部应用access_token，文档链接：https://developers.dingtalk.com/document/app/obtain-orgapp-token
2. 新建空间，文档链接：https://developers.dingtalk.com/document/app/new-space
3. 获取文件上传信息，文档链接：https://developers.dingtalk.com/document/app/obtain-upload-information
4. 文件上传流程，文档链接：https://developers.dingtalk.com/document/app/example-of-the-file-upload-sdk
5. 添加文件，文档链接：https://developers.dingtalk.com/document/app/add-file
6. 获取部门列表，文档链接：https://developers.dingtalk.com/document/app/obtain-the-department-list-v2
7. 添加权限，文档链接：https://developers.dingtalk.com/document/app/add-permissions
8. 获取文件下载信息，文档链接：https://developers.dingtalk.com/document/app/obtain-download-file-info
9. 文件下载流程，文档链接：https://developers.dingtalk.com/document/app/file-download-process
