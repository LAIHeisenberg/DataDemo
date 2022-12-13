#!/usr/bin/bash
echo '打包前端代码'
cd ../src/main/resources/web-root/webapp
npm config set registry https://registry.npm.taobao.org
npm install
npm run build:prod
cd -
cd ../../
echo '打包后端代码'
mvn clean package
cd -
cp ../target/data-demo-web-1.0.0-SNAPSHOT-exec.jar ./

echo '设置DOCK_HOST=tcp://192.168.0.161:2375'
export DOCKER_HOST="tcp://192.168.0.161:2375"
echo '停止容器: datademo-web'
container_id=`docker container ps -a | grep 'datademo-web' | tr -s ' ' | cut -d ' ' -f 1`
docker container stop $container_id
echo '删除容器: datademo-web'
docker container rm $container_id
echo '执行docker build: '
docker build --no-cache -f Dockerfile -t datademo-web ./

rm -f data-demo-web-1.0.0-SNAPSHOT-exec.jar
echo '执行docker run: '
docker run -d -p 8090:8090 --name datademo-web -v /data/sqlite:/data/sqlite --link cipher_redis:cipher_redis --link cipher_mysql:cipher_mysql -d datademo-web
