package test.nz.ac.vuw.swen301.assignment3.server;

import nz.ac.vuw.swen301.assignment3.server.LogEvent;
import nz.ac.vuw.swen301.assignment3.server.LogsServlet;
import nz.ac.vuw.swen301.assignment3.server.StatsServlet;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WhiteBoxTests {

    @Before
    public void clearLogs(){
        LogsServlet.logs.clear();
        LogsServlet.ids.clear();
    }


    //LOGS SERVLET GET TESTS ===========================================================================================
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
        assertEquals("[{\"id\":\"1\",\"message\":\"test\",\"timestamp\":\"11.11.1111\",\"thread\":\"thread\",\"logger\":\"logger\",\"level\":\"ERROR\",\"errorDetails\":\"eDets\"}]", result);

    }

    // LOGS SERVLET POST TESTS =========================================================================================
    @Test
    public void LOGSPOSTtestInvalidRequestResponseCode1() throws IOException {
        //TEST MISSING QUERY RESPONSE CODE
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        service.doPost(request,response);

        assertEquals(400,response.getStatus());
    }

    @Test
    public void LOGSPOSTtestInvalidRequestResponseCode2() throws IOException {
        // TEST INVALID QUERY RESPONSE CODE
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("not a valid param name","42");
        MockHttpServletResponse response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.doPost(request,response);
        assertEquals(400,response.getStatus());
    }

    @Test
    public void LOGSPOSTtestInvalidRequestResponseCode3() throws IOException {
        // TEST DUPLICATE ID RESPONSE CODE
        String logevent = "[\"{\\\"id\\\":\\\"7cfffe56-9e30-4f56-a645-855b2561798e\\\",\\\"message\\\":\\\"message1\\\",\\\"timestamp\\\":\\\"18:06:2019\\\",\\\"thread\\\":\\\"main\\\",\\\"logger\\\":\\\"test1\\\",\\\"level\\\":\\\"FATAL\\\",\\\"errorDetails\\\":\\\"\\\"}\"]";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent(logevent.getBytes());

        MockHttpServletRequest req2 = new MockHttpServletRequest();
        req2.setContent(logevent.getBytes());

        MockHttpServletResponse response = new MockHttpServletResponse();
        LogsServlet service = new LogsServlet();
        service.doPost(request,new MockHttpServletResponse());
        service.doPost(req2,response);

        assertEquals(409,response.getStatus());
    }

    @Test
    public void LOGSPOSTtestValidRequestResponseCode() throws IOException {
        // TEST SUCCESSFUL POST RESPONSE CODE
        String logevent = "[\"{\\\"id\\\":\\\"7cfffe56-9e30-4f56-a645-855b2561798e\\\",\\\"message\\\":\\\"message1\\\",\\\"timestamp\\\":\\\"18:06:2019\\\",\\\"thread\\\":\\\"main\\\",\\\"logger\\\":\\\"test1\\\",\\\"level\\\":\\\"FATAL\\\",\\\"errorDetails\\\":\\\"\\\"}\"]";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent(logevent.getBytes());
        MockHttpServletResponse response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.doPost(request,response);

        assertEquals(201,response.getStatus());
    }

    @Test
    public void LOGSPOSTtestValidContentType() throws IOException {
        // TEST VALID CASE CONTENT TYPE
        String logevent = "[\"{\\\"id\\\":\\\"7cfffe56-9e30-4f56-a645-855b2561798e\\\",\\\"message\\\":\\\"message1\\\",\\\"timestamp\\\":\\\"18:06:2019\\\",\\\"thread\\\":\\\"main\\\",\\\"logger\\\":\\\"test1\\\",\\\"level\\\":\\\"FATAL\\\",\\\"errorDetails\\\":\\\"\\\"}\"]";

        MockHttpServletRequest request = new MockHttpServletRequest();
        //Create entity
        request.setContent(logevent.getBytes());
        MockHttpServletResponse response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.doPost(request,response);

        assertTrue(response.getContentType().equals("application/json"));
    }

    @Test
    public void LOGSPOSTtestValidBehaviour() throws IOException {
        // TEST VALID CASE PUTS LOG INTO SERVLET
        String logevent = "[\"{\\\"id\\\":\\\"7cfffe56-9e30-4f56-a645-855b2561798e\\\",\\\"message\\\":\\\"message1\\\",\\\"timestamp\\\":\\\"18:06:2019\\\",\\\"thread\\\":\\\"main\\\",\\\"logger\\\":\\\"test1\\\",\\\"level\\\":\\\"FATAL\\\",\\\"errorDetails\\\":\\\"\\\"}\"]";


        MockHttpServletRequest request = new MockHttpServletRequest();
        //Create entity
        request.setContent(logevent.getBytes());
        MockHttpServletResponse response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.doPost(request,response);

        assertEquals(201, response.getStatus());
        assertEquals(1, LogsServlet.logs.size());
    }

    // STATS GET =======================================================================================================
    @Test
    public void STATSGETtestValidBehaviour1() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();

        MockHttpServletResponse response = new MockHttpServletResponse();

        StatsServlet service = new StatsServlet();
        LogsServlet logs = new LogsServlet();
        logs.logs.add(new LogEvent("id","message","timestamp","thread","logger","level","errordetails"));
        service.doGet(request,response);

        assertEquals(200, response.getStatus());
    }

    @Test
    public void STATSGETtestValidBehaviour2() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();

        MockHttpServletResponse response = new MockHttpServletResponse();

        StatsServlet service = new StatsServlet();
        LogsServlet logs = new LogsServlet();
        service.doGet(request,response);

        assertEquals("application/vnd.ms-excel", response.getContentType());
    }

    @Test
    public void STATSGETtestValidBehaviour3() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();

        MockHttpServletResponse response = new MockHttpServletResponse();

        StatsServlet service = new StatsServlet();
        LogsServlet logs = new LogsServlet();
        logs.logs.add(new LogEvent("id","message","timestamp","thread","logger","level","errordetails"));
        service.doGet(request,response);

        assertTrue(response.getContentAsString().length() > 0);
    }
}
