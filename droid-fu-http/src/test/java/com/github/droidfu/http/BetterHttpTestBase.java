package com.github.droidfu.http;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HttpContext;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

import com.github.droidfu.TestBase;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Log.class })
public class BetterHttpTestBase extends TestBase {

    protected String responseBody = "Here be Jason.";

    protected String url = "http://api.qype.com/positions/1,1/places?x=y&a=%2Bc";

    @Mock
    protected AbstractHttpClient httpClientMock;

    @Mock
    protected BetterHttpResponse mockResponse;

    @SuppressWarnings("unchecked")
    @Before
    public void setupHttpClient() throws Exception {

        when(mockResponse.getResponseBody()).thenReturn(
                new ByteArrayInputStream(responseBody.getBytes()));
        when(mockResponse.getResponseBodyAsBytes()).thenReturn(responseBody.getBytes());
        when(mockResponse.getResponseBodyAsString()).thenReturn(responseBody);
        when(mockResponse.getStatusCode()).thenReturn(200);
        when(
                httpClientMock.execute(any(HttpUriRequest.class), any(ResponseHandler.class),
                        any(HttpContext.class))).thenReturn(mockResponse);

        BasicHttpParams params = new BasicHttpParams();
        HttpConnectionParams.setSoTimeout(params, BetterHttp.DEFAULT_SOCKET_TIMEOUT);
        when(httpClientMock.getParams()).thenReturn(params);

        BetterHttp.setHttpClient(httpClientMock);
    }

}
