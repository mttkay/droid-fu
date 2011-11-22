package com.github.droidfu.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

}
