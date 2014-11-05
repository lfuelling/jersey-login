
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="img/favicon.ico">

    <title>Secured Homepage</title>

    <!-- Bootstrap core CSS -->
    <link href="css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="css/main.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body>
<div id="loader"></div>
<div id="page">
    <a href="https://github.com/lfuelling/jersey-login"><img style="z-index: 9999; position: absolute; top: 0; right: 0; border: 0;" src="https://camo.githubusercontent.com/38ef81f8aca64bb9a64448d0d70f1308ef5341ab/68747470733a2f2f73332e616d617a6f6e6177732e636f6d2f6769746875622f726962626f6e732f666f726b6d655f72696768745f6461726b626c75655f3132313632312e706e67" alt="Fork me on GitHub" data-canonical-src="https://s3.amazonaws.com/github/ribbons/forkme_right_darkblue_121621.png"></a>
    <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">LoginPage</a>
        </div>
        <div id="navbar" class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
                <li class="active"><a href="#">Home</a></li>
                <li><a href="https://github.com/lfuelling/jersey-login">About</a></li>
                <li><a href="https://twitter.com/kellagroup">Contact</a></li>
            </ul>
        </div><!--/.nav-collapse -->
    </div>
</nav>

<div class="container">

    <div class="starter-template">
        <h1>Secured area</h1>
        <p class="lead">Everyone who isn't logged in (or manually set the <code>token</code> Cookie) will be redirected to the login page.</p>
    </div>

</div><!-- /.container -->
</div><!-- /#page -->

<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="js/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/jquery.cookies.min.js"></script>
<script src="js/jquery.foggy.min.js"></script>
<script src="js/spin.min.js"></script>
<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<script src="js/ie10-viewport-bug-workaround.js"></script>
<script type="text/javascript">
    $(document).ready(function(){
        $('#page').foggy();
        var opts = {
            lines: 17, // The number of lines to draw
            length: 40, // The length of each line
            width: 2, // The line thickness
            radius: 60, // The radius of the inner circle
            corners: 1, // Corner roundness (0..1)
            rotate: 0, // The rotation offset
            direction: 1, // 1: clockwise, -1: counterclockwise
            color: '#000', // #rgb or #rrggbb or array of colors
            speed: 1.5, // Rounds per second
            trail: 64, // Afterglow percentage
            shadow: false, // Whether to render a shadow
            hwaccel: false, // Whether to use hardware acceleration
            className: 'spinner', // The CSS class to assign to the spinner
            zIndex: 2e9, // The z-index (defaults to 2000000000)
            top: '50%', // Top position relative to parent
            left: '50%' // Left position relative to parent
        };
        var target = document.getElementById('loader');
        var spinner = new Spinner(opts).spin(target);
        var token = $.cookies.get("token");
        if (token == null) {
            window.location.href = "index.jsp";
        } else {
            $.ajax({
                url: "webapi/login/check",
                type: "POST",
                data: token,
                contentType: "text/plain; charset=utf-8",
                dataType: "text",
                success: function (data) {
                    if (data == "false") {
                        $.cookies.del("token");
                    } else if (data == "true") {
                        $('#page').foggy(false);
                        spinner.stop();
                        $('#loader').hide();
                    }
                }
            });
        }
    });
</script>

</body>
</html>
