package com.alt.base.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: hhl
 * @Description: excel通用工具类
 * @Create: 2018-11-13 17:45
 */
@Slf4j
public class ExcelUtils {
    /**
     * 无参构造方法
     */
    private ExcelUtils() {
    }

    private static final String TRANSACTION_NO = "transactionNo";

    /**
     * 根据流读取Excel文件
     *
     * @param inputStream
     * @param isExcel2003
     * @return
     * @see [类、类#方法、类#成员]
     */
    public List<List<String>> read(InputStream inputStream, boolean isExcel2003)
            throws IOException {

        List<List<String>> dataLst = null;

        /** 根据版本选择创建Workbook的方式 */
        Workbook wb = null;

        if (isExcel2003) {
            wb = new HSSFWorkbook(inputStream);
        } else {
            wb = new XSSFWorkbook(inputStream);
        }
        dataLst = readDate(wb);

        return dataLst;
    }

    /**
     * 读取数据
     *
     * @param wb
     * @return
     * @see [类、类#方法、类#成员]
     */
    private List<List<String>> readDate(Workbook wb) {

        List<List<String>> dataLst = new ArrayList<List<String>>();

        /** 得到第一个shell */
        Sheet sheet = wb.getSheetAt(0);

        /** 得到Excel的行数 */
        int totalRows = sheet.getPhysicalNumberOfRows();

        /** 得到Excel的列数 */
        int totalCells = 0;
        if (totalRows >= 1 && sheet.getRow(0) != null) {
            totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }

        /** 循环Excel的行 */
        for (int r = 0; r < totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }

            List<String> rowLst = new ArrayList<String>();

            /** 循环Excel的列 */
            for (int c = 1; c < totalCells; c++) {

                Cell cell = row.getCell(c);
                String cellValue = "";

                if (null != cell) {
                    // 以下是判断数据的类型
                    switch (cell.getCellType()) {
                        case HSSFCell.CELL_TYPE_NUMERIC: // 数字
                            cellValue = cell.getNumericCellValue() + "";
                            break;

                        case HSSFCell.CELL_TYPE_STRING: // 字符串
                            cellValue = cell.getStringCellValue();
                            break;

                        case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
                            cellValue = cell.getBooleanCellValue() + "";
                            break;

                        case HSSFCell.CELL_TYPE_FORMULA: // 公式
                            cellValue = cell.getCellFormula() + "";
                            break;

                        case HSSFCell.CELL_TYPE_BLANK: // 空值
                            cellValue = "";
                            break;

                        case HSSFCell.CELL_TYPE_ERROR: // 故障
                            cellValue = "非法字符";
                            break;

                        default:
                            cellValue = "未知类型";
                            break;
                    }
                }

                rowLst.add(cellValue);
            }

            /** 保存第r行的第c列 */
            dataLst.add(rowLst);
        }

        return dataLst;
    }

    /**
     * 读取列表数据
     * <按顺序放入带有注解的实体成员变量中>
     *
     * @param wb        工作簿
     * @param t         实体
     * @param beginLine 开始行数
     * @param totalcut  结束行数减去相应行数
     * @return List<T> 实体列表
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unchecked")
    public static <T> Map readDateListT(Workbook wb, T t, int beginLine, int totalcut) throws IllegalAccessException, InstantiationException {
        List<T> listt = new ArrayList<T>();
        /** 得到第一个shell */
        Sheet sheet = wb.getSheetAt(0);
        /** 得到Excel的行数 */
        int totalRows = sheet.getPhysicalNumberOfRows();
        /** 得到Excel的列数 */
        int totalCells = 0;
        if (totalRows >= 0 && sheet.getRow(0) != null) {
            totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }
        //创建解析异常消息存储对象
        Map map = new HashMap<>();
        LinkedHashMap lhm = null;
        /** 循环Excel的行 */
        int lineNum = 1;
        //判断是否可加入数组
        boolean isAdd = true;
        for (int r = beginLine - 1; r < totalRows - totalcut; r++) {
            Object newInstance = t.getClass().newInstance();
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            // 成员变量的值
            Object entityMemberValue = "";
            // 所有成员变量
            Field[] fields = t.getClass().getDeclaredFields();
            // 列开始下标
            int startCell = 0;
            StringBuilder sbf = new StringBuilder();
            for (int f = 1; f < fields.length; f++) {
                fields[f].setAccessible(true);
                String fieldName = fields[f].getName();
                Cell cell = row.getCell(startCell);
                String cellValue = getCellValue(cell);
                //判断交易编号是否存在且是否为空
                if (TRANSACTION_NO.equals(fieldName) && ValidateUtils.isStringEmpty(cellValue)) {
                    isAdd = false;
                    break;
                }
                //合同类型获取数值
                if ("contractType".equals(fieldName) && !ValidateUtils.isStringEmpty(cellValue)) {
                    cellValue = MapUtil.getContractType.get(cellValue);
                }
                //手机号码获取数值 去除0.00
                if (("telephone1".equals(fieldName) || "telephone2".equals(fieldName)) && !ValidateUtils.isStringEmpty(cellValue) && cellValue.indexOf(".00") != -1) {
                    cellValue = cellValue.substring(0, cellValue.indexOf('.'));
                }
                //交易序号获取数值 去除0.00
                if ((TRANSACTION_NO.equals(fieldName)) && !ValidateUtils.isStringEmpty(cellValue) && cellValue.indexOf('.') != -1) {
                    cellValue = cellValue.substring(0, cellValue.indexOf('.'));
                }

                try {
                    entityMemberValue = getEntityMemberValue(fields[f], cellValue);
                    // 赋值
                    PropertyUtils.setProperty(newInstance, fieldName, entityMemberValue);
                } catch (Exception e) {
                    //init error对象存储
                    if (lhm == null) {
                        lhm = new LinkedHashMap();
                    }
                    int columnNum = f;
                    columnNum = ++columnNum;
                    sbf.append("第" + lineNum + "行第" + columnNum + "列" + "数据格式有误，解析失败|");
                }
                // 列的下标加1
                startCell++;
            }
            String toString = sbf.toString();
            if (!ValidateUtils.isBlank(toString)) {
                lhm.put(lineNum, toString.substring(0, toString.length() - 1));
            }
            lineNum++;
            //数组添加
            if (isAdd) {
                listt.add((T) newInstance);
            }
        }
        map.put("error", lhm);
        map.put("result", listt);
        return map;
    }

    /**
     * 读取列表数据
     * <按顺序放入带有注解的实体成员变量中>
     *
     * @param wb       工作簿
     * @param t        实体
     * @param beginRow 开始行数
     * @param cutRow   末尾不解析的行数
     * @return List<T> 实体列表
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unchecked")
    public static <T> Map readData(Workbook wb, T t, int beginRow, int cutRow) throws IllegalAccessException, InstantiationException {
        //返回消息存储对象
        Map map = new HashMap<>();
        //解析结果对象列表
        List<T> tList = new ArrayList<T>();
        //错误信息
        Map errorMap = new LinkedHashMap();
        StringBuilder sb = new StringBuilder();

        // 所有成员变量
        Field[] fields = t.getClass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
        }

        /** 取第一个shell */
        Sheet sheet = wb.getSheetAt(0);
        /** Excel行数 */
        int totalRows = sheet.getPhysicalNumberOfRows();
        /** Excel列数 */
        int totalCells = 0;
        int headRowIndex = beginRow - 2;
        if (totalRows < 1 || headRowIndex < 0) {
            return map;
        } else if (sheet.getRow(headRowIndex) != null) {
            totalCells = sheet.getRow(headRowIndex).getPhysicalNumberOfCells();
            if (totalCells > fields.length) {
                totalCells = fields.length;
            }
        }

        //解析行数
        int rowLength = totalRows - cutRow;
        /** 循环Excel的行 */
        for (int rowIndex = beginRow - 1; rowIndex < rowLength; rowIndex++) {
            Object newInstance = t.getClass().newInstance();
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            //行码
            int rowNum = rowIndex + 1;
            for (int colIndex = 0; colIndex < totalCells; colIndex++) {
                //取cell的值
                Cell cell = row.getCell(colIndex);
                String cellValue = getCellValue(cell);
                try {
                    // 成员变量的值
                    Object entityMemberValue = getEntityMemberValue(fields[colIndex], cellValue);
                    //注入cell的值
                    String fieldName = fields[colIndex].getName();
                    PropertyUtils.setProperty(newInstance, fieldName, entityMemberValue);
                } catch (Exception e) {
                    log.warn("ExcelUtils.readData", e);
                    //列码
                    int colNum = colIndex + 1;
                    sb.append(" 第" + rowNum + "行第" + colNum + "列" + "数据格式有误，解析失败 ");
                }
            }
            tList.add((T) newInstance);
            String errorMsg = sb.toString();
            if (!ValidateUtils.isBlank(errorMsg)) {
                errorMap.put(rowNum, errorMsg);
                sb.setLength(0);
            }
        }

        map.put("error", errorMap);
        map.put("result", tList);
        return map;
    }

    /**
     * 根据Excel表格中的数据判断类型得到值
     *
     * @param cell
     * @return
     * @see [类、类#方法、类#成员]
     */
    private static String getCellValue(Cell cell) {
        String cellValue = "";

        if (null != cell) {
            // 以下是判断数据的类型
            switch (cell.getCellType()) {
                case HSSFCell.CELL_TYPE_NUMERIC: // 数字
                    if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                        Date theDate = cell.getDateCellValue();
                        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd");
                        cellValue = dff.format(theDate);
                    } else {
                        DecimalFormat df = new DecimalFormat("0.00");
                        cellValue = df.format(cell.getNumericCellValue());
                    }
                    break;

                case HSSFCell.CELL_TYPE_STRING: // 字符串
                    cellValue = cell.getStringCellValue().trim();
                    break;

                case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
                    cellValue = cell.getBooleanCellValue() + "";
                    break;

                case HSSFCell.CELL_TYPE_FORMULA: // 公式
                    cellValue = cell.getCellFormula() + "";
                    break;

                case HSSFCell.CELL_TYPE_BLANK: // 空值
                    cellValue = "";
                    break;

                case HSSFCell.CELL_TYPE_ERROR: // 故障
                    cellValue = "非法字符";
                    break;

                default:
                    cellValue = "未知类型";
                    break;
            }

        }
        return cellValue;
    }

    /**
     * 根据实体成员变量的类型得到成员变量的值
     *
     * @param field
     * @param cellValue
     * @return
     * @see [类、类#方法、类#成员]
     */
    private static Object getEntityMemberValue(Field field, String cellValue) throws ParseException {
        Object realValue = "";
        String type = field.getType().getName();
        switch (type) {
            case "char":
            case "java.lang.Character":
            case "java.lang.String":
                realValue = cellValue;
                break;
            case "java.util.Date":
                realValue = ValidateUtils.isBlank(cellValue) ? null : ExcelUtils.DateUtil.strToDate(cellValue, ExcelUtils.DateUtil.YYYY_MM_DD);
                break;
            case "java.lang.Integer":
                realValue = ValidateUtils.isBlank(cellValue) ? null : Integer.valueOf(cellValue);
                break;
            case "int":
            case "float":
            case "double":
            case "java.lang.Double":
            case "java.lang.Float":
            case "java.lang.Long":
            case "java.lang.Short":
            case "java.math.BigDecimal":
                realValue = ValidateUtils.isBlank(cellValue) ? null : new BigDecimal(cellValue);
                break;
            default:
                break;
        }
        return realValue;
    }

    /**
     * 根据路径或文件名选择Excel版本
     *
     * @param filePathOrName
     * @param in
     * @return
     * @throws IOException
     * @see [类、类#方法、类#成员]
     */
    public static Workbook chooseWorkbook(String filePathOrName, InputStream in)
            throws IOException {
        /** 根据版本选择创建Workbook的方式 */
        Workbook wb = null;
        boolean isExcel2003 = ExcelUtils.ExcelVersionUtil.isExcel2003(filePathOrName);
        if (isExcel2003) {
            wb = new HSSFWorkbook(in);
        } else {
            wb = new XSSFWorkbook(in);
        }
        return wb;
    }

    static class ExcelVersionUtil {
        private ExcelVersionUtil() {
        }

        /**
         * 是否是2003的excel，返回true是2003
         *
         * @param filePath
         * @return
         * @see [类、类#方法、类#成员]
         */
        public static boolean isExcel2003(String filePath) {
            return filePath.matches("^.+\\.(?i)(xls)$");

        }

        /**
         * 是否是2007的excel，返回true是2007
         *
         * @param filePath
         * @return
         * @see [类、类#方法、类#成员]
         */
        public static boolean isExcel2007(String filePath) {
            return filePath.matches("^.+\\.(?i)(xlsx)$");

        }
    }

    public static class DateUtil {
        private DateUtil() {
        }

        // ======================日期格式化常量=====================//

        public static final String YYYY_MM_DDHHMMSS = "yyyy-MM-dd HH:mm:ss";

        public static final String YYYY_MM_DD = "yyyy-MM-dd";

        public static final String YYYY_MM = "yyyy-MM";

        public static final String YYYY = "yyyy";

        public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

        public static final String YYYYMMDD = "yyyyMMdd";

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
        public static Date strToDate(String s, String pattern) throws ParseException {
            if (s == null) {
                return null;
            }
            Date date = null;
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(s);
        }
    }
}
