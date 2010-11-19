/* Copyright (c) 2009 Matthias Kaeppler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.droidfu.http;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.github.droidfu.cachefu.HttpResponseCache;
import com.github.droidfu.http.CachedHttpResponse.ResponseData;

public abstract class BetterHttpRequestBase implements BetterHttpRequest,
        ResponseHandler<BetterHttpResponse> {

    private static final int MAX_RETRIES = 5;

    protected static final String HTTP_CONTENT_TYPE_HEADER = "Content-Type";

    protected List<Integer> expectedStatusCodes = new ArrayList<Integer>();

    protected AbstractHttpClient httpClient;

    protected HttpUriRequest request;

    protected int maxRetries = MAX_RETRIES;

    BetterHttpRequestBase(AbstractHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpUriRequest unwrap() {
        return request;
    }

    public String getRequestUrl() {
        return request.getURI().toString();
    }

    public BetterHttpRequestBase expecting(Integer... statusCodes) {
        expectedStatusCodes = Arrays.asList(statusCodes);
        return this;
    }

    public BetterHttpRequestBase retries(int retries) {
        if (retries < 0) {
            this.maxRetries = 0;
        } else if (retries > MAX_RETRIES) {
            this.maxRetries = MAX_RETRIES;
        } else {
            this.maxRetries = retries;
        }
        return this;
    }

    public BetterHttpResponse send() throws ConnectException {

        httpClient.setHttpRequestRetryHandler(new BetterHttpRequestRetryHandler(maxRetries));

        HttpContext context = new BasicHttpContext();

        try {
            return httpClient.execute(request, this, context);
        } catch (IOException cause) {
            ConnectException ex = new ConnectException();
            ex.initCause(cause);
            throw ex;
        }
    }

    public BetterHttpResponse handleResponse(HttpResponse response) throws IOException {
        int status = response.getStatusLine().getStatusCode();
        if (expectedStatusCodes != null && !expectedStatusCodes.isEmpty()
                && !expectedStatusCodes.contains(status)) {
            throw new HttpResponseException(status, "Unexpected status code: " + status);
        }

        BetterHttpResponse bhttpr = new BetterHttpResponseImpl(response);
        HttpResponseCache responseCache = BetterHttp.getResponseCache();
        if (responseCache != null) {
            ResponseData responseData = new ResponseData(status, bhttpr.getResponseBodyAsBytes());
            responseCache.put(getRequestUrl(), responseData);
        }
        return bhttpr;
    }
}
