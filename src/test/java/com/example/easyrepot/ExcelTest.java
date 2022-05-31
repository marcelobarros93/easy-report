package com.example.easyrepot;

import com.example.easyreport.Excel;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Log4j2
class ExcelTest {

    @Test
    void generate_ShouldCreateFile_WhenIsValidData() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
        List<ReportTest> data = List.of(
                new ReportTest("Marcelo", 29, LocalDate.now().minusYears(29), LocalDateTime.now()),
                new ReportTest("Bruna", 28, null, LocalDateTime.now()));

        String path = "/tmp/people_" + UUID.randomUUID() + ".xlsx";
        Excel excel = Excel.builder()
                .localDatePattern("dd/MM/yyyy")
                .localDateTimePattern("dd/MM/yyyy HH:mm")
                .headerColor(IndexedColors.YELLOW)
                .build();

        try(FileOutputStream outputStream = new FileOutputStream(path)) {
            excel.write(outputStream, data);
        }

        log.info(path);
        Assertions.assertTrue(new File(path).exists());
    }
}
