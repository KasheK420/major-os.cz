<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8"/>
        <title>Login</title>
        <style>
        .marco{
            margin: auto;
            text-align: center;
            height: calc(100% - 200px);
            display: flex;
            flex-direction: column;
            justify-content: center;
        }
        .marco div{
            margin-bottom: 7px;
        }
        html, 
        body {
            height: 100%;
        }
        </style>
    </head>
    <body>
        <div class="marco">
            <form action="/register" method="POST">
                @csrf
                <div>Register</div>
                <div><input type="text" placeholder="Name" name='name' required></div>
                <div><input type="email" placeholder="Email" name='email' required></div>
                <div><input type="password" placeholder="Password" name='password' required></div>
                <div><input type="password" placeholder="Password Confirmation" name='password_confirmation' required></div>
                @if($errors->any())
                    <div>{{ implode('', $errors->all(':message')) }}</div>
                @endif
                <div><button type="submit">Enviar</button></div>
            </form>
        </div>
    </body>
</html>