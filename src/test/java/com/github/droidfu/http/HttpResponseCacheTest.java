package com.github.droidfu.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

import com.github.droidfu.cachefu.HttpResponseCache;
import com.github.droidfu.http.CachedHttpResponse.ResponseData;
import com.github.droidfu.support.StringSupport;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { Log.class, HttpResponseCache.class, StringSupport.class })
public class HttpResponseCacheTest extends BetterHttpTestBase {

    private HttpResponseCache cache;
    
    @Mock
    private File fileMock;
    
    @SuppressWarnings("unchecked")
    @Before
    public void setupHttpClient() throws Exception {
        super.setupHttpClient();
        
        mockStatic(StringSupport.class);
        when(StringSupport.underscore(Matchers.anyString())).thenReturn("http_resp/");
        
        mockIOObjects();
        
        BetterHttp.enableResponseCache(10, 60, 1);
        cache = BetterHttp.getResponseCache();

        when(
                httpClientMock.execute(any(HttpUriRequest.class), any(ResponseHandler.class),
                        any(HttpContext.class))).thenAnswer(new Answer<BetterHttpResponse>() {
            public BetterHttpResponse answer(InvocationOnMock invocation) throws Throwable {
                HttpResponseCache cache = BetterHttp.getResponseCache();
                cache.put(url, new ResponseData(200, responseBody.getBytes()));
                return mockResponse;
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testBasicCachingFlow() throws Exception {
        when(fileMock.exists()).thenReturn(false);
        
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
        assertEquals("http+api+qype+com+positions+1+1+places+x+y+a+2Bc", cache
                .getFileNameForKey(url));
    }

    @Test
    public void removingByPrefixShouldWork() throws IOException {
        cache.setDiskCacheEnabled("cache_root_dir");
        
        cache.put("http://example.com/places", new ResponseData(200, responseBody.getBytes()));
        cache.put("http://example.com/places/photos",
                new ResponseData(200, responseBody.getBytes()));
        
        verify(fileMock, times(2)).createNewFile();
        assertEquals(2, cache.size());
        
        cache.removeAllWithPrefix("http://example.com/places");
        verify(fileMock, times(2)).delete();
        assertTrue(cache.isEmpty());
    }
    
    @Test
    public void removeByPrefixShouldRemoveExpiredCachedFiles() throws IOException {
        cache.setDiskCacheEnabled("cache_root_dir");
        File[] cachedFiles = { new File("http://example.com/users/photos"), new File("http://example.com/users") };
        when(fileMock.listFiles(Matchers.any(FilenameFilter.class))).thenReturn(cachedFiles);
        
        cache.put("http://example.com/users", new ResponseData(200, responseBody.getBytes()));
        cache.put("http://example.com/users/photos",
                new ResponseData(200, responseBody.getBytes()));

        verify(fileMock, times(2)).createNewFile();
        assertEquals(2, cache.size()); 

        // Cache expires
        cache.removeKey("http://example.com/users");
        cache.removeKey("http://example.com/users/photos");
        
        cache.removeAllWithPrefix("http://example.com/users");
        verify(fileMock, times(2)).delete();
    }
    
    private void mockIOObjects() throws Exception {
        whenNew(File.class).withArguments(Matchers.anyString()).thenReturn(fileMock);
        when(fileMock.exists()).thenReturn(true);
        when(fileMock.createNewFile()).thenReturn(true);
        when(fileMock.length()).thenReturn(11111L);
        
        mockStatic(FileInputStream.class);
        FileInputStream fis = mock(FileInputStream.class);
        whenNew(FileInputStream.class).withArguments(File.class).
        thenReturn(fis);
        
        mockStatic(BufferedInputStream.class);
        BufferedInputStream bis = mock(BufferedInputStream.class);
        whenNew(BufferedInputStream.class).withArguments(FileInputStream.class).
        thenReturn(bis);
        
        mockStatic(FileOutputStream.class);
        FileOutputStream fos = mock(FileOutputStream.class);
        whenNew(FileOutputStream.class).withArguments(File.class).
        thenReturn(fos);
        
        mockStatic(BufferedOutputStream.class);
        BufferedOutputStream bos = mock(BufferedOutputStream.class);
        whenNew(BufferedOutputStream.class).withArguments(FileOutputStream.class).thenReturn(bos);
    }
    
}
