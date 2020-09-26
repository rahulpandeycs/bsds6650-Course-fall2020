import com.google.gson.Gson;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;

import model.ResponseMsg;

@WebServlet(name = "ResortServlet")
public class ResortsServlet extends javax.servlet.http.HttpServlet {
  protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
    response.setStatus(HttpStatus.SC_NOT_IMPLEMENTED);
  }

  protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
    String URI = request.getPathInfo();
    ResponseMsg message = new ResponseMsg("");

    final String SAMPLE_RESPONSE = "{\n" +
            "      \"skierID\": 888899,\n" +
            "      \"VertcialTotal\": 30400\n" +
            "    }";
    if(validateRequest(URI)) {
      message.setMessage(SAMPLE_RESPONSE);
    } else {
      response.setStatus(404);
      message.setMessage("Invalid input");
    }
    String jsonMessage = new Gson().toJson(message);
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    out.println(jsonMessage);
  }

  boolean validateRequest(String URI) {
    if(!URI.equals("/resort/day/top10vert")) return false;
    return true;
  }
}
