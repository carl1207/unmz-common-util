package net.unmz.java.util.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Project Name: 常用工具类集合
 * 功能描述：Http工具类<br/>
 * 1.0.0版本 此类采用阿里云分享的HttpsUtils,特此申明来源
 * 1.0.1版本 各种请求方式的请求头中添加字符集,完善相关方法
 * 1.0.2版本 修复请求头中类型问题,设置默认值
 * 1.0.3版本 判断请求中是否有请求参数有则使用传递的,没有则使用默认的
 *
 * @author faritor@unmz.net
 * @version 1.0.3
 * @date 2017-12-09 20:30
 * @since JDK 1.8
 */
public class HttpUtils {

    /**
     * Get方法
     *
     * @param url
     * @return
     * @throws Exception
     */
    public static String doGet(String url) throws Exception {
        HttpResponse response = doGet(url, null, null, null);
        return responseStr(response);
    }

    /**
     * get
     *
     * @param host
     * @param path
     * @param headers
     * @param queries
     * @return
     * @throws Exception
     */
    public static HttpResponse doGet(String host, String path,
                                     Map<String, String> headers,
                                     Map<String, String> queries) throws Exception {
        HttpClient httpClient = wrapClient(host);
        if (queries == null)
            queries = new HashMap<>();

        HttpGet request = new HttpGet(buildUrl(host, path, queries));

        if (headers == null) {
            headers = new HashMap<>();
            setHttpHeader(request);
        }

        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }
        return httpClient.execute(request);
    }

    /**
     * 简化Post请求 返回响应字符串
     *
     * @param host
     * @param path
     * @param body
     * @return
     */
    public static String doPost(String host, String path, Map<String, String> body) throws Exception {
        HttpResponse response = HttpUtils.doPost(host, path, null, null, body);
        return responseStr(response);
    }

    /**
     * post form
     *
     * @param host
     * @param path
     * @param headers
     * @param queries
     * @param bodies
     * @return
     * @throws Exception
     */
    public static HttpResponse doPost(String host, String path,
                                      Map<String, String> headers,
                                      Map<String, String> queries,
                                      Map<String, String> bodies) throws Exception {
        HttpClient httpClient = wrapClient(host);

        if (queries == null)
            queries = new HashMap<>();

        HttpPost request = new HttpPost(buildUrl(host, path, queries));

        if (headers == null) {
            headers = new HashMap<>();
            setHttpHeader(request);
        }

        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        if (bodies != null) {
            List<NameValuePair> nameValuePairList = new ArrayList<>();

            for (String key : bodies.keySet()) {
                nameValuePairList.add(new BasicNameValuePair(key, bodies.get(key)));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, "utf-8");
            formEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
            request.setEntity(formEntity);
        }
        return httpClient.execute(request);
    }

    /**
     * Post String
     *
     * @param host
     * @param path
     * @param headers
     * @param queries
     * @param body
     * @return
     * @throws Exception
     */
    public static HttpResponse doPost(String host, String path,
                                      Map<String, String> headers,
                                      Map<String, String> queries,
                                      String body) throws Exception {
        HttpClient httpClient = wrapClient(host);
        if (queries == null)
            queries = new HashMap<>();

        HttpPost request = new HttpPost(buildUrl(host, path, queries));

        if (headers == null) {
            headers = new HashMap<>();
            setHttpHeader(request);
        }

        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        if (StringUtils.isNotBlank(body)) {
            request.setEntity(new StringEntity(body, "utf-8"));
        }
        return httpClient.execute(request);
    }

    /**
     * Post stream
     *
     * @param host
     * @param path
     * @param headers
     * @param queries
     * @param body
     * @return
     * @throws Exception
     */
    public static HttpResponse doPost(String host, String path,
                                      Map<String, String> headers,
                                      Map<String, String> queries,
                                      byte[] body) throws Exception {
        HttpClient httpClient = wrapClient(host);

        if (queries == null)
            queries = new HashMap<>();

        HttpPost request = new HttpPost(buildUrl(host, path, queries));

        if (headers == null) {
            headers = new HashMap<>();
            setHttpHeader(request);
        }

        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        if (body != null) {
            request.setEntity(new ByteArrayEntity(body));
        }
        return httpClient.execute(request);
    }

    /**
     * Put String
     *
     * @param host
     * @param path
     * @param headers
     * @param queries
     * @param body
     * @return
     * @throws Exception
     */
    public static HttpResponse doPut(String host, String path,
                                     Map<String, String> headers,
                                     Map<String, String> queries,
                                     String body) throws Exception {
        HttpClient httpClient = wrapClient(host);
        if (queries == null)
            queries = new HashMap<>();

        HttpPut request = new HttpPut(buildUrl(host, path, queries));

        if (headers == null) {
            headers = new HashMap<>();
            setHttpHeader(request);
        }

        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        if (StringUtils.isNotBlank(body)) {
            request.setEntity(new StringEntity(body, "utf-8"));
        }
        return httpClient.execute(request);
    }

    /**
     * Put stream
     *
     * @param host
     * @param path
     * @param headers
     * @param queries
     * @param body
     * @return
     * @throws Exception
     */
    public static HttpResponse doPut(String host, String path,
                                     Map<String, String> headers,
                                     Map<String, String> queries,
                                     byte[] body) throws Exception {
        HttpClient httpClient = wrapClient(host);
        if (queries == null)
            queries = new HashMap<>();

        HttpPut request = new HttpPut(buildUrl(host, path, queries));

        if (headers == null) {
            headers = new HashMap<>();
            setHttpHeader(request);
        }

        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        if (body != null) {
            request.setEntity(new ByteArrayEntity(body));
        }
        return httpClient.execute(request);
    }

    /**
     * Delete
     *
     * @param host
     * @param path
     * @param headers
     * @param queries
     * @return
     * @throws Exception
     */
    public static HttpResponse doDelete(String host, String path,
                                        Map<String, String> headers,
                                        Map<String, String> queries) throws Exception {
        HttpClient httpClient = wrapClient(host);
        if (queries == null)
            queries = new HashMap<>();

        HttpDelete request = new HttpDelete(buildUrl(host, path, queries));

        if (headers == null) {
            headers = new HashMap<>();
            setHttpHeader(request);
        }

        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }
        return httpClient.execute(request);
    }

    private static String buildUrl(String host, String path, Map<String, String> queries) throws UnsupportedEncodingException {
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(host);
        if (!StringUtils.isBlank(path)) {
            sbUrl.append(path);
        }
        if (null != queries) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, String> query : queries.entrySet()) {
                if (0 < sbQuery.length()) {
                    sbQuery.append("&");
                }
                if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
                    sbQuery.append(query.getValue());
                }
                if (!StringUtils.isBlank(query.getKey())) {
                    sbQuery.append(query.getKey());
                    if (!StringUtils.isBlank(query.getValue())) {
                        sbQuery.append("=");
                        sbQuery.append(URLEncoder.encode(query.getValue(), "utf-8"));
                    }
                }
            }
            if (0 < sbQuery.length()) {
                sbUrl.append("?").append(sbQuery);
            }
        }

        return sbUrl.toString();
    }

    private static HttpClient wrapClient(String host) {
        HttpClient httpClient = new DefaultHttpClient();
        if (host.startsWith("https://")) {
            sslClient(httpClient);
        }

        return httpClient;
    }

    private static void sslClient(HttpClient httpClient) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] xcs, String str) {

                }

                public void checkServerTrusted(X509Certificate[] xcs, String str) {

                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = httpClient.getConnectionManager();
            SchemeRegistry registry = ccm.getSchemeRegistry();
            registry.register(new Scheme("https", 443, ssf));
        } catch (KeyManagementException | NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String responseStr(HttpResponse response) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            //获取返回实例entity
            HttpEntity entity = response.getEntity();
            //通过EntityUtils的一个工具方法获取返回内容
            return EntityUtils.toString(entity, "utf-8");
        } else {
            return "请求失败,错误码为: " + statusCode;
        }
    }

    private static HttpRequest setHttpHeader(HttpRequest request) {
        request.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8"));
        return request;
    }
}