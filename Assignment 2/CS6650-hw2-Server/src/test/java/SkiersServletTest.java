import com.google.gson.JsonParser;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class SkiersServletTest extends Mockito {

  @Test
  public void testServletGetResortDaysWithInit() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/MissionRidge/days/23/skiers/7889");

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    SkiersServlet skierServlet = new SkiersServlet();
    skierServlet.init();
    skierServlet.doGet(request, response);

    writer.flush(); // it may not have been flushed yet...
    JsonParser parser = new JsonParser();
    assertEquals(parser.parse(stringWriter.toString()), parser.parse("{\"resortID\":\"MissionRidge\",\"totalVert\":460}"));
  }

  @Test
  public void testServletGetSkiersVertical() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/7887/vertical");
    when(request.getQueryString()).thenReturn("resortID=MissionRidge");

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    SkiersServlet skierServlet = new SkiersServlet();
    skierServlet.init();
    skierServlet.doGet(request, response);

    writer.flush(); // it may not have been flushed yet...
    JsonParser parser = new JsonParser();
    assertEquals(parser.parse(stringWriter.toString()), parser.parse("{\"resortID\":\"MissionRidge\",\"totalVert\":0}"));
  }


  @Test //TODO
  public void testServletTruncateLiftRide() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("truncate");

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    SkiersServlet skierServlet = new SkiersServlet();
    skierServlet.init();
    skierServlet.doGet(request, response);


    writer.flush(); // it may not have been flushed yet...
    JsonParser parser = new JsonParser();
    assertEquals(parser.parse(stringWriter.toString()), parser.parse(""));
  }

  @Test
  public void testServletPost2() throws Exception {

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/liftrides");
    when(request.getMethod()).thenReturn("POST");

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    String input = "{\n" +
            "  \"resortID\": \"MissionRidge\",\n" +
            "  \"dayID\": 23,\n" +
            "  \"skierID\": 7887,\n" +
            "  \"time\": 217,\n" +
            "  \"liftID\": 12\n" +
            "}";
    BufferedReader reader = new BufferedReader(new StringReader(input));
    when(request.getReader()).thenReturn(reader);
    SkiersServlet skierServlet = new SkiersServlet();
    skierServlet.init();
    skierServlet.doPost(request, response);

    writer.flush(); // it may not have been flushed yet...
    assertTrue(stringWriter.toString().contains("{\"skierID\":7887,\"resortID\":\"MissionRidge\",\"dayID\":23,\"time\":217,\"liftID\":12}"));
  }

  @Test
  public void testServletPostNegativeIncorrectDataType() throws Exception {

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/liftrides");
    when(request.getMethod()).thenReturn("POST");

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    String input = "{\n" +
            "  \"resortID\": \"Mission Ridge\",\n" +
            "  \"dayID\": 23,\n" +
            "  \"skierID\": \"SkierId\",\n" +
            "  \"time\": 217,\n" +
            "  \"liftID\": 21\n" +
            "}";
    BufferedReader reader = new BufferedReader(new StringReader(input));
    when(request.getReader()).thenReturn(reader);

    new SkiersServlet().doPost(request, response);

    writer.flush(); // it may not have been flushed yet...
    assertTrue(stringWriter.toString().contains("{\"message\":\"Incorrect data provided in request!\"}"));
  }

  @Test
  public void testServletPostNegativeMissingValue() throws Exception {

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/liftrides");
    when(request.getMethod()).thenReturn("POST");

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);


    String input = "{\n" +
            "  \"resortID\": \"Mission Ridge\",\n" +
            "  \"dayID\": 23,\n" +
            "  \"skierID\": 7889" +
            "}";
    BufferedReader reader = new BufferedReader(new StringReader(input));
    when(request.getReader()).thenReturn(reader);

    new SkiersServlet().doPost(request, response);

    writer.flush(); // it may not have been flushed yet...
    assertTrue(stringWriter.toString().contains("{\"message\":\"Complete data not provided!\"}"));
    // assertEquals(response.getStatus(),201);
  }

  @Test
  public void testServletGetResortDaysNegative() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/1/days/abc/skiers/3");

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    new SkiersServlet().doGet(request, response);

    writer.flush(); // it may not have been flushed yet...
    JsonParser parser = new JsonParser();
    assertEquals(parser.parse(stringWriter.toString()), parser.parse("{\"message\":\"Data not found\"}"));
    // assertEquals(response.getStatus(),404);
  }


  @Test
  public void testServletGetResortDaysNegative2() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/1/days/12/skiers/acs");

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    new SkiersServlet().doGet(request, response);

    writer.flush(); // it may not have been flushed yet...
    JsonParser parser = new JsonParser();
    assertEquals(parser.parse(stringWriter.toString()), parser.parse("{\"message\":\"Data not found\"}"));
    // assertEquals(response.getStatus(),404);
  }

  @Test
  public void testServletGetResortDaysNegative3() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/1/days/12/skiers/");

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    new SkiersServlet().doGet(request, response);

    writer.flush(); // it may not have been flushed yet...
    JsonParser parser = new JsonParser();
    assertEquals(parser.parse(stringWriter.toString()), parser.parse("{\"message\":\"Invalid inputs supplied for: /1/days/12/skiers/\"}"));
    //  assertEquals(response.getStatus(),400);
  }

}
