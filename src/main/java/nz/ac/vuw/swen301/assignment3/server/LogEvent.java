package nz.ac.vuw.swen301.assignment3.server;

import com.google.gson.JsonObject;
import org.apache.log4j.Priority;

import java.text.DateFormat;
import java.util.logging.Level;

public class LogEvent {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public String getLogger() {
        return logger;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    public Priority getLevel() {
        return level;
    }

    public void setLevel(Priority level) {
        this.level = level;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }

    private String id;
    private String message;
    private String timestamp;
    private String thread;
    private String logger;
    private Priority level;
    private String errorDetails;

    public LogEvent(String id, String message, String timestamp, String thread, String logger, String level, String errorDetails){
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
        this.thread = thread;
        this.logger = logger;
        this.level = Priority.toPriority(level);
        this.errorDetails = errorDetails;
    }

    public JsonObject toJson(){
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("message", message);
        json.addProperty("timestamp", timestamp);
        json.addProperty("thread", thread);
        json.addProperty("logger", logger);
        json.addProperty("level", level.toString());
        json.addProperty("errorDetails", errorDetails);
        return json;
    }

}
