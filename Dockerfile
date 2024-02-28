FROM amazoncorretto:21

ADD build/distributions/heimdall.tar ./
RUN chmod a+rw heimdall
WORKDIR ./heimdall
EXPOSE 8080

ENV TZ="UTC"

ENTRYPOINT ["bin/heimdall"]
