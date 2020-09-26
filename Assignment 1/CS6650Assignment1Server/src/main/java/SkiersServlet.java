import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;

import model.ResponseMsg;

@WebServlet(name = "SkierServlet")
public class SkiersServlet extends javax.servlet.http.HttpServlet {
  protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

  }

  protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
    String URI = request.getPathInfo();
    ResponseMsg message = new ResponseMsg("");

    final String SAMPLE_RESPONSE = "{\n" +
            "  \"resortID\": \"Mission Ridge\",\n" +
            "  \"totalVert\": 56734\n" +
            "}";

    if(validateGetRequest(URI)) {
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

  boolean validateGetRequest(String URI) {

    // /skiers/{resortID}/days/{dayID}/skiers/{skierID}

    String[] splitURI = URI.split("/");
    if(!(splitURI.length >= 7)) return false;
    return true;
  }

  boolean validatePOSTRequest(String URI) {
    if(!URI.equals("/skiers/liftrides")) return false;
    return true;
  }
}
