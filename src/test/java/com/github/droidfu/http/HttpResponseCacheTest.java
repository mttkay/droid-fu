package com.github.droidfu.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

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
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
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
        
        mockFile();
        
        BetterHttp.enableResponseCache(10, 60, 1);
        cache = BetterHttp.getResponseCache();
        cache.setDiskCacheEnabled(true, "root_dir");

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
        // first time invocation should do an actual request
        BetterHttpResponse resp = BetterHttp.get(url, true).send();
        verify(fileMock, times(2)).exists();
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
    public void removingByPrefixShouldWork() {
        cache.put("http://example.com/places", new ResponseData(200, responseBody.getBytes()));
        cache.put("http://example.com/places/photos",
                new ResponseData(200, responseBody.getBytes()));
        cache.put("http://example.com/users", new ResponseData(200, responseBody.getBytes()));
        cache.put("http://example.com/users/photos",
                new ResponseData(200, responseBody.getBytes()));

        assertEquals(4, cache.size());        
        
        cache.removeAllWithPrefix("http://example.com/places");
        cache.removeKey("http://example.com/users");
        cache.removeAllWithPrefix("http://example.com/users");
        verify(fileMock, times(4)).delete();
        assertTrue(cache.isEmpty());
    }
    
    private void mockFile() throws Exception, IOException {
        PowerMockito.whenNew(File.class).withArguments(Matchers.anyString()).thenReturn(fileMock);
        when(fileMock.exists()).thenReturn(true);
        when(fileMock.createNewFile()).thenReturn(true);
        when(fileMock.length()).thenReturn(11L);
        File[] array = { new File("http://example.com/users") };
        when(fileMock.listFiles(Matchers.any(FilenameFilter.class))).thenReturn(array);

//        mockStatic(FilenameFilter.class);
//        FilenameFilter fnf = Mockito.mock(FilenameFilter.class);
//        when(fnf.accept(any(File.class), Matchers.anyString())).thenAnswer(new Answer<Boolean>() {
//            @Override
//            public Boolean answer(InvocationOnMock invocation) throws Throwable {
//                if (((String)invocation.getArguments()[0]).equals("http://example.com/users")) {
//                    return true;
//                }
//                return false;
//            }
//        });
        
        mockStatic(FileInputStream.class);
        FileInputStream fis = Mockito.mock(FileInputStream.class);
        PowerMockito.whenNew(FileInputStream.class).withArguments(File.class).
        thenReturn(fis);
        
        mockStatic(BufferedInputStream.class);
        BufferedInputStream bis = Mockito.mock(BufferedInputStream.class);
        PowerMockito.whenNew(BufferedInputStream.class).withArguments(FileInputStream.class).
        thenReturn(bis);
        when(bis.read()).thenReturn(200);
        when(bis.read(Matchers.any(byte[].class))).thenReturn(200);
        doNothing().when(bis).close();
        
        mockStatic(FileOutputStream.class);
        FileOutputStream fos = Mockito.mock(FileOutputStream.class);
        PowerMockito.whenNew(FileOutputStream.class).withArguments(File.class).
        thenReturn(fos);
        
        mockStatic(BufferedOutputStream.class);
        BufferedOutputStream bos = Mockito.mock(BufferedOutputStream.class);
        PowerMockito.whenNew(BufferedOutputStream.class).withArguments(FileOutputStream.class).thenReturn(bos);
        doNothing().when(bos).write(Matchers.anyInt());
        doNothing().when(bos).write(Matchers.any(byte[].class));
        doNothing().when(bos).close();
    }
    
}
