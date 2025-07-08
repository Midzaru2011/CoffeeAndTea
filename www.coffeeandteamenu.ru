# Редирект с HTTP на HTTPS
server {
    listen 80;
    server_name coffeeandteamenu.ru www.coffeeandteamenu.ru;

    return 301 https://www.coffeeandteamenu.ru$request_uri; 
}

# HTTPS сервер для обоих доменов
server {
    listen 443 ssl;
    server_name coffeeandteamenu.ru www.coffeeandteamenu.ru;

    ssl_certificate /etc/letsencrypt/live/www.coffeeandteamenu.ru/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/www.coffeeandteamenu.ru/privkey.pem;

    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    # Заголовки безопасности
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Content-Type-Options nosniff;
    add_header X-Frame-Options "SAMEORIGIN";
    add_header 'Content-Security-Policy' 'upgrade-insecure-requests';
    add_header X-XSS-Protection "1; mode=block";

    location / {
        proxy_pass http://localhost:8443;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        proxy_pass_request_headers on;
        proxy_cookie_path / "/; Path=/";
    }
}