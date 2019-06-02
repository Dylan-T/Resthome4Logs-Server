package test.nz.ac.vuw.swen301.assignment3.server;

import nz.ac.vuw.swen301.assignment3.server.LogsServlet;
import nz.ac.vuw.swen301.assignment3.server.StatsServlet;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import java.io.IOException;
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
        request.setParameter("level","DEBUG");
        request.addParameter("limit", "5");
        MockHttpServletResponse response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.doGet(request,response);

        String result = response.getContentAsString();
        String[] logs = result.split(" "); //TODO: see what separates logs
        List<String> list = Arrays.stream(logs).collect(Collectors.toList());

        assertTrue(list.get(0).equals("Joshua")); //TODO: Figure out what the logs will be
        assertTrue(list.get(1).equals("Jason"));
        assertTrue(list.get(2).equals("Jasmine"));
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
                "    \"timestamp\": {},\n" +
                "    \"thread\": \"main\",\n" +
                "    \"logger\": \"com.example.Foo\",\n" +
                "    \"level\": \"DEBUG\",\n" +
                "    \"errorDetails\": \"string\"\n" +
                "  }\n" +
                "]";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("LogEvent", logevent);
        MockHttpServletResponse response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.doPost(request,response);

        assertEquals(201,response.getStatus());
    }

    @Test
    public void LOGSPOSTtestValidContentType() throws IOException {
        String logevent = "[\n" +
                "  {\n" +
                "    \"id\": \"d290f1ee-6c54-4b01-90e6-d701748f0851\",\n" +
                "    \"message\": \"application started\",\n" +
                "    \"timestamp\": {},\n" +
                "    \"thread\": \"main\",\n" +
                "    \"logger\": \"com.example.Foo\",\n" +
                "    \"level\": \"DEBUG\",\n" +
                "    \"errorDetails\": \"string\"\n" +
                "  }\n" +
                "]";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("LogEvent", logevent);
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
        String[] logs = result.split(" ");
        List<String> list = Arrays.stream(logs).collect(Collectors.toList());

        assertTrue(list.get(0).equals("Joshua")); //TODO: Figure out what POST returns
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

        assertTrue(list.get(0).equals("Joshua")); //TODO: Figure out what the logs will be
        assertTrue(list.get(1).equals("Jason"));
        assertTrue(list.get(2).equals("Jasmine"));
    }

}
