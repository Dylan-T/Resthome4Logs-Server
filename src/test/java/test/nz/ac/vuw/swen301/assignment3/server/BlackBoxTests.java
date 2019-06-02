package test.nz.ac.vuw.swen301.assignment3.server;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.*;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BlackBoxTests {
    private static final String TEST_HOST = "localhost";
    private static final int TEST_PORT = 8080;
    private static final String TEST_PATH = "/resthome4logs"; // as defined in pom.xml
    private static final String SERVICE_PATH = TEST_PATH + "/logs"; // as defined in pom.xml and web.xml

    //HELPER METHODS
    @BeforeClass
    public static void startServer() throws Exception {
        Runtime.getRuntime().exec("mvn jetty:run");
        Thread.sleep(3000);
    }

    @AfterClass
    public static void stopServer() throws Exception {
        Runtime.getRuntime().exec("mvn jetty:stop");
        Thread.sleep(3000);
    }

    private HttpResponse get(URI uri) throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(uri);
        return httpClient.execute(request);
    }

    private boolean isServerReady() throws Exception {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(TEST_PATH);
        URI uri = builder.build();
        try {
            HttpResponse response = get(uri);
            boolean success = response.getStatusLine().getStatusCode() == 200;

            if (!success) {
                System.err.println("Check whether server is up and running, request to " + uri + " returns " + response.getStatusLine());
            }

            return success;
        }
        catch (Exception x) {
            System.err.println("Encountered error connecting to " + uri + " -- check whether server is running and application has been deployed");
            return false;
        }
    }


    //LOGS GET TESTS
    @Test
    public void LOGSGETtestValidRequestResponseCode () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH)
                .setParameter("limit", "10")
                .addParameter("level","DEBUG");
        URI uri = builder.build();
        HttpResponse response = get(uri);

        assertEquals(200,response.getStatusLine().getStatusCode());
    }

    @Test
    public void LOGSGETtestInvalidRequestResponseCode1 () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        // query parameter missing
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH);
        URI uri = builder.build();
        HttpResponse response = get(uri);

        assertEquals(400,response.getStatusLine().getStatusCode());
    }

    @Test
    public void LOGSGETtestInvalidRequestResponseCode2 () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        // wrong query parameter name
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH)
                .setParameter("invalidkey","J");
        URI uri = builder.build();
        HttpResponse response = get(uri);

        assertEquals(400,response.getStatusLine().getStatusCode());
    }

    @Test
    public void LOGSGETtestValidContentType () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH)
                .setParameter("limit","5")
                .addParameter("level","ERROR");
        URI uri = builder.build();
        HttpResponse response = get(uri);

        assertNotNull(response.getFirstHeader("Content-Type"));

        // use startsWith instead of assertEquals since server may append char encoding to header value
        assertTrue(response.getFirstHeader("Content-Type").getValue().startsWith("application/json"));
    }

    @Test
    public void LOGSGETtestReturnedValues () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH)
                .setParameter("limit","3")
                .addParameter("level","DEBUG");
        URI uri = builder.build();
        HttpResponse response = get(uri);

        String content = EntityUtils.toString(response.getEntity());
        String[] logs = content.split(" "); //TODO: see what separates logs
        List<String> list = Arrays.stream(logs).collect(Collectors.toList());

        assertTrue(list.get(0).equals("Joshua")); //TODO: Figure out what the logs will be
        assertTrue(list.get(1).equals("Jason"));
        assertTrue(list.get(2).equals("Jasmine"));
    }

    //LOGS POST TESTS

    @Test
    public void LOGSPOSTtestValidRequestResponseCode () throws Exception {
        Assume.assumeTrue(isServerReady());
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
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH)
                .setParameter("LogEvent", logevent);
        URI uri = builder.build();
        HttpResponse response = get(uri);

        assertEquals(201,response.getStatusLine().getStatusCode());
    }

    @Test
    public void LOGSPOSTtestInvalidRequestResponseCode1 () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        // query parameter missing
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH);
        URI uri = builder.build();
        HttpResponse response = get(uri);

        assertEquals(400,response.getStatusLine().getStatusCode());
    }

    @Test
    public void LOGSPOSTtestInvalidRequestResponseCode2 () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        // wrong query parameter name
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH)
                .setParameter("invalidkey","J");
        URI uri = builder.build();
        HttpResponse response = get(uri);

        assertEquals(400,response.getStatusLine().getStatusCode());
    }

    @Test
    public void LOGSPOSTtestValidContentType () throws Exception {
        Assume.assumeTrue(isServerReady());
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
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH)
                .setParameter("LogEvent", logevent);
        URI uri = builder.build();
        HttpResponse response = get(uri);

        assertNotNull(response.getFirstHeader("Content-Type"));

        // use startsWith instead of assertEquals since server may append char encoding to header value
        assertTrue(response.getFirstHeader("Content-Type").getValue().startsWith("application/json"));
    }

    @Test
    public void LOGSPOSTtestReturnedValues () throws Exception {
        Assume.assumeTrue(isServerReady());
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
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH)
                .setParameter("LogEvent", logevent);
        URI uri = builder.build();
        HttpResponse response = get(uri);

        String content = EntityUtils.toString(response.getEntity());
        String[] logs = content.split(" "); //TODO: see what separates logs
        List<String> list = Arrays.stream(logs).collect(Collectors.toList());

        assertTrue(list.get(0).equals("Joshua")); //TODO: Figure out what the logs will be
        assertTrue(list.get(1).equals("Jason"));
        assertTrue(list.get(2).equals("Jasmine"));
    }


    //STATS GET TESTS
    @Test
    public void STATSGETtestValidRequestResponseCode () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH);
        URI uri = builder.build();
        HttpResponse response = get(uri);

        assertEquals(200,response.getStatusLine().getStatusCode());
    }

    @Test
    public void STATSGETtestValidContentType () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH);
        URI uri = builder.build();
        HttpResponse response = get(uri);

        assertNotNull(response.getFirstHeader("Content-Type"));

        // use startsWith instead of assertEquals since server may append char encoding to header value
        assertTrue(response.getFirstHeader("Content-Type").getValue().startsWith("application/vnd.ms-excel"));
    }

    @Test
    public void STATSGETtestReturnedValues () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(SERVICE_PATH);
        URI uri = builder.build();
        HttpResponse response = get(uri);

        String content = EntityUtils.toString(response.getEntity());

        assertTrue(content.equals("Joshua")); //TODO: Figure out what will be returned
    }
}
