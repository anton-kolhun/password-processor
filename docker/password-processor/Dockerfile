FROM openjdk:8-jre
WORKDIR /app
VOLUME ["/app"]
COPY password-processor-1.0.jar app.jar
COPY start.sh start.sh
COPY wait-for-it.sh wait-for-it.sh
RUN sh -c 'touch app.jar'
ENTRYPOINT ["./start.sh"]