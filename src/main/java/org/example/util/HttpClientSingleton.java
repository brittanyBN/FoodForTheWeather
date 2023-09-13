package org.example.util;

import java.net.http.HttpClient;

public class HttpClientSingleton {
    public static final HttpClient httpClient = HttpClient.newHttpClient();
}

