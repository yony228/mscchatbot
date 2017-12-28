<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>聊天室</title>
    <link type="text/css" rel="stylesheet" href="../css/style.css">
    <script type="text/javascript" src="../js/jquery.min.js"></script>
</head>

<body>
<div class="chatbox">
    <div class="chat_top fn-clear">
        <div class="logo">
        <img src="../images/logo.png" width="132" height="60"  alt=""/>
        </div>
        <div class="uinfo fn-clear">
            <div class="uface"><img src="../images/hetu.jpg" width="40" height="40"  alt=""/></div>
            <div class="uname">
                ${userName}<i class="fontico down"></i>
                <ul class="managerbox">
                    <li><a href="../common/logout"><i class="fontico logout"></i>退出登录</a></li>
                </ul>
            </div>
        </div>
    </div>
    <div class="chat_message fn-clear">
        <div class="chat_left" style="width: 97%">
            <div class="message_box" id="message_box">
                <div class="msg_item fn-clear">
                    <div class="item_left">
                        <div class="name_time">信美分期客服小崔</div>
                        <div class="msg">${initMessage}</div>
                    </div>
                    <div class="uface"><img src="../images/53f442834079a.jpg" width="40" height="40"  alt=""/></div>
                </div>
            </div>
            <div class="write_box">
                <textarea id="message" name="message" class="write_area" placeholder="说点啥吧..."></textarea>
                <input type="hidden" name="fromname" id="fromname" value="${userName}" />
                <div class="facebox fn-clear">
                    <div class="expression"></div>
                    <div class="chat_type" id="chat_type">正在聊天...</div>
                    <button name="" class="sub_but">提 交</button>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function(e) {
        $('#message_box').scrollTop($("#message_box")[0].scrollHeight + 20);
        $('.uname').hover(
                function(){
                    $('.managerbox').stop(true, true).slideDown(100);
                },
                function(){
                    $('.managerbox').stop(true, true).slideUp(100);
                }
        );

        var fromname = $('#fromname').val();
        $('.sub_but').click(function(event){
            sendMessage(event, fromname);
        });

        /*按下按钮或键盘按键*/
        $("#message").keydown(function(event){
            var e = window.event || event;
            var k = e.keyCode || e.which || e.charCode;
            //按下ctrl+enter发送消息
            if((event.ctrlKey && (k == 13 || k == 10) )){
                sendMessage(event, fromname);
            }
        });
    });
    function sendMessage(event, from_name){
        var msg = $("#message").val();

        $.ajax({
            type: "POST",
            url: "../chat/message",
            data: {
                message: msg
            },
            dataType: "json",
            success: function (data) {
                var htmlData =   '<div class="msg_item fn-clear">'
                        + '   <div class="uface own"><img src="../images/hetu.jpg" width="40" height="40"  alt=""/></div>'
                        + '   <div class="item_right">'
                        + '     <div class="name_time">' + from_name + '</div>'
                        + '     <div class="msg own">' + msg + '</div>'
                        + '   </div>'
                        + '</div>';

                htmlData +=   '<div class="msg_item fn-clear">'
                        + '   <div class="uface"><img src="../images/53f442834079a.jpg" width="40" height="40"  alt=""/></div>'
                        + '   <div class="item_left">'
                        + '     <div class="name_time">信美分期客服小崔</div>'
                        + '     <div class="msg">' + data.msg + '</div>'
                        + '   </div>'
                        + '</div>';

                $("#message_box").append(htmlData);
                $('#message_box').scrollTop($("#message_box")[0].scrollHeight + 20);
                $("#message").val('');
            }
        });
    }
</script>
</body>
</html>