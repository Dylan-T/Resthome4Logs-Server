package nz.ac.vuw.swen301.assignment3.server;

import javafx.scene.layout.Priority;
import javafx.util.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Level;

public class StatsServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");

        List<LogEvent> logs = LogsServlet.logs;
        Map<String, int[]> occurrences = new HashMap<>();

        Workbook wb = new HSSFWorkbook();
        //Workbook wb = new XSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
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
        }

        //Initialise row headers
        sheet.createRow(0); // Date row
        sheet.createRow(1).createCell(0).setCellValue("FATAL"); // Fatal
        sheet.createRow(2).createCell(0).setCellValue("ERROR"); // Error
        sheet.createRow(3).createCell(0).setCellValue("WARN"); // Warn
        sheet.createRow(4).createCell(0).setCellValue("INFO"); // Info
        sheet.createRow(5).createCell(0).setCellValue("DEBUG"); // Debug
        sheet.createRow(6).createCell(0).setCellValue("TRACE"); // Trace

        int r = 6;
        int c = 1;

        //Add data to table
        for(String date : occurrences.keySet()){
            //add column header
            sheet.getRow(0).createCell(c).setCellValue(date);

            // add columns counts
            int[] levels = occurrences.get(date);
            for(int levelCount: levels){
                sheet.getRow(r).createCell(c).setCellValue(levelCount);
                r--;
            }

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
        System.out.print(file.toPath());



        try (OutputStream out = response.getOutputStream()) {
            Path path = file.toPath();
            Files.copy(path, out);
            out.flush();
        } catch (IOException e) {
            // handle exception
            e.printStackTrace();
        }

//        BufferedInputStream input = null;
//        ServletOutputStream output = null;
//
//        try {
//            // Open streams.
//            input = new BufferedInputStream(new FileInputStream(file), 10240);
//            output = response.getOutputStream();
//
//            // Write file contents to response.
//            byte[] buffer = new byte[10240];
//            int length;
//            while ((length = input.read(buffer)) > 0) {
//                output.write(buffer, 0, length);
//            }
//        } finally {
//            // Gently close streams.
////            input.close();
////            output.close();
//            file.delete();
//        }
        file.delete();
        response.setStatus(200);
    }
}
