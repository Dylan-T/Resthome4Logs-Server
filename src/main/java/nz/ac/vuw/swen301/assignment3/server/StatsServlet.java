package nz.ac.vuw.swen301.assignment3.server;

import javafx.util.Pair;
import org.apache.log4j.Level;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");

        List<LogEvent> logs = LogsServlet.logs;
        Map<String, int[]> occurrences = new HashMap<>();
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet("new sheet");

        //count levels per day
        for(LogEvent log: logs){
            //if new day add int[]
            if(occurrences.get(log.getTimestamp()) == null){
                occurrences.put(log.getTimestamp(), new int[6]);
            }
//            FATAL = 50000
//            ERROR = 40000
//            WARN = 30000
//            INFO = 20000
//            DEBUG = 10000
//            TRACE = 5000

            int[] levelCount = occurrences.get(log.getTimestamp());
            int level = Level.toLevel(log.getLevel()).toInt() / 10000;
            levelCount[level]++;


            List<String> loggerNames = new ArrayList<>();
        }



        //Initialise row headers
        sheet.createRow(0); // Date row
        sheet.createRow(1).createCell(0).setCellValue("FATAL"); // Fatal
        sheet.createRow(2).createCell(0).setCellValue("ERROR"); // Error
        sheet.createRow(3).createCell(0).setCellValue("WARN"); // Warn
        sheet.createRow(4).createCell(0).setCellValue("INFO"); // Info
        sheet.createRow(5).createCell(0).setCellValue("DEBUG"); // Debug
        sheet.createRow(6).createCell(0).setCellValue("TRACE"); // Trace
        sheet.createRow(7).createCell(0).setCellValue("==Loggers==");
        sheet.createRow(8).createCell(0).setCellValue("test1");
        sheet.createRow(9).createCell(0).setCellValue("==Threads==");
        sheet.createRow(10).createCell(0).setCellValue("main");

        int r = 6;
        int c = 1;

        //Add data to table
        for(String date : occurrences.keySet()){
            //add column header
            sheet.getRow(0).createCell(c).setCellValue(date);

            // add columns counts
            int[] levels = occurrences.get(date);
            int sum = 0;
            for(int levelCount: levels){
                sheet.getRow(r).createCell(c).setCellValue(levelCount);
                sum += levelCount;
                r--;
            }
            sheet.getRow(8).createCell(c).setCellValue(sum);
            sheet.getRow(10).createCell(c).setCellValue(sum);
            //move to next column
            r = 6;
            c++;
        }

        //write to file
        try (OutputStream fileOut = new FileOutputStream("workbook.xls")) {
            wb.write(fileOut);
        }

        // Decode the file name (might contain spaces and on) and prepare file object.
        File file = new File("workbook.xls");



        try (OutputStream out = response.getOutputStream()) {
            Path path = file.toPath();
            Files.copy(path, out);
            out.flush();
        } catch (IOException e) {
            // handle exception
            e.printStackTrace();
        }

        file.delete();
        response.setStatus(200);
    }
}
