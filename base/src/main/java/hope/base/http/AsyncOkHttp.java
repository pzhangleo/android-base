//package hope.base.http;
//
//import android.os.Handler;
//import android.os.Looper;
//
//import hope.base.Constants;
//import hope.base.http.callback.ResponseCallback;
//import hope.base.http.exception.HttpRequestException;
//import hope.base.log.ZLog;
//import hope.base.utils.DeviceInfoUtils;
//
//import java.io.IOException;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.TimeUnit;
//
//import javax.net.ssl.HostnameVerifier;
//import javax.net.ssl.SSLSocketFactory;
//import javax.net.ssl.X509TrustManager;
//
//import okhttp3.Cache;
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.Dispatcher;
//import okhttp3.Dns;
//import okhttp3.HttpUrl;
//import okhttp3.Interceptor;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
///**
// * 异步OkHttp
// * Created by Zhp on 2015/9/22.
// */
//@SuppressWarnings({"JavaDoc", "unused", "unchecked"})
//public class AsyncOkHttp {
//
//    public static final String LOG_TAG = AsyncOkHttp.class.getSimpleName();
//
//    public static final MediaType APPLICATION_XML =
//            MediaType.parse("application/xml; charset=utf-8");
//    public static final MediaType APPLICATION_JSON =
//            MediaType.parse("application/json; charset=utf-8");
//    public static final MediaType APPLICATION_FORM_URLENCODED =
//            MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
//    public static final MediaType MULTIPART_FORM_DATA =
//            MediaType.parse("multipart/form-data");
//    public static final MediaType APPLICATION_OCTET_STREAM =
//            MediaType.parse("application/octet-stream");
//    public static final MediaType TEXT_PLAIN =
//            MediaType.parse("text/plain; charset=utf-8");
//
//    public static final String HEADER_CONTENT_TYPE = "Content-Type";
//    public static final String HEADER_CONTENT_RANGE = "Content-Range";
//    public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
//    public static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
//    public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
//
//    public static final int DEFAULT_MAX_CONNECTIONS = 5;
//    public static final int DEFAULT_CONNECT_TIMEOUT = 10 * 1000;
//    public static final int DEFAULT_SOCKET_TIMEOUT = 30 * 1000;
//
//    private int maxConnections = DEFAULT_MAX_CONNECTIONS;
//    private int connectTimeout = DEFAULT_SOCKET_TIMEOUT;
//    private int responseTimeout = DEFAULT_SOCKET_TIMEOUT;
//
//    private OkHttpClient mOkHttpClient;
//
//    private Handler mThreadHandler;
//
//    public AsyncOkHttp() {
//        mThreadHandler = new Handler(Looper.getMainLooper());
//        Dispatcher dispatcher = new Dispatcher();
//        dispatcher.setMaxRequests(DEFAULT_MAX_CONNECTIONS);
//        dispatcher.setMaxRequestsPerHost(DEFAULT_MAX_CONNECTIONS);
//        mOkHttpClient = new OkHttpClient.Builder()
//                .readTimeout(DEFAULT_SOCKET_TIMEOUT, TimeUnit.MILLISECONDS)
//                .writeTimeout(DEFAULT_SOCKET_TIMEOUT, TimeUnit.MILLISECONDS)
//                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
//                .dispatcher(dispatcher)
//                .dns(Dns.SYSTEM)
//                .build();
//    }
//
//    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
//        if (mOkHttpClient != null) {
//            mOkHttpClient = mOkHttpClient.newBuilder().sslSocketFactory(sslSocketFactory).build();
//        }
//    }
//
//    public void setSslSocketFactory(SSLSocketFactory sslSocketFactory, X509TrustManager x509TrustManager) {
//        if (mOkHttpClient != null) {
//            mOkHttpClient = mOkHttpClient.newBuilder().sslSocketFactory(sslSocketFactory, x509TrustManager).build();
//        }
//    }
//
//    public void setCacheDir(Cache cache) {
//        mOkHttpClient = mOkHttpClient.newBuilder().cache(cache).build();
//    }
//
//    public void setDns(Dns dns) {
//        mOkHttpClient = mOkHttpClient.newBuilder().dns(dns).build();
//    }
//
//    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
//        mOkHttpClient = mOkHttpClient.newBuilder().hostnameVerifier(hostnameVerifier).build();
//    }
//
//    public void addInterceptor(Interceptor interceptor) {
//        mOkHttpClient = mOkHttpClient.newBuilder().addInterceptor(interceptor).build();
//    }
//
//    public void addNetworkInterceptor(Interceptor interceptor) {
//        mOkHttpClient = mOkHttpClient.newBuilder().addNetworkInterceptor(interceptor).build();
//    }
//
//    public CallHandler post(String url, RequestParams requestParams, ResponseCallback responseCallback) {
//        RequestBody requestBody = requestParams.getRequestBody();
//        HttpUrl urlWithPathSegment;
//        urlWithPathSegment = requestParams.getUrlWithPathSegment(url);
//        Request.Builder builder = new Request.Builder()
//                .url(urlWithPathSegment)
//                .post(requestBody);
//        setHeader(builder, requestParams);
//        return doExecute(builder, responseCallback);
//    }
//
//    public CallHandler get(String url, RequestParams requestParams, ResponseCallback responseCallback) {
//        HttpUrl urlWithQueryString;
//        if (requestParams != null) {
//            urlWithQueryString = requestParams.getUrlWithQueryString(url);
//        } else {
//            urlWithQueryString = HttpUrl.parse(url);
//        }
//        Request.Builder builder = new Request.Builder()
//                .url(urlWithQueryString)
//                .get();
//        setHeader(builder, requestParams);
//        return doExecute(builder, responseCallback);
//    }
//
//    /**
//     * 上传文件专用
//     *
//     * @param url
//     * @param requestParams
//     * @param responseCallback
//     * @return
//     */
//    public CallHandler upload(String url, RequestParams requestParams, ResponseCallback responseCallback) {
//        RequestBody requestBody = requestParams.getRequestBody();
//        Request.Builder builder = new Request.Builder()
//                .url(url)
//                .post(new ProgressRequestBody(responseCallback, requestBody, mThreadHandler));
//        return doExecute(builder, responseCallback);
//    }
//
//    public CallHandler doExecute(Request.Builder requestBuilder, final ResponseCallback responseCallback) {
//        requestBuilder.addHeader("User-Agent", DeviceInfoUtils.getUserAgent());
//        Request request = requestBuilder.build();
//        responseCallback.setRequest(request);
//        request = responseCallback.updateRequestHeaders(request);
//        if (!responseCallback.isSync()) {
//            mThreadHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    responseCallback.onStart();
//                }
//            });
//        } else {
//            responseCallback.onStart();
//        }
//        final Call newCall = mOkHttpClient.newCall(request);
//        final CallHandler callHandler = new CallHandler(newCall);
//        if (!responseCallback.isSync()) {//异步模式
//            newCall.enqueue(new Callback() {
//                @Override
//                public void onFailure(final Call call, final IOException e) {
//                    processFailure(call, e, responseCallback, callHandler);
//                }
//
//                @Override
//                public void onResponse(final Call call, final Response response) throws IOException {
//                    processResponse(call, response, responseCallback, callHandler);
//                }
//            });
//        } else {//同步模式
//            try {
//                final Response response = newCall.execute();
//                processResponse(newCall, response, responseCallback, callHandler);
//            } catch (IOException e) {
//                e.printStackTrace();
//                processFailure(newCall, e, responseCallback, callHandler);
//            }
//        }
//        return callHandler;
//    }
//
//    /**
//     * 解析Response数据
//     *
//     * @param call
//     * @param response
//     * @param responseCallback
//     */
//    private void processResponse(final Call call, final Response response,
//                                 final ResponseCallback responseCallback, final CallHandler callHandler) {
//        final OkHttpClient.Builder builder = mOkHttpClient.newBuilder();
//        mOkHttpClient = builder.build();
//        Object gsonType = null;
//        Exception exception = null;
//        if (response.isSuccessful()) {
//            try {
//                gsonType = responseCallback.parseResponse(response);
//            } catch (Exception e) {
//                e.printStackTrace();
//                exception = new HttpRequestException(
//                        HttpRequestException.format(call.request(), response), e);
//            }
//            if (gsonType != null) {
//                final Object finalGsonType = gsonType;
//                if (!responseCallback.isSync()) {
//                    mThreadHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            callSuccess(responseCallback, finalGsonType, response, callHandler);
//                        }
//                    });
//                } else {
//                    callSuccess(responseCallback, finalGsonType, response, callHandler);
//                }
//            }
//            response.body().close();
//        } else {
//            exception = new HttpRequestException(
//                    HttpRequestException.format(
//                            call.request(), response), new Throwable(response.message()));
//        }
//        if (exception != null) {
//            if (!responseCallback.isSync()) {
//                final Exception finalException = exception;
//                mThreadHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        callExceptionFail(response, responseCallback, finalException, callHandler, null);
//                    }
//                });
//            } else {
//                callExceptionFail(response, responseCallback, exception, callHandler, null);
//            }
//        }
//    }
//
//    /**
//     * 网络请求成功
//     *
//     * @param responseCallback
//     * @param finalGsonType
//     * @param response
//     */
//    private void callSuccess(ResponseCallback responseCallback, Object finalGsonType,
//                             Response response, CallHandler callHandler) {
//        if (Constants.DEBUG) {
//            responseCallback.onFinish();
//            responseCallback.onSuccess(finalGsonType);
//        } else {
//            try {
//                responseCallback.onFinish();
//                responseCallback.onSuccess(finalGsonType);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        if (!responseCallback.isSync()) {
//            notifyComplete(callHandler);
//        }
//    }
//
//    /**
//     * 网络请求失败
//     *
//     * @param call
//     * @param e
//     * @param responseCallback
//     */
//    private void processFailure(final Call call, final IOException e,
//                                final ResponseCallback responseCallback, final CallHandler callHandler) {
//        ZLog.e("request fail", e);
//        final OkHttpClient.Builder builder = mOkHttpClient.newBuilder();
//        mOkHttpClient = builder.build();
//        final HttpRequestException execption = new HttpRequestException(
//                HttpRequestException.format(call.request(), null), e);
//        if (!responseCallback.isSync()) {
//            mThreadHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    callExceptionFail(null, responseCallback, execption, callHandler, null);
//                }
//            });
//        } else {
//            callExceptionFail(null, responseCallback, execption, callHandler, null);
//        }
//    }
//
//    private void callExceptionFail(Response response, ResponseCallback responseCallback, Exception exception,
//                                   CallHandler callHandler, Object jsonType) {
//        int statusCode = response == null ? ResponseCallback.NO_NETWORK_STATUS_CODE : response.code();
//        if (Constants.DEBUG) {
//            responseCallback.onFinish();
//            responseCallback.onFail(statusCode, jsonType, exception);
//        } else {
//            try {
//                responseCallback.onFinish();
//                responseCallback.onFail(statusCode, jsonType, exception);
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
//        }
//        if (!responseCallback.isSync()) {
//            notifyFailure(callHandler, exception);
//        }
//    }
//
//    public void cancelAll() {
//        mOkHttpClient.dispatcher().cancelAll();
//    }
//
//    private void setHeader(Request.Builder builder, RequestParams params) {
//        if (params != null) {
//            ConcurrentHashMap<String, String> headerParams = params.getHeaderParams();
//            for (ConcurrentHashMap.Entry<String, String> entry : headerParams.entrySet()) {
//                builder.addHeader(entry.getKey(), entry.getValue());
//            }
//        }
//    }
//
//    private void notifyComplete(CallHandler callHandler) {
//        if (callHandler != null) {
//            callHandler.notifyComplete();
//        }
//
//    }
//
//    private void notifyFailure(CallHandler callHandler, Exception e) {
//        if (callHandler != null) {
//            callHandler.notifyFailure(e);
//        }
//
//    }
//
//    public OkHttpClient getOkHttpClient() {
//        return mOkHttpClient;
//    }
//}
