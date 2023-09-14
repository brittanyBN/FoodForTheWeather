package org.example.util;

import java.util.Random;

public class FoodForWeather {
    private final String[][] temperatureRanges = {
            {"soup", "noodles", "lasagna"},                 // 0 degrees
            {"pizza", "ham", "sauce"},                     // 1-9 degrees
            {"tomato", "vegetable", "meat"},               // 10-15 degrees
            {"rice", "salad", "fruit"},                   // 16-20 degrees
            {"salad", "tacos", "rice"},                   // 21-25 degrees
            {"tacos", "cold", "gyro"}                     // 26-30 degrees
    };

    private final Random random = new Random();

    public String getMealType(int lowTemp, int highTemp) {
        int averageTemp = (lowTemp + highTemp) / 2;

        return switch (averageTemp) {
            case 0 -> randomFoodFromRange(0);
            case 1, 2, 3, 4, 5, 6, 7, 8, 9 -> randomFoodFromRange(1);
            case 10, 11, 12, 13, 14, 15 -> randomFoodFromRange(2);
            case 16, 17, 18, 19, 20 -> randomFoodFromRange(3);
            case 21, 22, 23, 24, 25 -> randomFoodFromRange(4);
            case 26, 27, 28, 29, 30 -> randomFoodFromRange(5);
            default -> "Ice Cream";
        };
    }

    private String randomFoodFromRange(int rangeIndex) {
        int index = random.nextInt(temperatureRanges[rangeIndex].length);
        return temperatureRanges[rangeIndex][index];
    }
}
