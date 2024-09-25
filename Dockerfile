FROM amazoncorretto:21

ADD build/distributions/heimdall-0.1.tar ./
RUN chmod a+rw heimdall-0.1
WORKDIR ./heimdall-0.1
EXPOSE 8080

ENV TZ="UTC"

ENTRYPOINT ["bin/heimdall"]
