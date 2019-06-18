package test.nz.ac.vuw.swen301.assignment3.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.*;
import java.net.URI;
import java.net.URISyntaxException;
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
    private static final String LOGS_PATH = TEST_PATH + "/logs"; // as defined in pom.xml and web.xml
    private static final String STATS_PATH = TEST_PATH + "/stats";

    static Process process;

    //HELPER METHODS

    private JsonObject getJsonLog(){
        JsonObject json = new JsonObject();
        json.addProperty("id", "123");
        json.addProperty("timestamp", "16:06:2019");
        json.addProperty("thread", "main");
        json.addProperty("logger", "testLogger");
        json.addProperty("level", "DEBUG");
        json.addProperty("errorDetails", "string");
        return json;
    }

    @BeforeClass
    public static void startServer() throws Exception {
        //process = new ProcessBuilder("mvn jetty:run").start();
        process = Runtime.getRuntime().exec("mvn jetty:run");
        Thread.sleep(10000);
    }

    @AfterClass
    public static void stopServer() throws Exception {
        process.destroy();
        //Runtime.getRuntime().exec("mvn jetty:stop");
        Thread.sleep(3000);
    }

    private HttpResponse get(URI uri) throws Exception {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(uri);
        return httpClient.execute(request);
    }

    private HttpResponse post(URI uri, String arg) throws Exception{
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(uri);

        //Create entity
        if(arg != null) {
            StringEntity params = new StringEntity(arg);
            params.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            request.setEntity(params);
        }

        return httpClient.execute(request);
    }


    private boolean isServerReady() throws Exception {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOGS_PATH)
                .addParameter("level","WARN").addParameter("limit", "25");
        URI uri = builder.build();
        try {
            HttpResponse response = get(uri);
            boolean success = response.getStatusLine().getStatusCode() == 200;

            if (!success) {
                System.err.println("Check whether server is up and running, request to " + uri
                        + " returns " + response.getStatusLine());
            }

            return success;
        }
        catch (Exception x) {
            System.err.println("Encountered error connecting to " + uri
                    + " -- check whether server is running and application has been deployed");
            return false;
        }
    }


    // LOGS GET TESTS===================================================================================================
    @Test
    public void LOGSGETtestValidRequestResponseCode () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOGS_PATH)
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
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOGS_PATH);
        URI uri = builder.build();
        HttpResponse response = get(uri);

        assertEquals(400,response.getStatusLine().getStatusCode());
    }

    @Test
    public void LOGSGETtestInvalidRequestResponseCode2 () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        // wrong query parameter name
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOGS_PATH)
                .setParameter("invalidkey","J");
        URI uri = builder.build();
        HttpResponse response = get(uri);

        assertEquals(400,response.getStatusLine().getStatusCode());
    }

    @Test
    public void LOGSGETtestValidContentType () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOGS_PATH)
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
        String logevent = "[\"{\\\"id\\\":\\\"7cfffe56-9e30-4f56-a645-855b2561798e\\\",\\\"message\\\":\\\"message1\\\",\\\"timestamp\\\":\\\"18:06:2019\\\",\\\"thread\\\":\\\"main\\\",\\\"logger\\\":\\\"test1\\\",\\\"level\\\":\\\"FATAL\\\",\\\"errorDetails\\\":\\\"\\\"}\"]";
        System.out.println(logevent);

        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost("localhost:8080").setPath("/resthome4logs/logs");
        URI uri = builder.build();

        // create and execute the request
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(uri);
        request.addHeader("content-type", "application/json");

        //Create entity
        StringEntity params = new StringEntity(logevent);
        request.setEntity(params);
        HttpResponse r = httpClient.execute(request);
        System.out.println(r.getStatusLine().getStatusCode());


        //build request and set parameters
        URIBuilder builder2 = new URIBuilder();
        builder2.setScheme("http").setHost("localhost:8080").setPath("/resthome4logs/logs")
                .addParameter("limit", ""+3)
                .addParameter("level", "FATAL");
        URI uri2 = null;
        try {
            uri2 = builder2.build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpClient httpClient2 = HttpClientBuilder.create().build();
        HttpGet request2 = new HttpGet(uri2);

        //execute request
        HttpResponse response = httpClient2.execute(request2);
        String logs = EntityUtils.toString(response.getEntity());
        System.out.println(logs);
        assertEquals("[{\"id\":\"7cfffe56-9e30-4f56-a645-855b2561798e\",\"message\":\"message1\",\"timestamp\":\"18:06:2019\",\"thread\":\"main\",\"logger\":\"test1\",\"level\":\"FATAL\",\"errorDetails\":\"\"}]", logs);
    }

    // LOGS POST TESTS==================================================================================================

    @Test
    public void LOGSPOSTtestValidRequestResponseCode () throws Exception {
        Assume.assumeTrue(isServerReady());
        String logevent = "[\"{\\\"id\\\":\\\"bde128b4-e07b-44be-ad30-0971d970b757\\\",\\\"message\\\":\\\"message2\\\",\\\"timestamp\\\":\\\"18:06:2019\\\",\\\"thread\\\":\\\"main\\\",\\\"logger\\\":\\\"test1\\\",\\\"level\\\":\\\"ERROR\\\",\\\"errorDetails\\\":\\\"\\\"}\",\"{\\\"id\\\":\\\"25729e67-0ad9-44a8-a5ad-d7cf0b5b04da\\\",\\\"message\\\":\\\"message1\\\",\\\"timestamp\\\":\\\"18:06:2019\\\",\\\"thread\\\":\\\"main\\\",\\\"logger\\\":\\\"test1\\\",\\\"level\\\":\\\"ERROR\\\",\\\"errorDetails\\\":\\\"\\\"}\"]";
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOGS_PATH);
        URI uri = builder.build();

        HttpResponse response = post(uri, logevent);

        assertEquals(201,response.getStatusLine().getStatusCode());
    }

    @Test
    public void LOGSPOSTtestInvalidRequestResponseCode1 () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        // query parameter missing
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOGS_PATH);
        URI uri = builder.build();
        HttpResponse response = post(uri, null);

        assertEquals(400,response.getStatusLine().getStatusCode());
    }

    @Test
    public void LOGSPOSTtestInvalidRequestResponseCode2 () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        // wrong query parameter name
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOGS_PATH);
        URI uri = builder.build();
        HttpResponse response = post(uri, "hello");

        assertEquals(400,response.getStatusLine().getStatusCode());
    }

    @Test
    public void LOGSPOSTtestValidContentType () throws Exception {
        Assume.assumeTrue(isServerReady());
        String logevent = "[" +
                "  {" +
                "    \"id\": \"d290f1ee-6c54-4b01-90e6-d701748f0851\"," +
                "    \"message\": \"application started\"," +
                "    \"timestamp\": \"16.6.2019\"," +
                "    \"thread\": \"main\"," +
                "    \"logger\": \"com.example.Foo\"," +
                "    \"level\": \"DEBUG\"," +
                "    \"errorDetails\": \"string\"" +
                "  }" +
                "]";
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(LOGS_PATH);
        URI uri = builder.build();
        HttpResponse response = post(uri, logevent);

        assertNotNull(response.getFirstHeader("Content-Type"));

        // use startsWith instead of assertEquals since server may append char encoding to header value
        assertTrue(response.getFirstHeader("Content-Type").getValue().startsWith("application/json"));
    }




    // STATS GET TESTS==================================================================================================
    @Test
    public void STATSGETtestValidRequestResponseCode () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(STATS_PATH);
        URI uri = builder.build();
        HttpResponse response = get(uri);

        assertEquals(200,response.getStatusLine().getStatusCode());
    }

    @Test
    public void STATSGETtestValidContentType () throws Exception {
        Assume.assumeTrue(isServerReady());
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(STATS_PATH);
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
        builder.setScheme("http").setHost(TEST_HOST).setPort(TEST_PORT).setPath(STATS_PATH);
        URI uri = builder.build();
        HttpResponse response = get(uri);

        String content = EntityUtils.toString(response.getEntity());

        assertTrue(content.length() > 0);
    }
}
