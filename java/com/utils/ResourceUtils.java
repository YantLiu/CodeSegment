package com.alt.base.common.util;

import com.alt.base.common.dto.response.BaseRespDTO;
import com.alt.base.common.enums.ErrorCodeEnum;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @auther Administrator XIONGSY
 * @create 2018/11/20 9:06
 */
@Slf4j
public class ResourceUtils {
    private ResourceUtils() {
    }

    public static Object getResourceFile(String resourcePath) {
        try {
            ClassPathResource classPathResource = new ClassPathResource(resourcePath);
            String filename = classPathResource.getFilename();
            @Cleanup InputStream inputStream = classPathResource.getInputStream();
            byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
            String fileName = new String(filename.getBytes("UTF-8"), "iso-8859-1");// 为了解决中文名称乱码问题
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            return new ResponseEntity<>(bytes, headers, HttpStatus.CREATED);
        } catch (FileNotFoundException e) {
            log.error(ErrorCodeEnum.GET_FILE_FAILED.getMsg(), e);
            return new BaseRespDTO(ErrorCodeEnum.FILE_NOT_EXISTS);
        } catch (IOException e) {
            log.error(ErrorCodeEnum.GET_FILE_FAILED.getMsg(), e);
            return new BaseRespDTO(ErrorCodeEnum.GET_TEMPLATE_FAILED);
        }
    }
}
