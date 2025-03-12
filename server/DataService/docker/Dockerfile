FROM astita/openjdk21-alpine
ARG JAR_FILE
ADD ./build/libs/${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]