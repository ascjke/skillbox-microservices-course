FROM eclipse-temurin:17-jre-alpine as builder
WORKDIR application
ADD target/minmol-grants-0.0.1-SNAPSHOT.jar ./
RUN java -Djarmode=layertools -jar minmol-grants-0.0.1-SNAPSHOT.jar extract

FROM eclipse-temurin:17-jre-alpine
WORKDIR application
COPY ./fonts ./fonts
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", \
"-Djava.security.egd=file:/dev/./urandom", \
"org.springframework.boot.loader.JarLauncher"]