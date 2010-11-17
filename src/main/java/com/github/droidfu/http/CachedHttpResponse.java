package com.github.droidfu.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;

import com.github.droidfu.cachefu.HttpResponseCache;

/**
 * A response proxy returning data from a {@link HttpResponseCache}
 * 
 * @author Matthias Kaeppler
 */
public class CachedHttpResponse implements BetterHttpResponse {

    public static final class ResponseData {
        public ResponseData(int statusCode, byte[] responseBody) {
            this.statusCode = statusCode;
            this.responseBody = responseBody;
        }

        private int statusCode;
        private byte[] responseBody;

        public int getStatusCode() {
            return statusCode;
        }

        public byte[] getResponseBody() {
            return responseBody;
        }
    }

    private HttpResponseCache responseCache;

    private ResponseData cachedData;

    public CachedHttpResponse(String url) {
        responseCache = BetterHttp.getResponseCache();
        cachedData = responseCache.get(url);
    }

    public String getHeader(String header) {
        return null;
    }

    public InputStream getResponseBody() throws IOException {
        return new ByteArrayInputStream(cachedData.responseBody);
    }

    public byte[] getResponseBodyAsBytes() throws IOException {
        return cachedData.responseBody;
    }

    public String getResponseBodyAsString() throws IOException {
        return new String(cachedData.responseBody);
    }

    public int getStatusCode() {
        return cachedData.statusCode;
    }

    public HttpResponse unwrap() {
        return null;
    }

}
