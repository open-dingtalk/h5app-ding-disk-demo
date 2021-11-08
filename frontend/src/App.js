import axios from "axios"
import React from "react"
import "./App.css"
import "antd/dist/antd.min.css"
import { Button, message, Upload, Checkbox } from "antd"
import { UploadOutlined } from "@ant-design/icons"
import * as dd from "dingtalk-jsapi"

class App extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
      //内网穿透工具介绍:
      // https://developers.dingtalk.com/document/resourcedownload/http-intranet-penetration?pnamespace=app
      domain: "",
      corpId: "",
      authCode: "",
      userId: "",
      userName: "",
      unionId: "",
      spaceId: "",
      deptId: "",
      showType: 0,
      deptList: [],
      targetDeptList: [],
    }
  }

  render() {
    let data = {
      unionId: this.state.unionId,
      spaceId: this.state.spaceId,
      type: "image",
    }
    if (this.state.userId === "") {
      // 免登操作
      this.login()
    }
    return (
      <div className="content">
        <div className="header">
          <img
            src="https://img.alicdn.com/imgextra/i3/O1CN01Mpftes1gwqxuL0ZQE_!!6000000004207-2-tps-240-240.png"
            className="headImg"
          />
          钉钉模板
        </div>
        <div className="App">
          {this.state.showType === 0 && (
            <div>
              <h2>钉盘功能</h2>
              <p>
                <Button type="primary" onClick={() => this.createSpace()}>
                  创建钉盘空间
                </Button>
              </p>
              <p>
                <Upload
                  action="/biz/upload"
                  data={data}
                  name="file"
                  onChange={this.uploadFile}
                >
                  <Button type="primary" icon={<UploadOutlined />}>
                    上传图片到钉盘空间
                  </Button>
                </Upload>
              </p>
              <p>
                <Button type="primary" onClick={(e) => this.showDept(e, 1)}>
                  授权部门该文件查看/可下载权限
                </Button>
              </p>
              <p>
                <Button type="primary" onClick={() => this.download()}>
                  下载钉盘空间的图片
                </Button>
              </p>
            </div>
          )}
          {this.state.showType === 1 && (
            <div>
              <h2>部门列表</h2>
              {this.state.deptList.map((item, i) => (
                <p key={i}>
                  <Checkbox
                    value={item.deptId}
                    name={item.name}
                    onChange={(e) => this.addDeptToList(e)}
                  >
                    {item.name}
                  </Checkbox>
                  <span onClick={(e) => this.showDept(e, item.deptId)}>⇢</span>
                </p>
              ))}
              <Button type="primary" onClick={() => this.addPermissions()}>
                为选中部门添加查看/可下载权限
              </Button>
              <br />
              <a onClick={() => this.setState({ showType: 0 })}>←返回</a>
            </div>
          )}
          {this.state.showType === 2 && (
            <div>
              <h2>创建钉盘空间</h2>
              <input type="text" name={""} />
              <br />
              <a onClick={() => this.setState({ showType: 0 })}>←返回</a>
            </div>
          )}
        </div>
      </div>
    )
  }

  addDeptToList(e) {
    let list = this.state.targetDeptList
    let deptId = e.target.value
    console.log("------deptId------", deptId)
    if (list.indexOf(deptId) === -1) {
      list.push(deptId)
      this.setState({
        targetDeptList: list,
      })
    }
    console.log("------list------", list)
  }

  download() {
    const fileId = sessionStorage.getItem("fileId")
    const spaceId = this.state.spaceId
    const unionId = this.state.unionId
    window.open(
      this.state.domain +
        "/biz/download?fileId=" +
        fileId +
        "&spaceId=" +
        spaceId +
        "&unionId=" +
        unionId
    )
  }

  showDept(e, deptId) {
    axios
      .post(this.state.domain + "/biz/getDeptList", JSON.stringify(deptId), {
        headers: { "Content-Type": "application/json" },
      })
      .then((res) => {
        if (res.data.success) {
          this.setState({
            deptList: res.data.data,
            showType: 1,
          })
        } else {
          message.error(res.data.errorMsg)
        }
      })
      .catch((error) => {
        alert("showDept err, " + JSON.stringify(error))
      })
  }

  addPermissions() {
    let data = {
      fileId: sessionStorage.getItem("fileId"),
      spaceId: this.state.spaceId,
      userId: this.state.userId,
      unionId: this.state.unionId,
      deptIds: this.state.targetDeptList.join(","),
    }
    axios
      .post(this.state.domain + "/biz/addPermissions", JSON.stringify(data), {
        headers: { "Content-Type": "application/json" },
      })
      .then((res) => {
        if (res.data.success) {
          this.setState({
            deptList: [],
            targetDeptList: [],
            showType: 0,
          })
          message.success("添加权限成功！")
        } else {
          message.error(res.data.errorMsg)
        }
      })
      .catch((error) => {
        alert("addPermissions err, " + JSON.stringify(error))
      })
  }

  uploadFile(info) {
    if (info.file.status !== "uploading") {
      console.log(info.file, info.fileList)
    }
    if (
      info.file.response !== null &&
      info.file.response !== undefined &&
      info.file.response.success === true
    ) {
      sessionStorage.setItem("fileId", info.file.response.data.fileId)
      console.log("fileId: " + info.file.response.data.fileId)
      message.success(`${info.file.name} 文件上传成功`)
    } else if (
      info.file.response !== null &&
      info.file.response !== undefined &&
      info.file.response.success === false
    ) {
      message.error(`${info.file.response.errorMsg}`)
    }
  }

  chooseFile(e) {
    let value = e.target.value
    this.state({
      file: value,
    })
  }

  createSpace() {
    let data = {
      unionId: this.state.unionId,
      spaceName: "一号空间",
    }
    axios
      .post(this.state.domain + "/biz/createSpace", JSON.stringify(data), {
        headers: { "Content-Type": "application/json" },
      })
      .then((res) => {
        if (res.data.success) {
          this.setState({
            spaceId: res.data.data.spaceId,
          })
          message.success("创建钉盘空间成功！")
        } else {
          message.error(res.data.errorMsg)
        }
      })
      .catch((error) => {
        alert("createSpace err, " + JSON.stringify(error))
      })
  }

  //登录-获取corpId
  login() {
    axios
      .get(this.state.domain + "/getCorpId")
      .then((res) => {
        if (res.data) {
          this.loginAction(res.data)
        }
      })
      .catch((error) => {
        alert("corpId err, " + JSON.stringify(error))
      })
  }

  //登录操作
  loginAction(corpId) {
    let _this = this
    dd.runtime.permission.requestAuthCode({
      corpId: corpId, //企业 corpId
      onSuccess: function (res) {
        // 调用成功时回调
        axios
          .get(_this.state.domain + "/login?authCode=" + res.code)
          .then((res) => {
            if (res && res.data.success) {
              let userId = res.data.data.userId
              let userName = res.data.data.userName
              let unionId = res.data.data.unionId
              alert("登录成功，你好" + userName)
              _this.setState({
                userId: userId,
                userName: userName,
                unionId: unionId,
              })
            } else {
              alert("login failed --->" + JSON.stringify(res))
            }
          })
          .catch((error) => {
            alert("httpRequest failed --->" + JSON.stringify(error))
          })
      },
      onFail: function (err) {
        // 调用失败时回调
        alert("requestAuthCode failed --->" + JSON.stringify(err))
      },
    })
  }
}

export default App
