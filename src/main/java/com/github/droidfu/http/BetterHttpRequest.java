package com.github.droidfu.http;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.util.Log;

public abstract class BetterHttpRequest {

    private static final String LOG_TAG = BetterHttpRequest.class
        .getSimpleName();

    private static final int MAX_CONNECTIONS = 6;
    private static final int MAX_RETRIES = 5;
    private static final int RETRY_SLEEP_TIME_MILLIS = 3 * 1000;
    private static final int CONNECTION_TIMEOUT = 10 * 1000;
    private static final String REQUEST_URI_BACKUP = "request_uri_backup";

    protected static final String HTTP_CONTENT_TYPE_HEADER = "Content-Type";
    protected static final String HTTP_USER_AGENT = "Android/DroidFu";

    private static AbstractHttpClient httpClient;
    private static Context appContext;
    private static HashMap<String, String> defaultHeaders = new HashMap<String, String>();

    static {
        setupHttpClient();
    }

    private List<Integer> expectedStatusCodes = new ArrayList<Integer>();

    private OAuthConsumer oauthConsumer;

    protected HttpUriRequest request;

    /**
     * Wraps the {@link HttpResponse} into a {@link BetterHttpResponse}. Also
     * takes care of throwing a {@link HttpResponseException} if an unexpected
     * response code was received.
     */
    private ResponseHandler<BetterHttpResponse> responseHandler = new ResponseHandler<BetterHttpResponse>() {
        public BetterHttpResponse handleResponse(HttpResponse response)
                throws ClientProtocolException, IOException {

            int status = response.getStatusLine().getStatusCode();
            if (expectedStatusCodes != null
                    && !expectedStatusCodes.contains(status)) {
                throw new HttpResponseException(status,
                    "Unexpected status code: " + status);
            }

            return new BetterHttpResponse(response);
        }
    };

    /**
     * A custom request-retry handler which supports re-signing previously
     * failed messages. TODO: ignore non-idem-potent requests?
     */
    private HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {

        public boolean retryRequest(IOException exception, int executionCount,
                HttpContext context) {

            if (executionCount > MAX_RETRIES) {
                return false;
            }

            exception.printStackTrace();
            Log.d(BetterHttpRequest.class.getSimpleName(), "Retrying "
                    + request.getRequestLine().getUri() + " (tried: "
                    + executionCount + " times)");

            // Apache HttpClient rewrites the request URI to be relative before
            // sending a request, but we need the full URI for OAuth signing,
            // so restore it before proceeding.
            RequestWrapper request = (RequestWrapper) context
                .getAttribute(ExecutionContext.HTTP_REQUEST);
            URI rewrittenUri = request.getURI();
            URI originalUri = (URI) context.getAttribute(REQUEST_URI_BACKUP);
            request.setURI(originalUri);

            // re-sign the request, otherwise this may yield 401s
            if (oauthConsumer != null) {
                try {
                    oauthConsumer.sign(request);
                } catch (Exception e) {
                    e.printStackTrace();
                    // no reason to retry this
                    return false;
                }
            }

            // restore URI to whatever Apache HttpClient expects
            request.setURI(rewrittenUri);

            return true;
        }
    };

    private static void setupHttpClient() {
        BasicHttpParams httpParams = new BasicHttpParams();

        ConnManagerParams.setTimeout(httpParams, CONNECTION_TIMEOUT);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
            new ConnPerRouteBean(MAX_CONNECTIONS));
        ConnManagerParams.setMaxTotalConnections(httpParams, MAX_CONNECTIONS);
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUserAgent(httpParams, HTTP_USER_AGENT);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory
            .getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", PlainSocketFactory
            .getSocketFactory(), 443));

        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
            httpParams, schemeRegistry);
        httpClient = new DefaultHttpClient(cm, httpParams);
    }

    public static void updateProxySettings(Context context) {
        HttpParams httpParams = httpClient.getParams();
        ConnectivityManager connectivity = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nwInfo = connectivity.getActiveNetworkInfo();
        if (nwInfo == null) {
            return;
        }
        if (nwInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            String proxyHost = Proxy.getDefaultHost();
            int proxyPort = Proxy.getDefaultPort();
            if (proxyHost != null && proxyPort > -1) {
                Log.d(LOG_TAG, "Detected carrier proxy " + proxyHost + ":"
                        + proxyPort);
                HttpHost proxy = new HttpHost(proxyHost, proxyPort);
                httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            }
        } else {
            httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, null);
        }
    }

    public static void setContext(Context context) {
        if (BetterHttpRequest.appContext != null) {
            return;
        }
        BetterHttpRequest.appContext = context.getApplicationContext();
        context.registerReceiver(new ConnectionChangedBroadcastReceiver(),
            new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public static void setPortForScheme(String scheme, int port) {
        Scheme _scheme = new Scheme(scheme, PlainSocketFactory
            .getSocketFactory(), port);
        httpClient.getConnectionManager().getSchemeRegistry().register(_scheme);
    }

    public static void setDefaultHeader(String header, String value) {
        defaultHeaders.put(header, value);
    }

    public static BetterHttpRequest get(String url) {
        return new HttpGet(url, defaultHeaders);
    }

    public static BetterHttpRequest post(String url, HttpEntity payload) {
        return new HttpPost(url, payload, defaultHeaders);
    }

    public HttpUriRequest unwrap() {
        return request;
    }

    public BetterHttpRequest expecting(Integer... statusCodes) {
        expectedStatusCodes = Arrays.asList(statusCodes);
        return this;
    }

    public BetterHttpRequest signed(OAuthConsumer oauthConsumer)
            throws OAuthMessageSignerException, OAuthExpectationFailedException {
        this.oauthConsumer = oauthConsumer;
        oauthConsumer.sign(this.unwrap());
        return this;
    }

    public BetterHttpResponse send() throws ConnectException {
        if (appContext != null) {
            updateProxySettings(appContext);
        }
        HttpContext httpContext = new BasicHttpContext();
        // Apache HttpClient rewrites the request URI to be relative before
        // sending a request, but we need the full URI in the retry handler,
        // so store it manually before proceeding.
        httpContext.setAttribute(REQUEST_URI_BACKUP, request.getURI());

        httpClient.setHttpRequestRetryHandler(retryHandler);

        int numAttempts = 0;

        while (numAttempts < MAX_RETRIES) {

            numAttempts++;

            try {
                if (oauthConsumer != null) {
                    oauthConsumer.sign(request);
                }
                return httpClient
                    .execute(request, responseHandler, httpContext);
            } catch (Exception e) {
                waitAndContinue(e, numAttempts, MAX_RETRIES);
            }
        }
        return null;
    }

    private void waitAndContinue(Exception cause, int numAttempts,
            int maxAttempts) throws ConnectException {
        if (numAttempts == maxAttempts) {
            Log.e(LOG_TAG, "request failed after " + numAttempts + " attempts");
            ConnectException ex = new ConnectException();
            ex.initCause(cause);
            throw ex;
        } else {
            cause.printStackTrace();
            Log.e(LOG_TAG, "request failed, will retry after "
                    + RETRY_SLEEP_TIME_MILLIS / 1000 + " secs...");
            try {
                Thread.sleep(RETRY_SLEEP_TIME_MILLIS);
            } catch (InterruptedException e1) {
            }
        }
    }
}
