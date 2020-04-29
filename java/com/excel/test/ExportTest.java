package com.lyt;

import com.lyt.excel.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: excel导出-测试
 * @author: lyt
 * @create: 2020-04-29 15:48
 **/
@SpringBootTest
public class ExportTest {
    @Test
    public void export() throws IOException, IllegalAccessException {
        List<User> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(User.builder().account("123").name("张三").age(20).build());
        }
        ExcelUtils.exportExcelFromTemplate("template/userExport.xls", System.currentTimeMillis() + ".xls", 2, 1, list);
    }
}
