FROM pengjinfei/tomcat
MAINTAINER pengjinfei <312055826@163.com>
COPY web-service.war /usr/local/tomcat/
RUN unzip web-service.war -d /usr/local/tomcat/webapps/ROOT
EXPOSE 20880
RUN rm -f /usr/local/tomcat/web-service.war