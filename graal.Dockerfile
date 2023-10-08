FROM ghcr.io/graalvm/graalvm-community:20 as builder
RUN gu install native-image

COPY playground21-0.0.1-SNAPSHOT.jar.original app.jar
RUN native-image --static -jar app.jar -H:Name=output --enable-preview

FROM scratch
COPY --from=builder /app/output /opt/app
CMD ["/opt/app"]
