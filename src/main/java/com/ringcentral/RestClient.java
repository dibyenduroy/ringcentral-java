package com.ringcentral;

import com.alibaba.fastjson.JSON;
import com.ringcentral.definitions.TokenInfo;
import io.mikael.urlbuilder.UrlBuilder;
import okhttp3.*;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class RestClient extends HTTPClient {
    public static final String SANDBOX_SERVER = "https://platform.devtest.ringcentral.com";
    public static final String PRODUCTION_SERVER = "https://platform.ringcentral.com";
    private static final OkHttpClient httpClient = new OkHttpClient();
    private static final MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
    public boolean autoRefresh = true;
    private String appKey;
    private String appSecret;
    private String server;
    private TokenInfo _token;
    private Timer timer;

    public RestClient(String appKey, String appSecret, String server) {
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.server = server;
    }

    public TokenInfo getToken() {
        return _token;
    }

    public void setToken(TokenInfo token) {
        _token = token;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (autoRefresh && token != null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    refresh();
                }
            }, (token.expires_in - 120) * 1000);
        }
    }

    public void authorize(String username, String extension, String password) throws IOException, RestException {
        FormBody formBody = new FormBody.Builder()
            .add("grant_type", "password")
            .add("username", username)
            .add("extension", extension)
            .add("password", password)
            .build();
        setToken(null);
        setToken(post("/restapi/oauth/token", formBody, TokenInfo.class));
    }

    public void authorize(String auth_code, String redirectUri) throws IOException, RestException {
        FormBody formBody = new FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("code", auth_code)
            .add("redirect_uri", redirectUri)
            .build();
        setToken(null);
        setToken(post("/restapi/oauth/token", formBody, TokenInfo.class));
    }

    public void refresh() {
        if (getToken() == null) {
            return;
        }
        FormBody formBody = new FormBody.Builder()
            .add("grant_type", "refresh_token")
            .add("refresh_token", getToken().refresh_token)
            .build();
        setToken(null);
        try {
            setToken(post("/restapi/oauth/token", formBody, TokenInfo.class));
        } catch (IOException | RestException e) {
            e.printStackTrace();
        }
    }

    public void revoke() throws IOException, RestException {
        if (getToken() == null) {
            return;
        }
        FormBody formBody = new FormBody.Builder()
            .add("token", getToken().access_token)
            .build();
        setToken(null);
        post("/restapi/oauth/revoke", formBody);
    }

    public String authorizeUri(String redirectUri, String state) {
        return UrlBuilder.fromString(server)
            .withPath("/restapi/oauth/authorize")
            .addParameter("response_type", "code")
            .addParameter("state", state)
            .addParameter("redirect_uri", redirectUri)
            .addParameter("client_id", appKey)
            .toString();
    }

    public String authorizeUri(String redirectUri) {
        return authorizeUri(redirectUri, "");
    }

    public Subscription subscription(String[] events, Consumer<String> callback) {
        return new Subscription(this, events, callback);
    }

    private String basicKey() {
        return new String(Base64.getEncoder().encode(MessageFormat.format("{0}:{1}", appKey, appSecret).getBytes()));
    }

    private String authorizationHeader() {
        if (getToken() != null) {
            return MessageFormat.format("Bearer {0}", getToken().access_token);
        }
        return MessageFormat.format("Basic {0}", basicKey());
    }

    public ResponseBody request(Request.Builder builder) throws IOException, RestException {
        Request request = builder.addHeader("Authorization", authorizationHeader()).build();
        Response response = httpClient.newCall(request).execute();
        int statusCode = response.code();
        if (statusCode < 200 || statusCode > 299) {
            throw new RestException(statusCode, response.body().string());
        }
        return response.body();
    }

    public ResponseBody get(String endpoint) throws IOException, RestException {
        return request(new Request.Builder().url(server + endpoint));
    }

    public String post(String endpoint, FormBody formBody) throws IOException, RestException {
        return request(new Request.Builder().url(server + endpoint).post(formBody)).string();
    }

    public String post(String endpoint, Object object) throws IOException, RestException {
        RequestBody body = RequestBody.create(jsonMediaType, JSON.toJSONString(object));
        return request(new Request.Builder().url(server + endpoint).post(body)).string();
    }

    public ResponseBody postBinary(String endpoint, String fileName, String mediaType, byte[] fileContent) throws IOException, RestException {
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("image", fileName, RequestBody.create(MediaType.parse(mediaType), fileContent))
            .build();
        return request(new Request.Builder().url(server + endpoint).post(requestBody));
    }

    public String put(String endpoint, Object object) throws IOException, RestException {
        RequestBody body = RequestBody.create(jsonMediaType, JSON.toJSONString(object));
        return request(new Request.Builder().url(server + endpoint).put(body)).string();
    }

    public void delete(String endpoint) throws IOException, RestException {
        request(new Request.Builder().url(server + endpoint).delete());
    }
}
