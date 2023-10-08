FROM ghcr.io/graalvm/graalvm-community:20 as builder

RUN microdnf install maven -y
RUN gu install native-image

COPY / /build
RUN cd /build && \
    mvn -DskipTests=true package -P native

FROM scratch
COPY --from=builder /app/output /opt/app
CMD ["/opt/app"]
