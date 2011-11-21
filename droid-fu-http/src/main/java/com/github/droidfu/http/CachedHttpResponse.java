package com.github.droidfu.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;

import com.github.droidfu.cachefu.HttpResponseCache;
import com.github.droidfu.support.ResponseData;

/**
 * A response proxy returning data from a {@link HttpResponseCache}
 * 
 * @author Matthias Kaeppler
 */
public class CachedHttpResponse implements BetterHttpResponse {

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
        return new ByteArrayInputStream(cachedData.getResponseBody());
    }

    public byte[] getResponseBodyAsBytes() throws IOException {
        return cachedData.getResponseBody();
    }

    public String getResponseBodyAsString() throws IOException {
        return new String(cachedData.getResponseBody());
    }

    public int getStatusCode() {
        return cachedData.getStatusCode();
    }

    public HttpResponse unwrap() {
        return null;
    }

}
