package com.github.droidfu.http;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.github.droidfu.cachefu.HttpResponseCache;

@RunWith(MockitoJUnitRunner.class)
public class HttpCacheTest {

    private String url = "http://test.com";

    @Mock
    private AbstractHttpClient httpClientMock;

    @Mock
    private BetterHttpResponse mockResponse;

    private static final String RESPONSE_BODY = "Here be Jason.";

    @SuppressWarnings("unchecked")
    @Before
    public void setupHttpClient() throws Exception {

        BetterHttp.enableResponseCache(10, 60, 1);

        when(mockResponse.getResponseBody()).thenReturn(
            new ByteArrayInputStream(RESPONSE_BODY.getBytes()));
        when(mockResponse.getResponseBodyAsBytes()).thenReturn(RESPONSE_BODY.getBytes());
        when(mockResponse.getResponseBodyAsString()).thenReturn(RESPONSE_BODY);
        when(mockResponse.getStatusCode()).thenReturn(200);
        when(
            httpClientMock.execute(any(HttpUriRequest.class), any(ResponseHandler.class),
                any(HttpContext.class))).thenAnswer(new Answer<BetterHttpResponse>() {
            public BetterHttpResponse answer(InvocationOnMock invocation) throws Throwable {
                HttpResponseCache cache = BetterHttp.getResponseCache();
                cache.put(url, RESPONSE_BODY.getBytes());
                return mockResponse;
            }
        });

        BetterHttp.setHttpClient(httpClientMock);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotReturnCachedResponseIfCalledForFirstTime() throws Exception {
        // first time invocation should do an actual request
        BetterHttpResponse resp = BetterHttp.get(url, true).send();
        verify(httpClientMock, times(1)).execute(any(HttpUriRequest.class),
            any(ResponseHandler.class), any(HttpContext.class));
        assertSame(mockResponse, resp);

        // any subsequent invocation should return the cached response
        resp = BetterHttp.get(url, true).send();
        assertNotSame(mockResponse, resp);
        assertTrue(resp instanceof CachedHttpResponse);
    }
}
