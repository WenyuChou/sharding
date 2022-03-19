package com.zhou.sharding.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.util.StringUtils;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Wenyu Zhou
 */
@Slf4j
public class HttpClient {

    public static String[] post(String url, String param, Integer timeOut) {
        Map<String, String> heads = new HashMap<>(2);
        heads.put("Content-Type", "application/json");
        return doHttp(url, param, heads, HttpMethod.POST, timeOut);
    }

    public static String[] sendHttp(String url, String param, Map<String, String> headers, HttpMethod method, Integer timeOut) {
        method = method == null ? HttpMethod.GET : method;
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        if (!StringUtils.isEmpty(param) && HttpMethod.GET == method) {
            url = url + "?" + param;
        }
        int responseCode;
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            HttpURLConnection httpUrlConnection = (HttpURLConnection) conn;
            if (timeOut != null && timeOut > 0) {
                httpUrlConnection.setReadTimeout(timeOut * 1000);
            }
            httpUrlConnection.setRequestMethod(method.name());
            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpUrlConnection.setRequestProperty(header.getKey(), header.getValue());
            }
            httpUrlConnection.setDoInput(true);
            boolean bool = !StringUtils.isEmpty(param)
                    && (HttpMethod.POST == method || HttpMethod.PUT == method);
            if (bool) {
                httpUrlConnection.setDoOutput(true);
                out = new PrintWriter(httpUrlConnection.getOutputStream());
                out.print(param);
                out.flush();
            }
            httpUrlConnection.connect();
            /*
             * 判断getResponseCode，
             * 当返回不是HttpURLConnection.HTTP_OK， HttpURLConnection.HTTP_CREATED， HttpURLConnection.HTTP_ACCEPTED 时，
             * 不能用getInputStream()，而是应该用getErrorStream()
             */
            responseCode = httpUrlConnection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                in = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(httpUrlConnection.getErrorStream()));
            }
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = new StringBuilder();
            responseCode = HttpsURLConnection.HTTP_INTERNAL_ERROR;
        } finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        log.info("httpClient请求 - url:{} , responseCode:{} , result:{}", url, responseCode, result);
        return new String[]{String.valueOf(responseCode), result.toString()};
    }

    public static String[] sendHttps(String url, String param, Map<String, String> headers, HttpMethod method, Integer timeOut) {
        method = method == null ? HttpMethod.GET : method;
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        if (!StringUtils.isEmpty(param) && HttpMethod.GET == method) {
            url = url + "?" + param;
        }
        int responseCode;
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            URL realUrl = new URL(null, url, new sun.net.www.protocol.https.Handler());
            URLConnection conn = realUrl.openConnection();
            HttpsURLConnection httpsUrlConnection = (HttpsURLConnection) conn;
            if (timeOut != null && timeOut > 0) {
                httpsUrlConnection.setReadTimeout(timeOut * 1000);
            }
            httpsUrlConnection.setRequestMethod(method.name());
            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpsUrlConnection.setRequestProperty(header.getKey(), header.getValue());
            }

            httpsUrlConnection.setDoInput(true);
            boolean bool = !StringUtils.isEmpty(param)
                    && (HttpMethod.POST == method || HttpMethod.PUT == method);
            if (bool) {
                httpsUrlConnection.setDoOutput(true);
                out = new PrintWriter(httpsUrlConnection.getOutputStream());
                out.print(param);
                out.flush();
            }
            httpsUrlConnection.connect();
            responseCode = httpsUrlConnection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                in = new BufferedReader(new InputStreamReader(httpsUrlConnection.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(httpsUrlConnection.getErrorStream()));
            }
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = new StringBuilder();
            responseCode = HttpsURLConnection.HTTP_INTERNAL_ERROR;
        } finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        log.info("httpClient请求 - url:{} , responseCode:{} , result:{}", url, responseCode, result);
        return new String[]{String.valueOf(responseCode), result.toString()};
    }

    /**
     * 带重试机制的http请求
     *
     * @param url           地址
     * @param param         参数
     * @param method        GET/POST/PUT/DELETE/...
     * @param timeOut       超时时间(秒)
     * @param retryTimes    重试次数
     * @param retryInterval 重试等待时间(毫秒)
     * @return result[0]:responseCode ; result[1] 返回结果
     */
    public static String[] doHttpRetry(String url, String param, HttpMethod method, Integer timeOut, Integer retryTimes, Long retryInterval) {
        try {
            String[] result;
            result = doHttp(url, param, new HashMap<>(2), method, timeOut);
            while (--retryTimes > 0 && Integer.parseInt(result[0]) != HttpsURLConnection.HTTP_OK) {
                Thread.sleep(retryInterval);
                result = doHttp(url, param, new HashMap<>(2), method, timeOut);
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new String[]{String.valueOf(HttpsURLConnection.HTTP_INTERNAL_ERROR), null};
        }
    }

    public static String[] doHttp(String url, String param, HttpMethod method, Integer timeOut) {
        return doHttp(url, param, new HashMap<>(), method, timeOut);
    }

    public static String[] doHttp(String url, String param, Map<String, String> headers, HttpMethod method, Integer timeOut) {
        return url.startsWith("https")
                ? sendHttps(url, param, headers, method, timeOut)
                : sendHttp(url, param, headers, method, timeOut);
    }

    public static class NullHostNameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String arg0, SSLSession arg1) {
            return true;
        }
    }

    static TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }};


}
