package org.example.resource;

public class FoodForWeather {
    public String getMealType(int lowTemp, int highTemp) {
        int averageTemp = (lowTemp + highTemp) / 2;
        return switch (averageTemp) {
            case 0 -> "Soup";
            case 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 -> "Pasta";
            case 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 -> "Salad";
            case 26, 27, 28, 29, 30 -> "Tacos";
            default -> "Ice Cream";
        };
    }
}
