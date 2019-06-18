package test.nz.ac.vuw.swen301.assignment3.server;

import com.google.gson.Gson;
import nz.ac.vuw.swen301.assignment3.server.LogEvent;
import nz.ac.vuw.swen301.assignment3.server.LogsServlet;
import nz.ac.vuw.swen301.assignment3.server.StatsServlet;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WhiteBoxTests {

    //LOGS SERVLET GET TESTS
    @Test
    public void LOGSGETtestInvalidRequestResponseCode1() throws IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        // query parameter missing
        LogsServlet service = new LogsServlet();
        service.doGet(request,response);

        assertEquals(400,response.getStatus());
    }

    @Test
    public void LOGSGETtestInvalidRequestResponseCode2() throws IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("not a valid param name","42");
        MockHttpServletResponse response = new MockHttpServletResponse();
        // wrong query parameter

        LogsServlet service = new LogsServlet();
        service.doGet(request,response);

        assertEquals(400,response.getStatus());
    }

    @Test
    public void LOGSGETtestValidRequestResponseCode() throws IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level","TRACE");
        request.addParameter("limit", "10");
        MockHttpServletResponse response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.doGet(request,response);

        assertEquals(200,response.getStatus());
    }

    @Test
    public void LOGSGETtestValidContentType() throws IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level","DEBUG");
        request.addParameter("limit", "5");
        MockHttpServletResponse response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.doGet(request,response);

        assertTrue(response.getContentType().startsWith("application/json"));
    }

    @Test
    public void LOGSGETtestReturnedValues() throws IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level","ERROR");
        request.addParameter("limit", "5");
        MockHttpServletResponse response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.logs.add(new LogEvent("1","test", "11.11.1111", "thread", "logger", "ERROR", "eDets"));
        service.logs.add(new LogEvent("1","test", "11.11.1111", "thread", "logger", "DEBUG", "eDets"));
        service.logs.add(new LogEvent("1","test", "11.11.1111", "thread", "logger", "DEBUG", "eDets"));
        service.doGet(request,response);

        String result = response.getContentAsString();
        System.out.println(result);
        assertEquals("[{\"id\":\"1\",\"message\":\"test\",\"timestamp\":\"11.11.1111\",\"thread\":\"thread\",\"logger\":\"logger\",\"level\":\"ERROR\",\"errorDetails\":\"eDets\"}]", result);

    }

    //LOGS SERVLET POST TESTS
    @Test
    public void LOGSPOSTtestInvalidRequestResponseCode1() throws IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        // query parameter missing
        LogsServlet service = new LogsServlet();
        service.doPost(request,response);

        assertEquals(400,response.getStatus());
    }

    @Test
    public void LOGSPOSTtestInvalidRequestResponseCode2() throws IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("not a valid param name","42");
        MockHttpServletResponse response = new MockHttpServletResponse();
        // wrong query parameter

        LogsServlet service = new LogsServlet();
        service.doPost(request,response);

        assertEquals(400,response.getStatus());
    }

    @Test
    public void LOGSPOSTtestValidRequestResponseCode() throws IOException {
        String logevent = "[\n" +
                "  {\n" +
                "    \"id\": \"d290f1ee-6c54-4b01-90e6-d701748f0851\",\n" +
                "    \"message\": \"application started\",\n" +
                "    \"timestamp\": \"1234\",\n" +
                "    \"thread\": \"main\",\n" +
                "    \"logger\": \"com.example.Foo\",\n" +
                "    \"level\": \"DEBUG\",\n" +
                "    \"errorDetails\": \"string\"\n" +
                "  }\n" +
                "]";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent(logevent.getBytes());
        MockHttpServletResponse response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.doPost(request,response);

        assertEquals(201,response.getStatus());
    }

    @Test
    public void LOGSPOSTtestValidContentType() throws IOException {
        String logevent = "[" +
                "  {" +
                "    \"id\": \"d290f1ee-6c54-4b01-90e6-d701748f0851\"," +
                "    \"message\": \"application started\"," +
                "    \"timestamp\": 2141241," +
                "    \"thread\": \"main\"," +
                "    \"logger\": \"com.example.Foo\"," +
                "    \"level\": \"DEBUG\"," +
                "    \"errorDetails\": \"string\"" +
                "  }" +
                "]";

        MockHttpServletRequest request = new MockHttpServletRequest();
        //Create entity
        request.setContent(logevent.getBytes());
        MockHttpServletResponse response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.doPost(request,response);

        assertTrue(response.getContentType().startsWith("application/json"));
    }

    @Test
    public void LOGSPOSTtestReturnedValues() throws IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level","DEBUG");
        request.addParameter("limit", "5");
        MockHttpServletResponse response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.doPost(request,response);

        String result = response.getContentAsString();

        assertEquals("items created", result);
    }


    //STATS SERVLET GET TESTS

    @Test
    public void STATSGETtestValidRequestResponseCode() throws IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        StatsServlet service = new StatsServlet();
        service.doGet(request,response);

        assertEquals(200,response.getStatus());
    }

    @Test
    public void STATSGETtestValidContentType() throws IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        StatsServlet service = new StatsServlet();
        service.doGet(request,response);

        assertTrue(response.getContentType().startsWith("application/vnd.ms-excel"));
    }

    @Test
    public void STATSGETtestReturnedValues() throws IOException {



        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level","DEBUG");
        request.addParameter("limit", "5");
        MockHttpServletResponse response = new MockHttpServletResponse();

        StatsServlet service = new StatsServlet();
        service.doGet(request,response);

        String result = response.getContentAsString();
        String[] logs = result.split(" "); //TODO: see what separates logs
        List<String> list = Arrays.stream(logs).collect(Collectors.toList());

        assertTrue(list.get(0).equals(null)); //TODO: Figure out what the logs will be
        assertTrue(list.get(1).equals(null));
        assertTrue(list.get(2).equals(null));
    }

}
