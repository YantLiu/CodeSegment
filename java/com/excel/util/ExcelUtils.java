package com.lyt.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: hhl lyt
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

    /**
     * 读取列表数据
     * <按顺序放入带有注解的实体成员变量中>
     *
     * @param wb       工作簿
     * @param t        实体
     * @param beginRow 开始行数
     * @param cutRow   末尾不解析的行数
     * @param beginCol 数据开始列
     * @return List<T> 实体列表
     * @throws Exception
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unchecked")
    public static <T> Map readData(Workbook wb, T t, int beginRow, int cutRow, int beginCol) throws IllegalAccessException, InstantiationException {
        //返回消息存储对象
        Map map = new HashMap<>();
        //解析结果对象列表
        List<T> tList = new ArrayList<>();
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
        int totalRows = sheet.getLastRowNum() + 1;
        /** Excel列数 */
        int totalCells = 0;
        if (totalRows < 1) {
            return map;
        } else if (sheet.getRow(beginRow - 2) != null) {
            totalCells = sheet.getRow(beginRow - 2).getLastCellNum();
            if (totalCells > fields.length + beginCol - 1) {
                totalCells = fields.length + beginCol - 1;
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

            for (int colIndex = beginCol - 1; colIndex < totalCells; colIndex++) {
                //取cell的值
                Cell cell = row.getCell(colIndex);
                String cellValue = getCellValue(cell);
                try {
                    // 成员变量的值
                    int fieldIndex = colIndex - beginCol + 1;
                    Object entityMemberValue = getEntityMemberValue(fields[fieldIndex], cellValue);
                    //注入cell的值
                    String fieldName = fields[fieldIndex].getName();
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
            switch (cell.getCellTypeEnum()) {
                case NUMERIC: // 数字
                    if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                        Date theDate = cell.getDateCellValue();
                        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd");
                        cellValue = dff.format(theDate);
                    } else {
                        DecimalFormat df = new DecimalFormat("0.00");
                        cellValue = df.format(cell.getNumericCellValue());
                    }
                    if (cellValue.lastIndexOf(".00") == cellValue.length() - 3) {
                        cellValue = cellValue.substring(0, cellValue.length() - 3);
                    }
                    break;
                case STRING: // 字符串
                    cellValue = cell.getStringCellValue().trim();
                    break;

                case BOOLEAN: // Boolean
                    cellValue = cell.getBooleanCellValue() + "";
                    break;

                case FORMULA: // 公式
                    cellValue = cell.getCellFormula() + "";
                    break;

                case BLANK: // 空值
                    cellValue = "";
                    break;

                case ERROR: // 故障
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
     * 创建单元格表头样式
     *
     * @param workbook 工作薄
     */
    private static CellStyle createCellHeadStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置边框样式
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        //设置对齐样式
        style.setAlignment(HorizontalAlignment.CENTER);
        // 生成字体
        Font font = workbook.createFont();
        // 表头样式
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        // 把字体应用到当前的样式
        style.setFont(font);
        return style;
    }

    /**
     * 创建单元格正文样式
     *
     * @param workbook 工作薄
     */
    private static CellStyle createCellContentStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置边框样式
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        //设置对齐样式
        style.setAlignment(HorizontalAlignment.CENTER);
        // 生成字体
        Font font = workbook.createFont();
        // 正文样式
        style.setFillPattern(FillPatternType.NO_FILL);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        font.setBold(false);
        // 把字体应用到当前的样式
        style.setFont(font);
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,#0"));//数据格式只显示整数
        return style;
    }

    /**
     * 单元格样式(Double)列表
     */
    private static CellStyle createCellContent4DoubleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        // 设置边框样式
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        //设置对齐样式
        style.setAlignment(HorizontalAlignment.CENTER);
        // 生成字体
        Font font = workbook.createFont();
        // 正文样式
        style.setFillPattern(FillPatternType.NO_FILL);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        font.setBold(false);
        // 把字体应用到当前的样式
        style.setFont(font);
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));//保留两位小数点
        return style;
    }

    /**
     * 单元格样式列表
     */
    private static Map<String, CellStyle> setStyleMap(Workbook workbook) {
        Map<String, CellStyle> styleMap = new LinkedHashMap<>();
        styleMap.put("head", createCellHeadStyle(workbook));
        styleMap.put("content", createCellContentStyle(workbook));
        styleMap.put("double", createCellContent4DoubleStyle(workbook));
        return styleMap;
    }

    /*
     * cell
     * field 字段
     * t 实体对象
     */
    private static <T> void setCellValue(Map<String, CellStyle> styleMap, Cell cell, Field field, T t) throws IllegalAccessException {
        if (field == null) {
            return;
        }
        field.setAccessible(true);
        Object object = field.get(t);
        if (object == null) {
            return;
        }

        String type = field.getType().getName();
        String value = object.toString();
        switch (type) {
            case "char":
            case "java.lang.Character":
            case "java.lang.String":
                cell.setCellStyle(styleMap.get("content"));
                cell.setCellValue(object.toString());
                break;
            case "java.util.Date":
                cell.setCellStyle(styleMap.get("content"));
                value = new SimpleDateFormat(DateUtil.YYYY_MM_DD).format(object);
                break;
            case "short":
            case "long":
            case "int":
            case "java.lang.Short":
            case "java.lang.Long":
            case "java.lang.Integer":
                cell.setCellStyle(styleMap.get("content"));
                break;
            case "java.lang.Float":
            case "java.lang.Double":
            case "float":
            case "double":
                cell.setCellStyle(styleMap.get("double"));
                break;
            case "java.math.BigDecimal":
                cell.setCellStyle(styleMap.get("double"));
                BigDecimal v = (BigDecimal) object;
                value = v.setScale(2).toString();
                break;
            default:
                break;
        }
        cell.setCellValue(value);
    }

    /**
     * 根据实体成员变量的类型得到成员变量的值
     *
     * @param field
     * @param cellValue
     * @return
     * @see [类、类#方法、类#成员]
     */
    private static Object getEntityMemberValue(Field field, String cellValue) {
        Object realValue = "";
        String type = field.getType().getName();
        switch (type) {
            case "char":
            case "java.lang.Character":
            case "java.lang.String":
                realValue = cellValue;
                break;
            case "java.util.Date":
                realValue = ValidateUtils.isBlank(cellValue) ? null : DateUtil.strToDate(cellValue, DateUtil.YYYY_MM_DD);
                break;
            case "int":
            case "java.lang.Integer":
                realValue = ValidateUtils.isBlank(cellValue) ? null : Integer.valueOf(cellValue);
                break;
            case "short":
            case "java.lang.Short":
                realValue = ValidateUtils.isBlank(cellValue) ? null : Short.valueOf(cellValue);
                break;
            case "long":
            case "java.lang.Long":
                realValue = ValidateUtils.isBlank(cellValue) ? null : Long.valueOf(cellValue);
                break;
            case "float":
            case "java.lang.Float":
                realValue = ValidateUtils.isBlank(cellValue) ? null : Float.valueOf(cellValue);
                break;
            case "double":
            case "java.lang.Double":
                realValue = ValidateUtils.isBlank(cellValue) ? null : Double.valueOf(cellValue);
                break;
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
    public static Workbook chooseWorkbook(String filePathOrName, InputStream in) throws IOException {
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


    /**
     * @param : [templateFile 模板文件, exportFileName 导出文件名, titleRowIndex 表头所在行, titleColIndex 起始列, list 数据列表]
     * @return : java.io.File
     * @throws :
     * @Description : 按模板导出excel
     * @author : liuyanting
     * @date : 2019/9/2 18:37
     */
    public static <T> File exportExcel(File templateFile, String exportFileName, int titleRowIndex, int titleColIndex, List<T> list) throws IOException, IllegalAccessException {
        //复制模板文件, 在副本上操作
        String tempPath = AppConstants.getTemporaryLocation() + "exportTemporary";
        File dir = new File(tempPath);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }
        File targetFile = new File(tempPath + File.separator + exportFileName);
        //targetFile.createNewFile();
        log.debug("exportExcel>>path>>{}", targetFile.getAbsolutePath());
        Files.copy(templateFile.toPath(), targetFile.toPath());

        if (ValidateUtils.isCollectionEmpty(list)) {
            return targetFile;
        }

        //取表头
        Workbook wb = ExcelUtils.chooseWorkbook(targetFile.getName(), new FileInputStream(targetFile));
        Sheet sheet = wb.getSheetAt(0);
        Row titleRow = sheet.getRow(titleRowIndex - 1);
        int totalCells = titleRow.getPhysicalNumberOfCells();
        //取表头字段列表
        List<Field> fieldList = new ArrayList<>(totalCells - titleColIndex + 1);
        for (int i = titleColIndex - 1; i < totalCells; i++) {
            String str = getCellValue(titleRow.getCell(i));
            if (ValidateUtils.isStringEmpty(str)) {
                break;
            }
            try {
                Field field = list.get(0).getClass().getDeclaredField(str);
                fieldList.add(field);
            } catch (NoSuchFieldException e) {
                list.add(null);
            }
        }

        //反射填充数据
        Map<String, CellStyle> styleMap = setStyleMap(wb);
        for (int i = 0; i < list.size(); i++) {
            Row row = sheet.createRow(titleRowIndex + i);
            for (int j = 0; j < fieldList.size(); j++) {
                Cell cell = row.createCell(titleColIndex + j - 1);
                setCellValue(styleMap, cell, fieldList.get(j), list.get(i));
            }
        }
        //删除标题行
        sheet.shiftRows(titleRowIndex, titleRowIndex + list.size(), -1);

        try (OutputStream ops = new FileOutputStream(targetFile)) {
            wb.write(ops);
            ops.flush();
        }

        log.info("生成文件>>{}", targetFile.getAbsolutePath());
        return targetFile;
    }

    /**
     * @param : [templatePath, outFileName, titleRowIndex, titleColIndex, dataList]
     * @return : java.io.File
     * @throws :
     * @description : 按模板生成excel
     * @author : liuyanting
     * @date : 2020/2/28 15:18
     */
    public static <T> File exportExcelFromTemplate(String templatePath, String outFileName, int titleRowIndex, int titleColIndex, List<T> dataList) throws IOException, IllegalAccessException {
        //截取文件名及文件后缀
        int fileNameIndex = templatePath.indexOf('/') != -1 ? templatePath.lastIndexOf('/') + 1 : 0;
        File templateFile = new File(AppConstants.getTemporaryLocation() + "exportTemplate" + File.separator + templatePath.substring(fileNameIndex));
        try {
            FileUtil.getResourceFile(templatePath, templateFile);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("获取导出模板失败");
        }
        File outFile = ExcelUtils.exportExcel(templateFile, outFileName, titleRowIndex, titleColIndex, dataList);
        return outFile;
    }
}