FROM amazoncorretto:21

ADD build/distributions/heimdall-optimized-0.1.tar ./
RUN chmod a+rw heimdall-optimized-0.1
WORKDIR ./heimdall-optimized-0.1
EXPOSE 8080

ENV TZ="UTC"

ENTRYPOINT ["bin/heimdall"]
