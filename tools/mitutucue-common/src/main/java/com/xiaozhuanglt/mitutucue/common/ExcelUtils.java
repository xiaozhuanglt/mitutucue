package com.xiaozhuanglt.mitutucue.common;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel工具类
 * 
 * @author lvhonglei
 * @version $Id: ExcelUtils.java, v 0.1 2016年1月22日 下午5:07:36 lvhonglei Exp $
 */
public class ExcelUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtils.class);

    private ExcelUtils() {
    }

    /**
     * 创建隐藏的sheet,并设置选择框信息
     * @param workbook
     * @param hideSelectList
     * @date: 2016年2月25日 下午10:53:08
     */
    private static void createHideSheet(XSSFWorkbook workbook,
                                        List<Map<String, Object>> hideSelectList, String hideName) {
        /**创建隐藏工作表*/
        XSSFSheet hideSheet = workbook.createSheet(hideName);
        for (int i = 0; i < hideSelectList.size(); i++) {
            XSSFRow row = hideSheet.createRow(i);
            Map<String, Object> map = hideSelectList.get(i);
            String[] hideArray = (String[]) map.get("nameValue");
            if (null != hideArray) {
                for (int j = 0; j < hideArray.length; j++) {
                    XSSFCell cell = row.createCell(j);
                    cell.setCellValue(hideArray[j]);
                }
            }
        }
    }

    /**
     * 创建基本excel,并创建标题行
     * @param workbook
     * @param sheetName
     * @param headers
     * @date: 2016年2月25日 下午10:43:54
     */
    public static XSSFSheet  createExcel(XSSFWorkbook workbook, String sheetName,
                                        List<Object> headers) {
        /**创建工作表*/
        XSSFSheet sheet = workbook.createSheet(sheetName);
        /**创建行*/
        XSSFRow row = sheet.createRow(0);
        /**创建单元格，并赋值*/
        for (int i = 0; i < headers.size(); i++) {
            Cell cell = row.createCell(i);
            JSONObject headerJson = (JSONObject) JSONObject.toJSON(headers.get(i));
            String header = headerJson.getString("key");
            Boolean isRequired = headerJson.getBoolean("value");
            XSSFRichTextString text = new XSSFRichTextString(header);
            cell.setCellValue(text);
            if(isRequired){
                setExelComment(sheet,cell);
            }
            cell.setCellStyle(createCellStyle(workbook, true));
            /*if(headers.get(i).indexOf("_") > -1){
                //插入单元格内容
                cell.setCellValue(new XSSFRichTextString(headers.get(i).split("_")[0]));
                setExelComment(sheet,cell);
            }*/
        }
        return sheet;
    }

    private static void setExelComment(XSSFSheet sheet, Cell cell) {
        ////创建绘图对象
        XSSFDrawing p=sheet.createDrawingPatriarch();
        //获取批注对象
        //(int dx1, int dy1, int dx2, int dy2, short col1, int row1, short col2, int row2)
        //前四个参数是坐标点,后四个参数是编辑和显示批注时的大小.
        XSSFComment comment=p.createCellComment(new XSSFClientAnchor(0,0,0,0,(short)3,3,(short)5,6));
        //输入批注信息
        comment.setString(new XSSFRichTextString("必填!"));
        //添加作者,选中B5单元格,看状态栏
        comment.setAuthor("user");
        //将批注添加到单元格对象中
        cell.setCellComment(comment);
    }

    /**
     * 设置选择框信息
     * @param workbook 
     * @param headers
     * @param hideSelectList
     * @param hideSheetName
     * @date: 2016年2月29日 下午1:49:05
     * @user lvhonglei
     */
    private static void creatExcelNameList(Workbook workbook, final String[] headers,
                                           List<Map<String, Object>> hideSelectList,
                                           String hideSheetName) {
        if (LOGGER.isDebugEnabled() && null != headers) {
            LOGGER.debug(headers.toString());
        }
        for (int i = 0; i < hideSelectList.size(); i++) {
            Map<String, Object> hideMap = hideSelectList.get(i);
            String nameCode = (String) hideMap.get("nameKey");
            String[] nameValues = (String[]) hideMap.get("nameValue");
            if (null != nameCode && !"".equals(nameCode)) {
                Name name = workbook.createName();
                name.setNameName(nameCode);
                String formula = hideSheetName + "!" + creatExcelNameList(i + 1, nameValues.length);
                System.out.println(nameCode + " ==  " + formula);
                name.setRefersToFormula(formula);
            }
        }

    }

    /**选取选择框可选值的长度*/
    private static String creatExcelNameList(int order, int size) {
        char start = 'A';
        return "$" + start + "$" + order + ":$" + getExcelColumnLabel(size) + "$" + order;
    }

    /**计算列所在的字母*/
    public static String getExcelColumnLabel(int num) {
        String temp = "";
        double i = Math.floor(Math.log(25.0 * (num) / 26.0 + 1) / Math.log(26)) + 1;
        if (i > 1) {
            double sub = num - 26 * (Math.pow(26, i - 1) - 1) / 25;
            for (double j = i; j > 0; j--) {
                temp = temp + (char) (sub / Math.pow(26, j - 1) + 65);
                sub = sub % Math.pow(26, j - 1);
            }
        } else {
            temp = temp + (char) (num + 65);
        }
        return temp;
    }

    /**
     * 验证选择框内容
     * 1.获取非隐藏的sheet
     * 2.设定验证数据
     * @param workbook
     * @param headers
     * @param hideSelectList
     * @date: 2016年2月26日 上午8:39:28
     */
    private static void setDataValidation(XSSFWorkbook workbook, final String[] headers,
                                          List<Map<String, Object>> hideSelectList) {
        for (int j = 0; j < hideSelectList.size(); j++) {
            Map<String, Object> result = hideSelectList.get(j);
            /**选择框对应的中文名称*/
            String nameString = (String) result.get("nameString");
            /**选择框的英文名称*/
            String name = (String) result.get("nameKey");
            /**
             * 跟标题行比较，确定列数
             */
            for (int k = 0; k < headers.length; k++) {
                String header = headers[k];
                if (nameString.equals(header)) {
                    /**获取工作表*/
                    XSSFSheet sheet = workbook.getSheetAt(0);
                    XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
                    XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
                        .createFormulaListConstraint(name);
                    CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, k, k);
                    XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(
                        dvConstraint, addressList);
                    validation.setSuppressDropDownArrow(true);
                    validation.setShowErrorBox(true);
                    sheet.addValidationData(validation);
                }
            }
        }
    }

    /**
     * 生成下拉选项的excel模版
     * 1.创建workbook
     * 2.创建显示的excel
     * 3.创建隐藏的excel
     * 4.设置验证数据
     * 
     * <ul>
     *  <li>sheetName</li>
     *  <li>String 类型，工作表名称</li>
     * </ul>
     * 
     * <ul>
     *  <li>headers</li>
     *  <li>String[] 数据，存在标题列的中文名字</li>
     * </ul>
     * 
     * <ul>
     *  <li>hideSelectList</li>
     *  <li>map.get("nameKey"),nameKey,String 类型，存储英文类型的字段名字</li>
     *  <li>map.get("nameString"),nameString,String 类型，存在中文字段名字，后续要跟headers判断位置，并确定验证数据列</li>
     *  <li>map.get("nameValue"),nameValue,String[] 数组，存储下拉框的选择值</li>
     * </ul>
     * 
     * 
     */
    //    public static void createDownloadTemplate(XSSFWorkbook workbook, final String sheetName,
    //                                              final String hideSheetName, final String[] headers,
    //                                              List<Map<String, Object>> hideSelectList) {
    //        Map<String, Object> resultMap = new HashMap<String, Object>();
    //        if (null == sheetName || "".equals(sheetName.trim())) {
    //            LOGGER.warn("表格标题不能为空");
    //            throw new RuntimeException("the sheetName must be not null");
    //        }
    //        if (null == headers || headers.length == 0) {
    //            LOGGER.warn("表格标题行不能为空");
    //            throw new RuntimeException("the headers must be not null");
    //        }
    //        if (null == hideSelectList || hideSelectList.isEmpty()) {
    //            LOGGER.warn("导出数据不能为空");
    //            throw new RuntimeException("the hideSelectList must be not null");
    //        }
    //        createExcel(workbook, sheetName, headers);
    //        createHideSheet(workbook, hideSelectList, hideSheetName);
    //        creatExcelNameList(workbook, headers, hideSelectList, hideSheetName);
    //        setDataValidation(workbook, headers, hideSelectList);
    //        resultMap.put("success", true);
    //        resultMap.put("message", "create excel template success");
    //        resultMap.put("extData", workbook);
    //    }

    /**
     * 此方法只试用 动态生成对象的excel生成（具体应用，可以借鉴sku导出）
     * @param workbook
     * @param sheetTitle
     * @param headers
     * @param dataList
     * @return
     * @date: 2016年3月2日 下午4:00:30
     * @user lvhonglei
     */
    public static void  exportCommonExcel(XSSFWorkbook workbook, final String sheetTitle,
                                         final List<Object> headers, final List<String[]> dataList,
                                         Integer extType) {
        XSSFSheet sheet = createExcel(workbook, sheetTitle, headers);
        /**遍历数据*/
        for (int i = 0; i < dataList.size(); i++) {
            XSSFRow row = sheet.createRow(i + 1);
            String[] values = dataList.get(i);
            for (int j = 0; j < headers.size(); j++) {
                XSSFCell cell = row.createCell(j);
                Object value = values[j + extType];
                if (value instanceof Boolean) {
                    cell.setCellValue(new Boolean(value.toString()));
                } else if (value instanceof Integer) {
                    cell.setCellValue(value.toString());
                } else if (value instanceof String) {
                    cell.setCellValue(value.toString());
                } else if (value instanceof Date) {
                    long daDate = ((Date) value).getTime();
                    cell.setCellValue(new Date(daDate));
                } else if (value instanceof Double) {
                    cell.setCellValue(Double.parseDouble(value.toString()));
                }
            }
        }
    }

    /**
     * 生成一个工作薄
     * @return
     * @date: 2016年3月17日 下午2:58:31
     * @user lvhonglei
     */
    public static XSSFWorkbook createWorkbook() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        return workbook;
    }

    /**
     * 生成一个sheet页
     * @param workbook 工作薄
     * @param sheetName sheet名字
     * @return
     * @date: 2016年3月17日 下午3:00:09
     * @user lvhonglei
     */
    public static XSSFSheet createSheet(XSSFWorkbook workbook, String sheetName) {
        XSSFSheet sheet = workbook.createSheet(sheetName);
        return sheet;
    }

    /**
     * 生成标题行
     * @param sheet sheet页
     * @param headers 标题数据
     * @param workbook 工作薄
     * @return
     * @date: 2016年3月17日 下午3:05:12
     * @user lvhonglei
     */
    public static void createTitleRow(XSSFWorkbook workbook, XSSFSheet sheet, String[] headers) {
        XSSFRow row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellStyle(createCellStyle(workbook, true));
            XSSFRichTextString text = new XSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }
    }

    public static void createRow(XSSFWorkbook wb, XSSFSheet sheet, String[] headers,
                                 Integer rowNumber) {
        XSSFRow row = sheet.createRow(rowNumber);
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellStyle(createCellStyle(wb, true));
            XSSFRichTextString text = new XSSFRichTextString(headers[i]);
            cell.setCellValue(text);
        }

    }

    /**
     * 生成cellstyle，并设置字体
     * @param workbook 工作薄
     * @param isHeader 是否是标题
     * @return
     * @date: 2016年3月17日 下午3:09:44
     * @user lvhonglei
     */
    public static XSSFCellStyle createCellStyle(XSSFWorkbook workbook, boolean isHeader) {
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        if (isHeader) {
            cellStyle.setFillForegroundColor(HSSFColor.BRIGHT_GREEN.index);
        } else {
            cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
        }
        cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        createFont(workbook, cellStyle);
        return cellStyle;
    }

    /**
     * 生成 字体
     * @param workbook
     * @param cellStyle
     * @return
     * @date: 2016年3月17日 下午3:15:12
     * @user lvhonglei
     */
    public static XSSFFont createFont(XSSFWorkbook workbook, XSSFCellStyle cellStyle) {
        if (LOGGER.isDebugEnabled() && null != cellStyle) {
            LOGGER.debug(cellStyle.toString());
        }
        XSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.VIOLET.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        return font;
    }

    /**
     * Java反射，生成工作博
     * 
     * 1.生成工作薄
     * 2.生成表格
     * 3.生成并设置样式
     * 4.生成标题行
     * 5.遍历数据
     * 6.生成row
     * 6.Java反射 获取字段
     * 7.根据传入字段取值
     * 8.根据传入的字段数据，创建单元格
     * 9.取字段的值
     * 10.判断字段的类型，并强转
     * 11.给单元格赋值
     * 12.返回book
     * 
     * @param <T>
     * @param sheetTitle 表格标题
     * @param headers 第一行标题
     * @param dataList 导出数据集合
     * @param params 导出字段
     * @return
     * @date: 2016年1月25日 下午4:38:14
     */
    public static <T> Map<String, Object> createExcel(XSSFWorkbook workbook,
                                                      final String sheetTitle,
                                                      final String[] headers,
                                                      final List<T> dataList, final String[] params) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (null == sheetTitle || "".equals(sheetTitle.trim())) {
            LOGGER.warn("表格标题不能为空");
            resultMap.put("success", false);
            resultMap.put("message", "表格标题不能为空");
            resultMap.put("extData", null);
            return resultMap;
        }
        if (null == headers || headers.length == 0) {
            LOGGER.warn("表格标题行不能为空");
            resultMap.put("success", false);
            resultMap.put("message", "表格标题行不能为空");
            resultMap.put("extData", null);
            return resultMap;
        }
        if (null == dataList || dataList.isEmpty()) {
            LOGGER.warn("导出数据不能为空");
            resultMap.put("success", false);
            resultMap.put("message", "导出数据不能为空");
            resultMap.put("extData", null);
            return resultMap;
        }
        if (null == workbook) {
            workbook = createWorkbook();
        }
        XSSFSheet sheet = createSheet(workbook, sheetTitle);
        XSSFCellStyle style = createCellStyle(workbook, false);
        createTitleRow(workbook, sheet, headers);
        /**
         * 遍历数据
         */
        for (int i = 0; i < dataList.size(); i++) {
            /**
             * 创建行
             */
            XSSFRow row = sheet.createRow(i + 1);
            T t = dataList.get(i);
            /**
             * 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
             * 
             */
            Field[] fields = t.getClass().getDeclaredFields();
            for (int j = 0; j < fields.length; j++) {
                if (null == params || params.length == 0) {
                    /**
                     * 反射，获取字段
                     */
                    Field field = fields[j];
                    /**
                     * 创建单元格
                     */
                    XSSFCell cell = row.createCell(j);
                    cell.setCellStyle(style);
                    /**
                     * 获取字段名称
                     */
                    String fieldName = field.getName();
                    final String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase()
                                                 + fieldName.substring(1);
                    try {
                        Method getMethod = t.getClass().getMethod(getMethodName);
                        Object value = getMethod.invoke(t);
                        /**
                         * 判断类型，强制转换
                         */
                        if (value instanceof Boolean) {
                            cell.setCellValue(new Boolean(value.toString()));
                        } else if (value instanceof Integer) {
                            cell.setCellValue(value.toString());
                        } else if (value instanceof String) {
                            cell.setCellValue(value.toString());
                        } else if (value instanceof Date) {
                            long daDate = ((Date) value).getTime();
                            cell.setCellValue(new Date(daDate));
                        } else if (value instanceof Double) {
                            cell.setCellValue(Double.parseDouble(value.toString()));
                        } else if (value instanceof Long) {
                            cell.setCellValue(String.valueOf(value));
                        }
                    } catch (NoSuchMethodException e) {
                        LOGGER.warn("Java反射错误", e);
                        resultMap.put("success", false);
                        resultMap.put("message", "Java反射错误");
                        resultMap.put("extData", null);
                    } catch (SecurityException e) {
                        LOGGER.warn("Java反射错误", e);
                        resultMap.put("success", false);
                        resultMap.put("message", "Java反射错误");
                        resultMap.put("extData", null);
                    } catch (IllegalAccessException e) {
                        LOGGER.warn("Java反射错误", e);
                        resultMap.put("success", false);
                        resultMap.put("message", "Java反射错误");
                        resultMap.put("extData", null);
                    } catch (IllegalArgumentException e) {
                        LOGGER.warn("Java反射错误", e);
                        resultMap.put("success", false);
                        resultMap.put("message", "Java反射错误");
                        resultMap.put("extData", null);
                    } catch (InvocationTargetException e) {
                        LOGGER.warn("Java反射错误", e);
                        resultMap.put("success", false);
                        resultMap.put("message", "Java反射错误");
                        resultMap.put("extData", null);
                    }
                } else {
                    for (int k = 0; k < params.length; k++) {
                        /**
                         * 反射，获取字段
                         */
                        Field field = fields[j];
                        /**
                         * 获取字段名称
                         */
                        String fieldName = field.getName();
                        String paramName = params[k];
                        if (null == paramName || "".equals(paramName.trim())) {
                            resultMap.put("success", false);
                            resultMap.put("message", "导出字段为空，程序bug");
                            resultMap.put("extData", null);
                            break;
                        }
                        if (fieldName.equalsIgnoreCase(paramName)) {
                            /**
                             * 创建单元格
                             */
                            XSSFCell cell = row.createCell(k);
                            cell.setCellStyle(style);
                            /**
                             * 获取值
                             */
                            final String getMethodName = "get"
                                                         + fieldName.substring(0, 1).toUpperCase()
                                                         + fieldName.substring(1);
                            try {
                                Method getMethod = t.getClass().getMethod(getMethodName);
                                Object value = getMethod.invoke(t);

                                /**
                                 * 判断类型，强制转换
                                 */
                                if (value instanceof Boolean) {
                                    cell.setCellValue(new Boolean(value.toString()));
                                } else if (value instanceof Integer) {
                                    cell.setCellValue(value.toString());
                                } else if (value instanceof String) {
                                    cell.setCellValue(value.toString());
                                } else if (value instanceof Date) {
                                    long daDate = ((Date) value).getTime();
                                    cell.setCellValue(new Date(daDate));
                                } else if (value instanceof Double) {
                                    cell.setCellValue(Double.parseDouble(value.toString()));
                                } else if (value instanceof Long) {
                                    cell.setCellValue(String.valueOf(value));
                                }
                            } catch (NoSuchMethodException e) {
                                LOGGER.warn("Java反射错误", e);
                            } catch (SecurityException e) {
                                LOGGER.warn("Java反射错误", e);
                            } catch (IllegalAccessException e) {
                                LOGGER.warn("Java反射错误", e);
                            } catch (IllegalArgumentException e) {
                                LOGGER.warn("Java反射错误", e);
                            } catch (InvocationTargetException e) {
                                LOGGER.warn("Java反射错误", e);
                            }
                        }
                    }
                }
            }
        }
        resultMap.put("success", true);
        resultMap.put("message", "生成book成功");
        resultMap.put("extData", workbook);
        return resultMap;
    }

    public static void main(String[] args) throws IOException {
        //创建工作簿对象
        XSSFWorkbook wb=new XSSFWorkbook();
        //创建工作表对象
        XSSFSheet sheet=wb.createSheet("我的工作表");
        //创建单元格对象,批注插入到4行,1列,B5单元格
        XSSFCell cell=sheet.createRow(4).createCell(1);
        //插入单元格内容
        cell.setCellValue(new XSSFRichTextString("批注"));
        setExelComment(sheet,cell);
        //创建输出流
        FileOutputStream out=new FileOutputStream("D:/data/html/writerPostil111.xlsx");

        wb.write(out);
        //关闭流对象
        out.close();
    }

}
