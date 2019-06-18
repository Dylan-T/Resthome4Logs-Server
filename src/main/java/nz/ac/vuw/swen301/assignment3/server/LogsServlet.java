package nz.ac.vuw.swen301.assignment3.server;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sun.javafx.util.Logging;
import org.apache.http.HttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LogsServlet extends HttpServlet {

    public static List<LogEvent> logs = new ArrayList<LogEvent>();
    public static Set<String> ids = new HashSet<>();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        // Check the parameters are given
        if(request.getParameter("limit") == null || request.getParameter("level") == null){
            response.setStatus(400);
        }else {
            List<JsonObject> responseLogs = new ArrayList<JsonObject>();

            // get params
            int limit = Integer.parseInt(request.getParameter("limit"));
            String level = request.getParameter("level");

            // Iterate backwards to get them newest to oldest
            int counter = 0;
            for (LogEvent log : logs) {
                //add only if level is right
                System.out.println();
                if (Level.toLevel(log.getLevel()).toInt() >= Level.toLevel(level).toInt()) {
                    responseLogs.add(log.toJson());
                    counter++;
                }
                if (counter == limit) break; //Stop once limit is reached
            }
            PrintWriter out = response.getWriter();

            // Convert logs to json array
            Gson gson = new Gson();
            JsonElement element = gson.toJsonTree(responseLogs, new TypeToken<List<JsonObject>>() {}.getType());
            JsonArray jsonArray = element.getAsJsonArray();

            // write to response
            out.print(jsonArray);
            out.close();
            response.setStatus(200);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response){
        // Stores a log
        response.setContentType("application/json");
        try {
            // Read Entity

            HttpEntity entity = new InputStreamEntity(request.getInputStream(), request.getContentLength());
            String jsonLogs = EntityUtils.toString(entity);


            if(jsonLogs.equals("")){
                response.setStatus(400);
                return;
            }

            // Turn String into JsonArray
            Gson gson = new Gson();
            JsonParser jsonParser = new JsonParser();
            JsonArray arrayFromString = jsonParser.parse(jsonLogs).getAsJsonArray();

            // Add the LogEvents to the logs list
            for(JsonElement json: arrayFromString){
                if(!json.isJsonNull()) {

                    String id = json.getAsJsonObject().get("id").toString();
                    if(ids.contains(id)){
                        response.setStatus(409);
                        return;
                    }
                    ids.add(id);
                    logs.add(gson.fromJson(json.toString(), LogEvent.class));
                }
            }

            response.setStatus(201);
        }catch(Exception e) {
            response.setStatus(400);
        }
    }

}
