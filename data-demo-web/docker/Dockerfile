FROM maven:latest
ENV TZ=Asia/Shanghai
ENV APP_PATH=/data

WORKDIR $APP_PATH
COPY ./data-demo-web-1.0.0-SNAPSHOT-exec.jar ./

ENTRYPOINT java -jar "$APP_PATH/data-demo-web-1.0.0-SNAPSHOT-exec.jar" --spring.profiles.active=docker