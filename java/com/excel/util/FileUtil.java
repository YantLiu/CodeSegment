package com.lyt.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @program: newrun-etc
 * @description:
 * @author: liuyanting
 * @create: 2019-09-21 16:53
 **/
@Slf4j
public class FileUtil {
    private FileUtil() {
    }

    /**
     * @param : [file]
     * @return : void
     * @throws :
     * @Description : 若文件不存在则创建
     * @author : liuyanting
     * @date : 2019/9/21 17:30
     */
    public static void createFileIfNull(File file) throws IOException {
        if (!file.getParentFile().isDirectory()) {
            file.getParentFile().mkdirs();
        }
        if (!file.isFile()) {
            file.createNewFile();
        }
    }

    /**
     * @param : [filePath]
     * @return : void
     * @throws :
     * @Description : 若文件不存在则创建
     * @author : liuyanting
     * @date : 2019/9/21 17:31
     */
    public static File createFileIfNull(String filePath) throws IOException {
        File file = new File(filePath);
        createFileIfNull(file);
        return file;
    }

    /**
     * @param : [filePath]
     * @return : void
     * @throws :
     * @Description : 若文件不存在则创建
     * @author : liuyanting
     * @date : 2019/9/21 17:31
     */
    public static void createDirIfNull(File dir) {
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
    }

    /**
     * @param : [filePath]
     * @return : void
     * @throws :
     * @Description : 若文件不存在则创建
     * @author : liuyanting
     * @date : 2019/9/21 17:31
     */
    public static File createDirIfNull(String dirPath) {
        File dir = new File(dirPath);
        createDirIfNull(dir);
        return dir;
    }


    /**
     * @param : [resourcePath: 资源路径, outFile: 输出文件]
     * @return : void
     * @throws :
     * @Description : 获取资源文件
     * @author : liuyanting
     * @date : 2019/9/21 17:02
     */
    public static void getResourceFile(String resourcePath, File outFile) throws IOException {
        createFileIfNull(outFile);
        InputStream inputStream = new ClassPathResource(resourcePath).getInputStream();
        //优化, 当文件大小发生改变时才重新获取资源文件
        if (inputStream.available() != outFile.length()) {
            getFile(inputStream, outFile);
        }
    }

    /**
     * @param : [bytes, outFile]
     * @return : void
     * @throws :
     * @description : 将InputStream转换成文件
     * @author : liuyanting
     * @date : 2019/10/23 15:28
     */
    public static void getFile(InputStream is, File outFile) throws IOException {
        createFileIfNull(outFile);
        try (
                BufferedInputStream in = new BufferedInputStream(is);
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
        ) {
            int len;
            byte[] b = new byte[1024];
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
            out.flush();
        }
    }

    /**
     * @param : [bytes, outFile]
     * @return : void
     * @throws :
     * @description : 将Byte数组转换成文件
     * @author : liuyanting
     * @date : 2019/10/23 15:28
     */
    public static void getFile(byte[] bytes, File outFile) throws IOException {
        createFileIfNull(outFile);
        try (
                FileOutputStream fos = new FileOutputStream(outFile);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
        ) {
            bos.write(bytes);
            bos.flush();
        }
    }

    /**
     * 从网络Url中下载文件
     *
     * @param urlStr
     * @param outFile
     * @throws IOException
     */
    public static void downLoadFromUrl(String urlStr, File outFile) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(30 * 1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        getFile(inputStream, outFile);
    }

    /**
     * @param : [urlStr]
     * @return : java.io.File
     * @throws :
     * @description : 从网络Url中下载文件
     * @author : liuyanting
     * @date : 2019/10/24 15:57
     */
    public static File downLoadFromUrl(String urlStr) throws IOException {
        String filename = urlStr.substring(urlStr.lastIndexOf('/') + 1);
        File outFile = new File(AppConstants.getTemporaryLocation() + filename);
        FileUtil.downLoadFromUrl(urlStr, outFile);
        return outFile;
    }

    /**
     * @param : [file]
     * @return : byte[]
     * @throws :
     * @description : 文件转字节数组
     * @author : liuyanting
     * @date : 2019/10/24 16:59
     */
    public static byte[] fileToBytes(File file) throws IOException {
        try (
                FileInputStream in = new FileInputStream(file);
        ) {
            return inputStreamToBytes(in);
        }
    }

    /**
     * @param : [file]
     * @return : byte[]
     * @throws :
     * @description : 文件转字节数组
     * @author : liuyanting
     * @date : 2019/10/24 16:59
     */
    public static byte[] inputStreamToBytes(InputStream in) throws IOException {
        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
        ) {
            int len;
            byte[] b = new byte[1024];
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
            }
            out.flush();
            return out.toByteArray();
        }
    }
}