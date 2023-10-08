FROM --platform=linux/amd64 cgr.dev/chainguard/graalvm-native

COPY linux-build/lib*.so /
COPY linux-build/playground21 /app

# TODO pass as env to container
#ENV SPRING_DATASOURCE_URL=jdbc:postgresql://empty-brook-9488-db.flycast:5432/empty_brook_9488?sslmode=disable
#ENV SPRING_DATASOURCE_USERNAME=empty_brook_9488
#ENV SPRING_DATASOURCE_PASSWORD=gTVQRf0eJgybvBv

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/playground
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=postgres

EXPOSE 8080

ENTRYPOINT ["/app", "-XX:+PrintGC"]