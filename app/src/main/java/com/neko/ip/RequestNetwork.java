package com.neko.ip;

import android.app.Activity;
import java.util.HashMap;

public class RequestNetwork {
    private Activity activity;
    private HashMap<String, Object> params = new HashMap<>();
    private HashMap<String, Object> headers = new HashMap<>();
    private int requestType = 0;

    public interface RequestListener {
        void onErrorResponse(String str, String str2);

        void onResponse(String str, String str2, HashMap<String, Object> hashMap);
    }

    public RequestNetwork(Activity activity) {
        this.activity = activity;
    }

    public void setHeaders(HashMap<String, Object> hashMap) {
        this.headers = hashMap;
    }

    public void setParams(HashMap<String, Object> hashMap, int i) {
        this.params = hashMap;
        this.requestType = i;
    }

    public HashMap<String, Object> getParams() {
        return this.params;
    }

    public HashMap<String, Object> getHeaders() {
        return this.headers;
    }

    public Activity getActivity() {
        return this.activity;
    }

    public int getRequestType() {
        return this.requestType;
    }

    public void startRequestNetwork(String str, String str2, String str3, RequestListener requestListener) {
        RequestNetworkController.getInstance().execute(this, str, str2, str3, requestListener);
    }
}