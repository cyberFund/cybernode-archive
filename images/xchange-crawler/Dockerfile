FROM openjdk:8-jre-alpine

ADD bin/* /cyberapp/
VOLUME /cyberdata

CMD ["java", "-jar", "/cyberapp/xchange-crawler.jar"]
