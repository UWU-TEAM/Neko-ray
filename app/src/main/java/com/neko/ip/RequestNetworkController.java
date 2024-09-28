package com.neko.ip;

import android.app.Activity;
import com.google.gson.Gson;
import com.neko.ip.RequestNetwork;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestNetworkController {
    public static final String DELETE = "DELETE";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    private static final int READ_TIMEOUT = 25000;
    public static final int REQUEST_BODY = 1;
    public static final int REQUEST_PARAM = 0;
    private static final int SOCKET_TIMEOUT = 15000;
    private static RequestNetworkController mInstance;
    protected OkHttpClient client;

    public static synchronized RequestNetworkController getInstance() {
        RequestNetworkController requestNetworkController;
        synchronized (RequestNetworkController.class) {
            if (mInstance == null) {
                mInstance = new RequestNetworkController();
            }
            requestNetworkController = mInstance;
        }
        return requestNetworkController;
    }

    private OkHttpClient getClient() {
        if (this.client == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            try {
                TrustManager[] trustManagerArr = {new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }};
                SSLContext sSLContext = SSLContext.getInstance("TLS");
                sSLContext.init(null, trustManagerArr, new SecureRandom());
                builder.sslSocketFactory(sSLContext.getSocketFactory(), (X509TrustManager) trustManagerArr[0]);
                builder.connectTimeout(15000L, TimeUnit.MILLISECONDS);
                builder.readTimeout(25000L, TimeUnit.MILLISECONDS);
                builder.writeTimeout(25000L, TimeUnit.MILLISECONDS);
                builder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String str, SSLSession sSLSession) {
                        return true;
                    }
                });
            } catch (Exception e) {
            }
            this.client = builder.build();
        }
        return this.client;
    }

    public void execute(final RequestNetwork requestNetwork, String str, String str2, final String str3, final RequestNetwork.RequestListener requestListener) {
        Request.Builder builder = new Request.Builder();
        Headers.Builder builder2 = new Headers.Builder();
        if (requestNetwork.getHeaders().size() > 0) {
            for (Map.Entry<String, Object> entry : requestNetwork.getHeaders().entrySet()) {
                builder2.add(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        try {
            if (requestNetwork.getRequestType() == 0) {
                if (str.equals(GET)) {
                    try {
                        HttpUrl.Builder newBuilder = HttpUrl.parse(str2).newBuilder();
                        if (requestNetwork.getParams().size() > 0) {
                            for (Map.Entry<String, Object> entry2 : requestNetwork.getParams().entrySet()) {
                                newBuilder.addQueryParameter(entry2.getKey(), String.valueOf(entry2.getValue()));
                            }
                        }
                        builder.url(newBuilder.build()).headers(builder2.build()).get();
                    } catch (NullPointerException e) {
                        throw new NullPointerException("unexpected url: " + str2);
                    }
                } else {
                    FormBody.Builder builder3 = new FormBody.Builder();
                    if (requestNetwork.getParams().size() > 0) {
                        for (Map.Entry<String, Object> entry3 : requestNetwork.getParams().entrySet()) {
                            builder3.add(entry3.getKey(), String.valueOf(entry3.getValue()));
                        }
                    }
                    builder.url(str2).headers(builder2.build()).method(str, builder3.build());
                }
            } else {
                RequestBody create = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(requestNetwork.getParams()));
                if (str.equals(GET)) {
                    builder.url(str2).headers(builder2.build()).get();
                } else {
                    builder.url(str2).headers(builder2.build()).method(str, create);
                }
            }
            getClient().newCall(builder.build()).enqueue(new Callback() {
                public void onFailure(Call call, final IOException iOException) {
                    Activity activity = requestNetwork.getActivity();
                    final RequestNetwork.RequestListener requestListener2 = requestListener;
                    final String str4 = str3;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            requestListener2.onErrorResponse(str4, iOException.getMessage());
                        }
                    });
                }

                public void onResponse(Call call, final Response response) throws IOException {
                    final String trim = response.body().string().trim();
                    Activity activity = requestNetwork.getActivity();
                    final RequestNetwork.RequestListener requestListener2 = requestListener;
                    final String str4 = str3;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Headers headers = response.headers();
                            HashMap<String, Object> hashMap = new HashMap<>();
                            for (String str5 : headers.names()) {
                                hashMap.put(str5, headers.get(str5) != null ? headers.get(str5) : "null");
                            }
                            requestListener2.onResponse(str4, trim, hashMap);
                        }
                    });
                }
            });
        } catch (Exception e2) {
            requestListener.onErrorResponse(str3, e2.getMessage());
        }
    }
}