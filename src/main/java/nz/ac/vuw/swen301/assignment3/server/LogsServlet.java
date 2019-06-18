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
import java.util.*;

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
            for (int i = logs.size()-1; i >= 0; i--) {
                //add only if level is right
                if (Level.toLevel(logs.get(i).getLevel()).toInt() >= Level.toLevel(level).toInt()) {
                    responseLogs.add(logs.get(i).toJson());
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

        try {
            request.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");

            // Read Entity
            HttpEntity entity = new InputStreamEntity(request.getInputStream(), request.getContentLength());
            String jsonLogs = EntityUtils.toString(entity);
            System.out.println("TEST");
            System.out.println(jsonLogs);

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
                LogEvent le = gson.fromJson(json.getAsString(), LogEvent.class);
                String id = le.getId();
                if(ids.contains(id)){
                    response.setStatus(409);
                    return;
                }
                ids.add(id);

                System.out.println(json.getAsString());
                logs.add(le);
            }

            response.setStatus(201);
        } catch (Exception e) {
            response.setStatus(400);
            e.printStackTrace();
        }
    }

}
