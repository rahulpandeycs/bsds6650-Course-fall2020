import com.google.gson.Gson;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import model.LiftRide;
import model.ResponseMsg;
import model.Skiers;

@WebServlet(name = "SkierServlet")
public class SkiersServlet extends javax.servlet.http.HttpServlet {
  protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    String URI = request.getPathInfo();
    ResponseMsg message = new ResponseMsg("");

    if(!validatePOSTRequest(URI)) {
      response.setStatus(201);
      message.setMessage("Invalid input");
    }

    String requestBody = "";
    if ("POST".equalsIgnoreCase(request.getMethod())) {
      requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
      if(!validatePOSTRequestData(requestBody)){
        response.setStatus(HttpStatus.SC_BAD_REQUEST);
        message.setMessage("Complete data not provided!");
      }
    } else if("PUT".equalsIgnoreCase(request.getMethod()) || "DELETE".equalsIgnoreCase(request.getMethod())) {
      response.setStatus(HttpStatus.SC_NOT_IMPLEMENTED);
    }

    String jsonMessage = new Gson().toJson(message);
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    out.println(jsonMessage);
  }

  protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
    String URI = request.getPathInfo();
    ResponseMsg message = new ResponseMsg("");

    final String SAMPLE_RESPONSE = "{\n" +
            "  \"resortID\": \"Mission Ridge\",\n" +
            "  \"totalVert\": 56734\n" +
            "}";

    int validatedResult = validateGetRequest(URI);
    if(validatedResult > 0) {
      message.setMessage(SAMPLE_RESPONSE);
    } else if(validatedResult < 0){
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      message.setMessage("Invalid inputs supplied");
    } else {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      message.setMessage("Data not found");
    }

    String jsonMessage = new Gson().toJson(message);
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    out.println(jsonMessage);
  }

  int validateGetRequest(String URI) {
    // /skiers/{resortID}/days/{dayID}/skiers/{skierID}
    String[] splitURI = URI.split("/");
    if(splitURI.length == 4){
      if(!(splitURI[1].equals("skiers") && splitURI[3].equals("vertical"))) return 0;
    } else if(splitURI.length == 7){
      if(!(splitURI[1].equals("skiers") && splitURI[3].equals("days") && splitURI[5].equals("skiers"))) return 0;
    } else
      return -1;
    return 1;
  }

  boolean validatePOSTRequest(String URI) {
    if(!URI.equals("/skiers/liftrides")) return false;
    return true;
  }

  boolean validatePOSTRequestData(String body) {
    LiftRide liftRide = new Gson().fromJson(body, LiftRide.class);
    return Objects.nonNull(liftRide.getSkierID()) && Objects.nonNull(liftRide.getDayID()) && Objects.nonNull(liftRide.getDayID()) &&
            Objects.nonNull(liftRide.getResortID()) && Objects.nonNull(liftRide.getTime());
  }
}
