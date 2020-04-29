package com.lyt.excel;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Slf4j
public class DateUtil {
    private DateUtil() {
    }

    // ======================日期格式化常量=====================//

    public static final String YYYY_MM_DDHHMMSS = "yyyy-MM-dd HH:mm:ss";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String YYYY_MM = "yyyy-MM";

    public static final String YYYY = "yyyy";

    public static final String MM = "MM";

    public static final String DD = "dd";


    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static final String YYYYMMDD = "yyyyMMdd";

    public static final String HH = "HH";

    public static final String YYYYMM = "yyyyMM";

    public static final String YYYYMMDDHHMMSS_1 = "yyyy/MM/dd HH:mm:ss";

    public static final String YYYY_MM_DD_1 = "yyyy/MM/dd";

    public static final String YYYY_MM_1 = "yyyy/MM";

    /**
     * 自定义取值，Date类型转为String类型
     *
     * @param date    日期
     * @param pattern 格式化常量
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String dateToStr(Date date, String pattern) {
        SimpleDateFormat format = null;
        if (null == date)
            return null;
        format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.format(date);
    }

    /**
     * 将字符串转换成Date类型的时间
     * <hr>
     *
     * @param s 日期类型的字符串<br>
     *          datePattern :YYYY_MM_DD<br>
     * @return java.util.Date
     */
    public static Date strToDate(String s, String pattern) {
        if (ValidateUtils.isStringEmpty(s)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(s);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("日期格式有误");
        }
    }
}
