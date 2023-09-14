# Food for the Weather
This is a simple application that suggests what to eat based on the current weather conditions of your location. It uses
the Weather API to retrieve weather data and the Recipe API from API Ninjas to provide meal recommendations. 

## Endpoints
- /suggestions/{city}: This endpoint retrieves the weather data for the given city, and suggests a meal based on the average temperature.
- /weather/{city}: This endpoint retrieves the weather data for the given city.
- /recipes/{mealType}/{allergy}: This endpoint returns all recipes for a certain meal type, but excludes recipes that contain a certain ingredient. 
- /recipes/random/{mealType}: This endpoint returns five random recipes for a certain meal type. 

## Todos
- Check that the same recipe isn't being fetched twice in a row
- Fix tests