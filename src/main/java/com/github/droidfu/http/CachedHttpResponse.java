package com.github.droidfu.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;

import com.github.droidfu.cachefu.HttpResponseCache;

public class CachedHttpResponse implements BetterHttpResponse {

    private HttpResponseCache responseCache;

    private byte[] cachedData;

    public CachedHttpResponse(String url) {
        responseCache = BetterHttp.getResponseCache();
        System.out.println("response cache size = " + responseCache.size());
        cachedData = responseCache.get(url);
    }

    public String getHeader(String header) {
        return null;
    }

    public InputStream getResponseBody() throws IOException {
        return new ByteArrayInputStream(cachedData);
    }

    public byte[] getResponseBodyAsBytes() throws IOException {
        return cachedData;
    }

    public String getResponseBodyAsString() throws IOException {
        return new String(cachedData);
    }

    public int getStatusCode() {
        return -1;
    }

    public HttpResponse unwrap() {
        return null;
    }

}
