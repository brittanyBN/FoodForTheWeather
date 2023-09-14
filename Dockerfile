FROM openjdk:22-slim
WORKDIR /
ADD build/libs/FoodForTheWeather-1.0-SNAPSHOT.jar app.jar
ADD .env .env
EXPOSE 8080
CMD java -jar app.jar


