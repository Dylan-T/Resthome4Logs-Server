package nz.ac.vuw.swen301.assignment3.server;

import com.google.gson.Gson;
import com.sun.javafx.util.Logging;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class LogsServlet extends HttpServlet {

    public static List<LoggingEvent> logs = new ArrayList<LoggingEvent>();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //Retrieves a log

        response.setContentType("application/json");
        if(request.getParameter("limit") == null || request.getParameter("level") == null){
            response.setStatus(400);
        }else {
            List<LoggingEvent> responseLogs = new ArrayList<LoggingEvent>();
            int limit = Integer.parseInt(request.getParameter("limit"));
            String level = request.getParameter("level");
            // get logs in order of timestamp
            int counter = 0;
            for (LoggingEvent log : logs) {
                if (log.getLevel().toInt() <= Priority.toPriority(request.getParameter("level")).toInt()) {
                    responseLogs.add(log);
                    counter++;
                }
                if (counter == limit) break; //If limit is reached stop
            }
            PrintWriter out = response.getWriter();
            Gson gson = new Gson();
            out.println(gson.toJson(responseLogs));
            out.close();
            response.setStatus(200);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response){
        // Stores a log
        response.setContentType("application/json");
        if(request.getParameter("LogEvent") == null){
            response.setStatus(400);
            return;
        }

        response.setStatus(201);
        logs.add(new Gson().fromJson(request.getParameter("LogEvent"), LoggingEvent.class));
    }
}
