FROM openjdk:8-jre-alpine

ADD bin/* /cyberapp/
# override default config
ADD crawler.properties /cyberapp/crawler.properties
VOLUME /cyberdata

CMD ["java", "-jar", "/cyberapp/xchange-crawler.jar"]
