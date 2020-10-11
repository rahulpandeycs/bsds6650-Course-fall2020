import com.google.gson.JsonParser;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static junit.framework.TestCase.assertEquals;


public class ResortsServletTest extends Mockito {
  @Test
  public void testServletGet() throws Exception {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getPathInfo()).thenReturn("/day/top10vert");

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);

    new ResortsServlet().doGet(request, response);

    //verify(request, atLeast(1)).getPathInfo("username"); // only if you want to verify username was called...
    writer.flush(); // it may not have been flushed yet...
    JsonParser parser = new JsonParser();
    assertEquals(parser.parse(stringWriter.toString()), parser.parse("{\"topTenSkiers\":[{\"skierID\":\"888899\",\"verticalTotal\":30400}]}"));
  }
}
