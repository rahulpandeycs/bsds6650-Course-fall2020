import com.google.gson.Gson;
import com.google.gson.JsonParser;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class SkiersServletTest extends Mockito {


  @Test
  public void testServletGetResortDays() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/1/days/2/skiers/3");

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    new SkiersServlet().doGet(request, response);

    writer.flush(); // it may not have been flushed yet...
    JsonParser parser = new JsonParser();
    assertEquals(parser.parse(stringWriter.toString()), parser.parse("{\"resortID\":\"Mission Ridge\",\"totalVert\":56734}\n"));
    // assertEquals(response.getStatus(),200);
  }

  @Test
  public void testServletGetSkiersVertical() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/1/vertical");

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    new SkiersServlet().doGet(request, response);


    writer.flush(); // it may not have been flushed yet...
    JsonParser parser = new JsonParser();
    assertEquals(parser.parse(stringWriter.toString()), parser.parse("{\"resortID\":\"Mission Ridge\",\"totalVert\":56734}"));
   // assertEquals(response.getStatus(),200);
  }

  @Test
  public void testServletPost() throws Exception {

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
            "  \"skierID\": 7889,\n" +
            "  \"time\": 217,\n" +
            "  \"liftID\": 21\n" +
            "}";
    BufferedReader reader = new BufferedReader(new StringReader(input));
    when(request.getReader()).thenReturn(reader);

    new SkiersServlet().doPost(request, response);

    writer.flush(); // it may not have been flushed yet...
    assertTrue(stringWriter.toString().contains("{\"message\":\"Records Created Successfully!\"}"));
   // assertEquals(response.getStatus(),201);
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
