FROM openjdk:8 AS BUILD_IMAGE
ENV PROFILE dev
ENV APP_HOME=/root/dev/cake/
RUN mkdir -p $APP_HOME/src/main/java
WORKDIR $APP_HOME
COPY pom.xml mvnw mvnw.cmd $APP_HOME
COPY . .
RUN if ["$PROFILE" == "dev"]; \
 then ./mvnw install; \
 else ./mvnw install -DskipTests;\
 fi

FROM openjdk:8-jre
ENV PROFILE dev
WORKDIR /root/
COPY --from=BUILD_IMAGE /root/dev/cake/target/cakes.jar .
EXPOSE 80
CMD ["java","-jar", "-Dspring.profiles.active=${PROFILE}", "cakes.jar"]