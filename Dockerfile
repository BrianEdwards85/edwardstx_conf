FROM openjdk:8
RUN mkdir /etc/serivce 
COPY ./target/conf.jar /srv/conf.jar
WORKDIR /srv

EXPOSE 5005

ENTRYPOINT /usr/bin/java -Dconfig="/etc/service/edwardstx_conf.edn" -jar /srv/conf.jar


