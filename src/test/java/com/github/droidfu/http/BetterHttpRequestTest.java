package com.github.droidfu.http;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.params.CoreConnectionPNames;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { Log.class })
public class BetterHttpRequestTest extends BetterHttpTestBase {

    @Test
    public void testWithTimeout() throws Exception {
        // timeout before sending request
        int timeout = BetterHttp.getSocketTimeout();
        assertTrue(timeout != 1);

        // change timeout to 1 for this single request
        BetterHttpRequest request = BetterHttp.get(url).withTimeout(1);
        assertEquals(1, BetterHttp.getSocketTimeout());
        assertEquals(1, httpClientMock.getParams().getIntParameter(CoreConnectionPNames.SO_TIMEOUT,
                BetterHttp.DEFAULT_SOCKET_TIMEOUT));

        request.send();

        // make sure it is reset to its former value
        assertEquals(timeout, BetterHttp.getSocketTimeout());
        assertEquals(timeout, httpClientMock.getParams().getIntParameter(
                CoreConnectionPNames.SO_TIMEOUT, BetterHttp.DEFAULT_SOCKET_TIMEOUT));
    }
    
    @Test
    public void testHeaderAddition()
    {
        BetterHttpRequest request = BetterHttp.get(url);
        
        HttpUriRequest uriRequest = request.unwrap();
        
        int headerCount = uriRequest.getAllHeaders().length;
        
        request.addHeader("test", "1");
        
        assertTrue(headerCount +1 == uriRequest.getAllHeaders().length);
        
        Header[] headers = uriRequest.getHeaders("test");
        
        assertEquals(1, headers.length);
    }
    
    @Test
    public void testHeaderReaddition()
    {
        BetterHttpRequest request = BetterHttp.get(url);
        
        HttpUriRequest uriRequest = request.unwrap();
        
        request.addHeader("test", "1");
        
        Header[] headers = uriRequest.getHeaders("test");
        assertEquals("1", headers[0].getValue());
        
        request.addHeader("test", "2");
        
        headers = uriRequest.getHeaders("test");
        assertEquals("2", headers[0].getValue());
    }
    
    @Test
    public void testHeaderRemoval()
    {
        BetterHttpRequest request = BetterHttp.get(url);
        
        HttpUriRequest uriRequest = request.unwrap();
        
        request.addHeader("test", "1");
                
        assertEquals(1, uriRequest.getHeaders("test").length);

        request.removeHeader("test");

        assertEquals(0, uriRequest.getHeaders("test").length);
    }
    
    @Test
    public void testHeaderRemoveNonExisting()
    {
        BetterHttpRequest request = BetterHttp.get(url);
        request.removeHeader("UUUUUU");
    }

}
