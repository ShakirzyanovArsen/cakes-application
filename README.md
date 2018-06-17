# cakes-application

Запуск dev окружения (in memory база данных, тесты):
`docker build -t cakes . && docker run -p 8080:80 --env PROFILE=dev cakes `
Запуск production окружения (база данных храниться в контейнере, тесты не запускаются):
`docker build -t cakes . && docker run -p 8080:80 --env PROFILE=production cakes `