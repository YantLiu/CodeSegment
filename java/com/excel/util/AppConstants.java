package com.lyt.excel;

import java.io.File;

/**
 * @program: newrun-etc
 * @description:
 * @author: liuyanting
 * @create: 2019-09-17 14:45
 **/
public class AppConstants {
    private AppConstants() {
    }

    //系统临时文件目录
    private static final String TEMPORARY_LOCATION = System.getProperty("java.io.tmpdir");

    //若 系统临时文件目录不存在则创建
    public static String getTemporaryLocation() {
        File tempFile = new File(TEMPORARY_LOCATION);
        FileUtil.createDirIfNull(tempFile);
        return tempFile.getAbsolutePath() + File.separator;
    }
}
