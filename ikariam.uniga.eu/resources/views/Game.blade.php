<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <meta name="csrf-token" content="{{ csrf_token() }}">
    <meta name="user-notification" content="user_{{ Auth::id() }}">
    <title>Glarium</title>
    <script src="https://js.pusher.com/5.0/pusher.min.js"></script>

    <style>
        body{
            margin:0px
        }
    </style>
</head>
<body>
    <div id="app">
        <Game></Game>
    </div>
    <script src='/dist/main.js?v=1.0.0'></script>
</body>
</html>
