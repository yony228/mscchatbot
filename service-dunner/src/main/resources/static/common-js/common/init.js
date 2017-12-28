$(document).ready(function () {

    $("#login").on("click", login);

    function login() {
        var username = $("#username").val();
        var password = $("#password").val();
        $.ajax({
            type: "POST",
            url: "../common/login",
            data: {username: username, password: password},
            dataType: "json",
            success: function (data) {
                if (data.success === "true") {
                    var message = "登录成功";
                    /*显示登录用户名*/
                    var cookies = document.cookie.split("=")[1];
                    var $wd = $(window.parent.document);
                    var $username = $wd.find("#username");
                    var $btn_logout = $wd.find("#btn_logout");
                    var $usericon = $wd.find(".usericon");
                    var $login = $wd.find("#login");
                    $usericon.show();
                    $username.text(cookies);
                    $login.hide();
                    //alert("登录登录成功！");
                    $("#login-warning").text(message);
                    // if (data.user.debt > 0)
                    window.location.href = "/common/chat?userName="+data.user.name;
                    // else
                    //     window.location.href = "/common/welcome";
                } else {
                    var warningMessage = data.message;//"请填入正确的用户名和密码并与组名相匹配!";
                    alert("登录失败！    "+ warningMessage);
                    $("#login-warning").text(warningMessage);
                }
            },
            error: function (v1, v2, v3) {
                alert(v1 + v2 + v3);
                alert("构建失败");
            }
        });
    }

})