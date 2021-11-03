import axios from 'axios';
import React from 'react';
import './App.css';
import * as dd from "dingtalk-jsapi";

class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            //内网穿透工具介绍:
            // https://developers.dingtalk.com/document/resourcedownload/http-intranet-penetration?pnamespace=app
            domain: "",
            corpId: '',
            authCode: '',
            userId: '',
            userName: ''
        }
    }

    render() {
        if (this.state.userId === '') {
            // 免登操作
            this.login();
        }
        return (
            // 主编写模块
            <div className="App">

            </div>
        );
    }

    //登录-获取corpId
    login() {
        axios.get(this.state.domain + "/getCorpId")
            .then(res => {
                if (res.data) {
                    this.loginAction(res.data);
                }
            }).catch(error => {
            alert("corpId err, " + JSON.stringify(error))
        })
    }

    //登录操作
    loginAction(corpId) {
        let _this = this;
        dd.runtime.permission.requestAuthCode({
            corpId: corpId,//企业 corpId
            onSuccess: function (res) {
                // 调用成功时回调
                axios.get(_this.state.domain + "/login?authCode=" + _this.state.authCode
                ).then(res => {
                    if (res && res.data.success) {
                        let userId = res.data.data.userId;
                        let userName = res.data.data.userName;
                        alert('登录成功，你好' + userName);
                        _this.setState({
                            userId: userId,
                            userName: userName
                        })
                    } else {
                        alert("login failed --->" + JSON.stringify(res));
                    }
                }).catch(error => {
                    alert("httpRequest failed --->" + JSON.stringify(error))
                })
            },
            onFail: function (err) {
                // 调用失败时回调
                alert("requestAuthCode failed --->" + JSON.stringify(err))
            }
        });
    }
}

export default App;
