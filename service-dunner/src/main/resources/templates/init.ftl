<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <title>Bootstrap 101 Template</title>

    <!-- Bootstrap -->
    <link href="../css/bootstrap.min.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="../common-js/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="../common-js/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body>
<div class="container" style="margin-top: 15px;">
    <div>
        <h1 class="page-header">登录</h1>
        <#--<b class="text-warning">${(sourceURL?contains("main/init"))?string("", "请先登录")}</b>-->
    </div>
    <form class="form-horizontal" role="form" action="" method="post">
        <div class="form-group">
            <div class="col-md-5">
                <input name="username" type="text" id="username" class="form-control" placeholder="用户名/Email">
            </div>
        </div>
        <div class="form-group">
            <div class="col-md-5">
                <input name="password" type="password" id="password" class="form-control" placeholder="密码">
            </div>
        </div>
        <div class="form-group">
            <div class="col-md-5">
                <button id="login" type="button" class="btn btn-primary submit">登录</button>
            <#--<a href="/campnou/freemarker/register.do">注册</a>-->
            </div>
        </div>
    </form>
    <#--<div>-->
        <#--<input style="display: none" id="hidden_sourceURL" value="${sourceURL}">-->
    <#--</div>-->
</div>



<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<!--<script src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script>-->
<script src="../common-js/jquery-3.2.1.min.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="../js/bootstrap.min.js"></script>

<script src="../common-js/common/init.js"></script>
</body>
</html>