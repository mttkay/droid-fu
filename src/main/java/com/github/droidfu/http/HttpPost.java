package com.github.droidfu.http;

import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;

class HttpPost extends BetterHttpRequest {

    HttpPost(String url, HttpEntity payload,
            HashMap<String, String> defaultHeaders) {
        this.request = new org.apache.http.client.methods.HttpPost(url);
        ((HttpEntityEnclosingRequest) request).setEntity(payload);

        request.setHeader(HTTP_CONTENT_TYPE_HEADER,
                payload.getContentType().getValue());
        for (String header : defaultHeaders.keySet()) {
            request.setHeader(header, defaultHeaders.get(header));
        }
    }

}
