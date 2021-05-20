ARG BASEIMAGE
FROM $BASEIMAGE
WORKDIR /opt
COPY ./target/bootstrap/ ./
COPY ./target/platform/ ./
COPY ./target/thirdparty/ ./
COPY ./target/app/ ./
ENTRYPOINT exec java ${JAVA_OPTIONS} -Dspring.config.location=config/application.yaml,secrets/db/db.yaml org.springframework.boot.loader.JarLauncher
