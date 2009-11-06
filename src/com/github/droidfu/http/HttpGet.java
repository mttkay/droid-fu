package com.github.droidfu.http;

import java.util.HashMap;

class HttpGet extends BetterHttpRequest {

    HttpGet(String url, HashMap<String, String> defaultHeaders) {
        request = new org.apache.http.client.methods.HttpGet(url);
        for (String header : defaultHeaders.keySet()) {
            request.setHeader(header, defaultHeaders.get(header));
        }
    }

}
