package com.alt.base.common.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * @auther Administrator XIONGSY
 * @create 2018/12/11 19:30
 */
@Slf4j
public class RestTemplateUtil {
    private RestTemplateUtil() {
    }

    /**
     * @param restTemplate
     * @param url
     * @return result
     * @description post请求
     * @date 2019/1/17
     */
    public static <T> T post(RestTemplate restTemplate, String url, Object param, Class<T> returnClz) {
        String jsonString = JSON.toJSONString(param);
        log.info("RestTemplateUtil.post>>restTemplate = {},url = {}, params = {}", restTemplate, url, jsonString);

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());

        HttpEntity<String> formEntity = new HttpEntity<>(jsonString, headers);
        T result = restTemplate.postForObject(url, formEntity, returnClz);
        log.info("RestTemplateUtil.post>>result = {} ", result);
        return result;
    }

    /**
     * @param file
     * @return org.springframework.http.ResponseEntity<org.springframework.core.io.Resource>
     * @description 构建下载类
     * @author liuyanting
     * @date 2019/1/17
     */
    public static ResponseEntity<Resource> buildDownloadResponseEntity(File file) throws IOException {
        String fileName = file.getName();
        org.springframework.core.io.Resource body = new FileSystemResource(file);

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String header = request.getHeader("User-Agent").toUpperCase();
        HttpStatus status = HttpStatus.CREATED;
        if (header.contains("MSIE") || header.contains("TRIDENT") || header.contains("EDGE")) {
            fileName = URLEncoder.encode(fileName, "UTF-8");
            fileName = fileName.replace("+", "%20");    // IE下载文件名空格变+号问题
            status = HttpStatus.OK;
        } else {
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(file.length());

        return new ResponseEntity<>(body, headers, status);
    }
}
