package org.example.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.IOException;

public class ObjectMapperSingleton {
    public static final ObjectMapper mapper = new ObjectMapper();

}

