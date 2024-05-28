FROM openjdk:11
WORKDIR /app
COPY . .
RUN ./gradlew build
ENTRYPOINT ["java","-jar","build/libs/summarize-library-2.0.1.jar"]
