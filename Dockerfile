FROM public.ecr.aws/amazoncorretto/amazoncorretto:21 AS build
WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle* build.gradle*  ./

RUN chmod +x gradlew && sed -i 's/\r$//' gradlew
RUN ./gradlew --no-daemon dependencies || true

COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test

FROM public.ecr.aws/amazoncorretto/amazoncorretto:21
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENV JAVA_OPTS=""
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dserver.port=${PORT} -jar app.jar"]
