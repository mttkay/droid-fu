package com.github.droidfu.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

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
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

import com.github.droidfu.cachefu.HttpResponseCache;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { Log.class })
public class HttpResponseCacheTest {

    private String responseBody = "Here be Jason.";

    private String url = "http://api.qype.com/positions/1,1/places?x=y&a=%2Bc";

    private HttpResponseCache cache;

    @Mock
    private AbstractHttpClient httpClientMock;

    @Mock
    private BetterHttpResponse mockResponse;

    @SuppressWarnings("unchecked")
    @Before
    public void setupHttpClient() throws Exception {

        BetterHttp.enableResponseCache(10, 60, 1);
        cache = BetterHttp.getResponseCache();

        when(mockResponse.getResponseBody()).thenReturn(
            new ByteArrayInputStream(responseBody.getBytes()));
        when(mockResponse.getResponseBodyAsBytes()).thenReturn(responseBody.getBytes());
        when(mockResponse.getResponseBodyAsString()).thenReturn(responseBody);
        when(mockResponse.getStatusCode()).thenReturn(200);
        when(
            httpClientMock.execute(any(HttpUriRequest.class), any(ResponseHandler.class),
                any(HttpContext.class))).thenAnswer(new Answer<BetterHttpResponse>() {
            public BetterHttpResponse answer(InvocationOnMock invocation) throws Throwable {
                HttpResponseCache cache = BetterHttp.getResponseCache();
                cache.put(url, responseBody.getBytes());
                return mockResponse;
            }
        });

        BetterHttp.setHttpClient(httpClientMock);
    }

    // TODO: this is really useful... move to separate test project and export
    // as helper?
    @Before
    public void mockLogger() {
        // redirect Logger output to STDOUT
        mockStatic(Log.class);
        when(Log.d(any(String.class), any(String.class))).thenAnswer(new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                String tag = (String) invocation.getArguments()[0];
                String msg = (String) invocation.getArguments()[1];
                System.out.println("[" + tag + "] " + msg);
                return 0;
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBasicCachingFlow() throws Exception {
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

    @Test
    public void shouldGenerateCorrectFileNamesWhenCachingToDisk() {
        assertEquals("http+api+qype+com+positions+1+1+places+x+y+a+2Bc",
            cache.getFileNameForKey(url));
    }

    @Test
    public void removingByPrefixShouldWork() {
        cache.put("http://example.com/places", responseBody.getBytes());
        cache.put("http://example.com/places/photos", responseBody.getBytes());
        assertEquals(2, cache.size());

        cache.removeAllWithPrefix("http://example.com/places");
        assertTrue(cache.isEmpty());
    }
}
