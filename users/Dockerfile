FROM eclipse-temurin:17-jre-alpine as builder
WORKDIR application
ADD build/libs/users-1.0.0.jar ./
RUN java -Djarmode=layertools -jar users-1.0.0.jar extract

FROM eclipse-temurin:17-jre-alpine
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", \
"-Djava.security.egd=file:/dev/./urandom", \
"org.springframework.boot.loader.JarLauncher"]