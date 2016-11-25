# FROM tomcat:8.0.30
FROM java:8u66-jdk

MAINTAINER Andrey Lobarev <nxtpool@gmail.com.com>

# installing git
RUN apt-get update && apt-get install git -y
RUN mkdir /git
WORKDIR /git
RUN git clone https://github.com/cyberFund/cybernode.git

#repository cloned

ENV MAVEN_VERSION 3.3.3

RUN curl -fsSL http://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
  && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/

#ready for maven command

WORKDIR /git/cybernode/bitcoinj
RUN mvn clean install

WORKDIR /git/cybernode
RUN mvn clean install -DskipTests

ENV MAVEN_OPTS -javaagent:/root/.m2/repository/org/springframework/spring-instrument/4.3.3.RELEASE/spring-instrument-4.3.3.RELEASE.jar -Xmx2048m -Dorg.eclipse.jetty.annotations.maxWait=120
EXPOSE 80
CMD ["mvn", "-Djetty.http.port=80", "jetty:run"]


