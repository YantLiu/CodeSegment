package com.newrun.cloud.issue.sd.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.newrun.xiruo.xframework.util.ValidateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;

/**
 * @auther liuyanting
 * @create 2018/12/11 19:30
 */
@Slf4j
public class RestTemplateUtil {
    private RestTemplateUtil() {
    }

    /**
     * @param : []
     * @return : org.springframework.http.HttpHeaders
     * @throws :
     * @description : 获取Json请求头
     * @author : liuyanting
     * @date : 2019/12/4 18:20
     */
    public static HttpHeaders getJsonHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("timestamp", String.valueOf(System.currentTimeMillis()));
        return headers;
    }

    public static RestTemplate getDefaultRestTemplate() {
        //设置连接参数
        RestTemplate restTemplate = new RestTemplate();
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(20 * 1000);
        requestFactory.setConnectTimeout(20 * 1000);
        restTemplate.setRequestFactory(requestFactory);
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
        return restTemplate;
    }

    private static volatile RestTemplate instance = null;

    /**
     * @Author: liuyanting
     * @Description: 单例获取连接实例
     * @Param: []
     * @Return: org.springframework.web.client.RestTemplate
     * @Date: 2020/8/10
     */
    public static RestTemplate getInstance() {
        if (instance == null) {
            synchronized(RestTemplateUtil.class) {
                if (instance == null) {
                    instance = getDefaultRestTemplate();
                }
            }
        }
        return instance;
    }

    /**
     * @param restTemplate
     * @param url
     * @return result
     * @description post请求
     * @date 2019/1/17
     */
    public static <T> T post(RestTemplate restTemplate, HttpHeaders headers, String url, Object param, Class<T> returnClz) {
        String jsonString = JSON.toJSONString(param);
        log.debug("RestTemplateUtil.post>>request>>url: {}, params: {}", url, jsonString);
        HttpEntity<String> formEntity = new HttpEntity<>(jsonString, headers);
        T result = restTemplate.postForObject(url, formEntity, returnClz);
        log.debug("RestTemplateUtil.post>>resp>>result: {}", result);
        return result;
    }

    /**
     * @param restTemplate
     * @param url
     * @return result
     * @description post请求
     * @date 2019/1/17
     */
    public static <T> T post(RestTemplate restTemplate, String url, Object param, Class<T> returnClz) {
        return post(restTemplate, getJsonHeader(), url, param, returnClz);
    }

    /**
     * @param restTemplate
     * @param url
     * @return result
     * @description post请求
     * @date 2019/1/17
     */
    public static JSONObject post(RestTemplate restTemplate, String url, Object param) {
        String result =  post(restTemplate, getJsonHeader(), url, param, String.class);
        return JSON.parseObject(result, Feature.OrderedField);
    }

    /**
     * @param restTemplate
     * @param url
     * @return result
     * @description post请求
     * @date 2019/1/17
     */
    public static JSONObject postContent(RestTemplate restTemplate, HttpHeaders headers, String url, String postContent) {
        log.debug("RestTemplateUtil.post>>request>>url: {}, postContent: {}", url, postContent);
        HttpEntity<String> formEntity = new HttpEntity<>(postContent, headers);
        String result = restTemplate.postForObject(url, formEntity, String.class);
        log.debug("RestTemplateUtil.post>>resp>>result: {}", result);
        return JSON.parseObject(result, Feature.OrderedField);
    }

    /**
     * @param : [params]
     * @return : java.lang.String
     * @throws :
     * @description : 拼接get请求url参数后缀
     * @author : liuyanting
     * @date : 2019/10/30 11:14
     */
    private static String buildGetUrl(String url, Map<String, ?> params) {
        if (ValidateUtils.isMapEmpty(params)) {
            return url;
        }
        boolean isFirst = !url.contains("?");
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            if (!ValidateUtils.isObjectEmpty(entry.getValue())) {
                if (isFirst) {
                    sb.append("?");
                    isFirst = false;
                } else {
                    sb.append("&");
                }
                sb.append(String.format("%s={%s}", entry.getKey(), entry.getKey()));
            }
        }
        return url + sb.toString();
    }

    /**
     * @param : [restTemplate, headers, url, params, returnClz]
     * @return : T
     * @throws :
     * @description : get请求
     * @author : liuyanting
     * @date : 2019/12/4 18:52
     */
    public static <T> T get(RestTemplate restTemplate, HttpHeaders headers, String url, Map<String, ?> params, Class<T> returnClz) {
        log.debug("RestTemplateUtil.get>>request>>url: {}, params:{}", url, params);
        // header填充
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity(null, headers);
        url = RestTemplateUtil.buildGetUrl(url, params);
        log.debug(url);
        T result;
        if (params != null && !params.isEmpty()) {//带参请求
            result = restTemplate.exchange(url, HttpMethod.GET, request, returnClz, params).getBody();
        } else {//无参请求
            result = restTemplate.exchange(url, HttpMethod.GET, request, returnClz).getBody();
        }
        log.debug("RestTemplateUtil.get>>resp>>url: {}", url);
        log.debug("RestTemplateUtil.get>>resp>>result: {}", result);
        return result;
    }

    /**
     * @param : [restTemplate, url, params, returnClz]
     * @return : T
     * @throws :
     * @description : get请求
     * @author : liuyanting
     * @date : 2019/10/30 11:15
     */
    public static <T> T get(RestTemplate restTemplate, String url, Map<String, ?> params, Class<T> returnClz) {
        return get(restTemplate, null, url, params, returnClz);
    }


    /**
     * @param : [restTemplate, headers, url, params, returnClz]
     * @return : T
     * @throws :
     * @description : get请求
     * @author : liuyanting
     * @date : 2019/12/4 18:52
     */
    public static String get(RestTemplate restTemplate, HttpHeaders headers, String url, Map<String, ?> params) {
        log.debug("RestTemplateUtil.get>>request>>url: {}, params:{}", url, params);
        // header填充
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity(null, headers);
        url = RestTemplateUtil.buildGetUrl(url, params);
        log.debug(url);
        String result;
        if (params != null && !params.isEmpty()) {//带参请求
            result = restTemplate.exchange(url, HttpMethod.GET, request, String.class, params).getBody();
        } else {//无参请求
            result = restTemplate.exchange(url, HttpMethod.GET, request, String.class).getBody();
        }
        //log.debug("RestTemplateUtil.get>>resp>>url: {}", url);
        log.debug("RestTemplateUtil.get>>resp>>result: {}", result);
        return result;
    }
}
