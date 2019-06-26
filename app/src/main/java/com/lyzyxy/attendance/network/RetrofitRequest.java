package com.lyzyxy.attendance.network;

import android.content.Context;
import android.widget.Toast;
import com.lyzyxy.attendance.R;
import com.lyzyxy.attendance.network.result.RequestResult;
import com.lyzyxy.attendance.util.AuthUtil;
import com.lyzyxy.attendance.util.Constant;
import com.lyzyxy.attendance.util.NetworkUtil;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Retrofit请求类
 */
public class RetrofitRequest {
    private static int TIME_OUT = 30; // 30秒超时断开连接

    // 自定义一个信任所有证书的TrustManager，添加SSLSocketFactory的时候要用到
    private static X509TrustManager trustAllCert = new X509TrustManager() {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }
    };
    private static SSLSocketFactory sslSocketFactory = new SSLSocketFactoryCompat(trustAllCert);

    // httpclient
    public static OkHttpClient client = new OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCert)
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .build();

    /**
     * 网络框架单例
     * <p>因为示例中用到不同的接口地址，URL_BASE随便写了一个，如果是实际项目中，则设置为根路径即可</p>
     */
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.URL_BASE)
            .client(client)
            .build();

    /**
     * 发送GET网络请求
     * @param url 请求地址
     * @param clazz 返回的数据类型
     * @param resultHandler 回调
     * @param <T> 泛型
     */
    public static <T> void sendGetRequest(String url, Map<String, Object> paramMap,String token, final Class<T> clazz, final boolean isList, final ResultHandler resultHandler) {
        // 判断网络连接状况
        if (resultHandler.isNetDisconnected()) {
            resultHandler.onAfterFailure();
            return;
        }

        GetRequest getRequest = retrofit.create(GetRequest.class);

        Call<ResponseBody> call = null;
        // 构建请求
        if(token != null && !token.equals("")){
            call = getRequest.getUrl(token,url,paramMap);
        }else{
            call = getRequest.getUrl(url,paramMap);
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                resultHandler.onBeforeResult();
                try {
                    ResponseBody body = response.body();
                    if (body == null) {
                        ResponseBody errorBody = response.errorBody();
                        String err = errorBody.string();
                        RequestResult result = RequestResult.fromJsonObject(err,String.class);

                        resultHandler.onServerError(result);
                        resultHandler.onAfterFailure();
                        return;
                    }
                    String string = body.string();

                    RequestResult requestResult = null;
                    if(isList)
                        requestResult = RequestResult.fromJsonArray(string,clazz);
                    else
                        requestResult = RequestResult.fromJsonObject(string,clazz);

                    resultHandler.onResult(requestResult);
                } catch (IOException e) {
                    e.printStackTrace();
                    resultHandler.onFailure(e);
                    resultHandler.onAfterFailure();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                resultHandler.onFailure(t);
                resultHandler.onAfterFailure();
            }
        });
    }

    /**
     * 发送post网络请求
     * @param url 请求地址
     * @param paramMap 参数列表
     * @param clazz 返回的数据类型
     * @param resultHandler 回调
     * @param <T> 泛型
     */
    public static <T> void sendPostRequest(String url, Map<String, Object> paramMap, String token,final Class<T> clazz,final boolean isList, final ResultHandler resultHandler) {
        // 判断网络连接状况
        if (resultHandler.isNetDisconnected()) {
            resultHandler.onAfterFailure();
            return;
        }
        PostRequest postRequest = retrofit.create(PostRequest.class);

        Call<ResponseBody> call = null;
        // 构建请求
        if(token != null && !token.equals("")){
            call = postRequest.postMap(token,url, paramMap);
        }else{
            call = postRequest.postMap(url, paramMap);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                resultHandler.onBeforeResult();
                try {
                    ResponseBody body = response.body();
                    if (body == null) {
                        ResponseBody errorBody = response.errorBody();
                        String err = errorBody.string();
                        RequestResult result = RequestResult.fromJsonObject(err,String.class);

                        resultHandler.onServerError(result);
                        resultHandler.onAfterFailure();
                        return;
                    }
                    String string = body.string();

                    RequestResult requestResult = null;
                    if(isList)
                        requestResult = RequestResult.fromJsonArray(string,clazz);
                    else
                        requestResult = RequestResult.fromJsonObject(string,clazz);

                    resultHandler.onResult(requestResult);
                } catch (IOException e) {
                    e.printStackTrace();
                    resultHandler.onFailure(e);
                    resultHandler.onAfterFailure();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                resultHandler.onFailure(t);
                resultHandler.onAfterFailure();
            }
        });
    }

    /**
     * 发送上传文件网络请求
     * @param url 请求地址
     * @param file 文件
     * @param clazz 返回的数据类型
     * @param resultHandler 回调
     * @param <T> 泛型
     */
    public static <T> void fileUpload(String url, File file,Map<String, Object> requestMap, String token, final Class<T> clazz,final boolean isList, final ResultHandler<T> resultHandler) {
        // 判断网络连接状况
        if (resultHandler.isNetDisconnected()) {
            resultHandler.onAfterFailure();
            return;
        }
        FileRequest fileRequest = retrofit.create(FileRequest.class);

        Map<String, RequestBody> paramMap = new HashMap<>();
        RetrofitRequest.addMultiPart(paramMap, "file", file);

        Call<ResponseBody> call = null;

        if(token != null && !token.equals("")){
            call = fileRequest.postFile(token, url, paramMap,requestMap);
        }else{
            call = fileRequest.postFile(url, paramMap,requestMap);
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                resultHandler.onBeforeResult();
                try {
                    ResponseBody body = response.body();
                    if (body == null) {
                        ResponseBody errorBody = response.errorBody();
                        String err = errorBody.string();
                        RequestResult result = RequestResult.fromJsonObject(err,String.class);

                        resultHandler.onServerError(result);
                        resultHandler.onAfterFailure();
                        return;
                    }
                    String string = body.string();
                    RequestResult requestResult = null;
                    if(isList)
                        requestResult = RequestResult.fromJsonArray(string,clazz);
                    else
                        requestResult = RequestResult.fromJsonObject(string,clazz);

                    resultHandler.onResult(requestResult);
                } catch (IOException e) {
                    e.printStackTrace();
                    resultHandler.onFailure(e);
                    resultHandler.onAfterFailure();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                resultHandler.onFailure(t);
                resultHandler.onAfterFailure();
            }
        });
    }

    /**
     * 文件下载
     * @param url 请求地址
     * @param downloadHandler 回调接口
     */
    public static void fileDownload(String url, final DownloadHandler downloadHandler) {
        // 回调方法执行器，定义回调在子线程中执行，避免Callback返回到MainThread，导致文件下载出现NetworkOnMainThreadException
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        // 网络框架
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.URL_BASE)
                .callbackExecutor(executorService)
                .build();

        FileRequest fileRequest = retrofit.create(FileRequest.class);
        // 构建请求
        Call<ResponseBody> call = fileRequest.download(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // 写入文件
                    downloadHandler.onBody(response.body());
                } else {
                    downloadHandler.onError();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                downloadHandler.onError();
            }
        });
    }

    /**
     * 添加多媒体类型
     *
     * @param paramMap 参数对
     * @param key      键
     * @param obj      值
     */
    private static void addMultiPart(Map<String, RequestBody> paramMap, String key, Object obj) {
        if (obj instanceof String) {
            RequestBody body = RequestBody.create(MediaType.parse("text/plain;charset=UTF-8"), (String) obj);
            paramMap.put(key, body);
        } else if (obj instanceof File) {
            RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data;charset=UTF-8"), (File) obj);
            paramMap.put(key + "\"; filename=\"" + ((File) obj).getName() + "", body);
        }
    }

    /**
     * 网络请求结果处理类
     * @param <T> 请求结果封装对象
     */
    public static abstract class ResultHandler<T> {
        Context context;

        public ResultHandler(Context context) {
            this.context = context;
        }

        /**
         * 判断网络是否未连接
         *
         * @return
         */
        public boolean isNetDisconnected() {
            return NetworkUtil.isNetDisconnected(context);
        }

        /**
         * 请求成功之前
         */
        public abstract void onBeforeResult();

        /**
         * 请求成功时
         *
         * @param r 结果数据
         */
        public abstract void onResult(RequestResult<T> r);

        /**
         * 服务器出错
         */
        public void onServerError(RequestResult err) {
            if(err.getCode() == 401){
                AuthUtil.auth(context);
            }else {
                // 服务器处理出错
                Toast.makeText(context, R.string.net_server_error, Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * 请求失败后的处理
         */
        public abstract void onAfterFailure();

        /**
         * 请求失败时的处理
         *
         * @param t
         */
        public void onFailure(Throwable t) {
            if (t instanceof SocketTimeoutException || t instanceof ConnectException) {
                // 连接异常
                if (NetworkUtil.isNetworkConnected(context)) {
                    // 服务器连接出错
                    Toast.makeText(context, R.string.net_server_connected_error, Toast.LENGTH_SHORT).show();
                } else {
                    // 手机网络不通
                    Toast.makeText(context, R.string.net_not_connected, Toast.LENGTH_SHORT).show();
                }
            } else if (t instanceof Exception) {
                // 功能异常
                Toast.makeText(context, R.string.net_unknown_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 文件下载回调
     */
    public interface DownloadHandler {
        /**
         * 接收到数据体
         * @param body 响应体
         */
        public void onBody(ResponseBody body);

        /**
         * 文件下载出错
         */
        public void onError();
    }
}
