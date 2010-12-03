package com.github.droidfu.http;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;

@RunWith(PowerMockRunner.class)
@PrepareForTest( { Log.class })
public class BetterHttpTestBase {

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
}
