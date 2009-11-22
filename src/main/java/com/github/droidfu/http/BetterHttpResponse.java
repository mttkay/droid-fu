package com.github.droidfu.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

public class BetterHttpResponse {

    private HttpResponse response;

    private InputStream responseBody;

    public BetterHttpResponse(HttpResponse response) throws IOException {
        this.response = response;
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            this.responseBody = new BufferedHttpEntity(entity).getContent();
        }
    }

    public HttpResponse unwrap() {
        return response;
    }

    public InputStream getResponseBody() throws IOException {
        return this.responseBody;
    }

    public String getResponseBodyAsString() throws IOException {
        return EntityUtils.toString(this.response.getEntity());
    }

    public int getStatusCode() {
        return this.response.getStatusLine().getStatusCode();
    }

    public String getHeader(String header) {
        if (!response.containsHeader(header)) {
            return null;
        }
        return response.getFirstHeader(header).getValue();
    }
}
