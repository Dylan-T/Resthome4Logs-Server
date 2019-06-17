package nz.ac.vuw.swen301.assignment3.server;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sun.javafx.util.Logging;
import org.apache.http.HttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.util.EntityUtils;
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

    public static List<LogEvent> logs = new ArrayList<LogEvent>();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //Retrieves a log
        response.setContentType("application/json");
        if(request.getParameter("limit") == null || request.getParameter("level") == null){
            response.setStatus(400);
        }else {
            List<JsonObject> responseLogs = new ArrayList<JsonObject>();
            int limit = Integer.parseInt(request.getParameter("limit"));
            String level = request.getParameter("level");
            // get logs in order of timestamp
            int counter = 0;
            for (LogEvent log : logs) {
                if (Priority.toPriority(level.toUpperCase()).toInt() >= Priority.toPriority(level.toUpperCase()).toInt()) {
                    responseLogs.add(log.toJson());
                    counter++;
                }
                if (counter == limit) break; //If limit is reached stop
            }
            PrintWriter out = response.getWriter();

            Gson gson = new Gson();
            JsonElement element = gson.toJsonTree(responseLogs, new TypeToken<List<JsonObject>>() {}.getType());

            if (! element.isJsonArray()) {

            }

            JsonArray jsonArray = element.getAsJsonArray();

            out.print(jsonArray);
            out.close();
            response.setStatus(200);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response){
        // Stores a log
        response.setContentType("application/json");
        try {
            HttpEntity entity = new InputStreamEntity(request.getInputStream(), request.getContentLength());
            String jsonLogs = EntityUtils.toString(entity);

            if(jsonLogs.equals("")){
                response.setStatus(400);
                return;
            }
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonArray arrayFromString = jsonParser.parse(jsonLogs).getAsJsonArray();

            for(JsonElement json: arrayFromString){
                logs.add(gson.fromJson(json, LogEvent.class));
            }

            response.setStatus(201);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
