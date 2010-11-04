package com.github.droidfu.http;

import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;

import com.github.droidfu.cachefu.HttpResponseCache;

public class BetterHttp {

    public static final int DEFAULT_MAX_CONNECTIONS = 4;
    public static final int DEFAULT_CONNECTION_TIMEOUT = 10 * 1000;
    public static final String DEFAULT_HTTP_USER_AGENT = "Android/DroidFu";

    private static int maxConnections = DEFAULT_MAX_CONNECTIONS;
    private static int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

    private static HashMap<String, String> defaultHeaders = new HashMap<String, String>();
    private static AbstractHttpClient httpClient;
    private static Context appContext;

    private static HttpResponseCache responseCache;

    static {
        setupHttpClient();
    }

    protected static void setupHttpClient() {
        BasicHttpParams httpParams = new BasicHttpParams();

        ConnManagerParams.setTimeout(httpParams, connectionTimeout);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams,
            new ConnPerRouteBean(maxConnections));
        ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUserAgent(httpParams, DEFAULT_HTTP_USER_AGENT);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
        httpClient = new DefaultHttpClient(cm, httpParams);
    }

    public static void enableResponseCache(int initialCapacity, long expirationInMinutes,
            int maxConcurrentThreads) {
        responseCache = new HttpResponseCache(initialCapacity, expirationInMinutes,
                maxConcurrentThreads);
    }

    public static HttpResponseCache getResponseCache() {
        return responseCache;
    }

    public static void setHttpClient(AbstractHttpClient httpClient) {
        BetterHttp.httpClient = httpClient;
    }

    public static void updateProxySettings() {
        if (appContext == null) {
            return;
        }
        HttpParams httpParams = httpClient.getParams();
        ConnectivityManager connectivity = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nwInfo = connectivity.getActiveNetworkInfo();
        if (nwInfo == null) {
            return;
        }
        if (nwInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            String proxyHost = Proxy.getHost(appContext);
            if (proxyHost == null) {
                proxyHost = Proxy.getDefaultHost();
            }
            int proxyPort = Proxy.getPort(appContext);
            if (proxyPort == -1) {
                proxyPort = Proxy.getDefaultPort();
            }
            if (proxyHost != null && proxyPort > -1) {
                HttpHost proxy = new HttpHost(proxyHost, proxyPort);
                httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            } else {
                httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, null);
            }
        } else {
            httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, null);
        }
    }

    public static BetterHttpRequest get(String url) {
        return get(url, false);
    }

    public static BetterHttpRequest get(String url, boolean cached) {
        if (cached && responseCache != null && responseCache.containsKey(url)) {
            return new CachedHttpRequest(url);
        }
        return new HttpGet(httpClient, url, defaultHeaders);
    }

    public static BetterHttpRequest post(String url, HttpEntity payload) {
        return new HttpPost(httpClient, url, payload, defaultHeaders);
    }

    public static BetterHttpRequest put(String url, HttpEntity payload) {
        return new HttpPut(httpClient, url, payload, defaultHeaders);
    }

    public static BetterHttpRequest delete(String url) {
        return new HttpDelete(httpClient, url, defaultHeaders);
    }

    public static void setMaximumConnections(int maxConnections) {
        BetterHttp.maxConnections = maxConnections;
    }

    public static void setConnectionTimeout(int connectionTimeout) {
        BetterHttp.connectionTimeout = connectionTimeout;
    }

    public static void setDefaultHeader(String header, String value) {
        defaultHeaders.put(header, value);
    }

    public static void setContext(Context context) {
        if (appContext != null) {
            return;
        }
        appContext = context.getApplicationContext();
        context.registerReceiver(new ConnectionChangedBroadcastReceiver(), new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public static void setPortForScheme(String scheme, int port) {
        Scheme _scheme = new Scheme(scheme, PlainSocketFactory.getSocketFactory(), port);
        httpClient.getConnectionManager().getSchemeRegistry().register(_scheme);
    }

}
