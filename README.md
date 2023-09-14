# Food for the Weather
This is a simple application that suggests what to eat based on the current weather conditions of your location. It uses
the Weather API to retrieve weather data and the Recipe API from API Ninjas to provide meal recommendations. 

## Build
./gradlew build

## Run
./gradlew run

## Run in Docker
Create jar file by running jar task: ./gradlew jar
Test jar file: java -jar build/libs/FoodForTheWeather-1.0-SNAPSHOT.jar
Create Dockerfile in root of project
Create Docker image: docker build -t food-for-weather .
Run Docker container: docker run -it --rm -p 8080:8080 food-for-weather:latest
Test endpoint using command line tool: curl http://localhost:8080/weather/oslo

## Kubernetes
Currently deploying to kubernetes

## Endpoints
- /suggestions/{city}: This endpoint retrieves the weather data for the given city, and suggests a meal based on the average temperature.
- /weather/{city}: This endpoint retrieves the weather data for the given city.
- /recipes/{mealType}/{allergy}: This endpoint returns all recipes for a certain meal type, but excludes recipes that contain a certain ingredient. 
- /recipes/random/{mealType}: This endpoint returns five random recipes for a certain meal type.

## Todos
- Check that the same recipe isn't being fetched twice in a row
- Fix tests