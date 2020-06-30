FROM openjdk:8-jdk-alpine
VOLUME /tmp
ENV TZ=Asia/Shanghai

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ADD service.jar /usr/share/myservice/myservice.jar
RUN bash -c 'touch /usr/share/myservice/myservice.jar'
ADD arthas-boot.jar /arthas-boot.jar
ENTRYPOINT java  -Xms512m   -Xmx1024m  -Xmn256m   -Xss256k   -XX:SurvivorRatio=8  -jar  /usr/share/myservice/myservice.jar
EXPOSE 8090