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

    //verify(request, atLeast(1)).getPathInfo("username"); // only if you want to verify username was called...
    writer.flush(); // it may not have been flushed yet...
    JsonParser parser = new JsonParser();
    assertEquals(parser.parse(stringWriter.toString()), parser.parse("{\"resortID\":\"Mission Ridge\",\"totalVert\":56734}\n"));
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

    //verify(request, atLeast(1)).getPathInfo("username"); // only if you want to verify username was called...
    writer.flush(); // it may not have been flushed yet...
    JsonParser parser = new JsonParser();
    assertEquals(parser.parse(stringWriter.toString()), parser.parse("{\"resortID\":\"Mission Ridge\",\"totalVert\":56734}"));
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

 //   verify(request, atLeast(1)).getParameter("username"); // only if you want to verify username was called...
    writer.flush(); // it may not have been flushed yet...
    assertTrue(stringWriter.toString().contains("Records Created Successfully!"));
  }
}
