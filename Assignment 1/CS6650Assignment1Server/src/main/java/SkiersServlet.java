import com.google.gson.Gson;

import org.apache.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import model.LiftRide;
import model.ResponseMsg;
import model.SkierVertical;

@WebServlet(name = "SkierServlet")
public class SkiersServlet extends javax.servlet.http.HttpServlet {
  private Gson gson = new Gson();

  protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    String URI = request.getPathInfo();
    ResponseMsg message = new ResponseMsg("");

    if (!validatePOSTRequest(URI)) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      message.setMessage("Invalid input");
    }

    String requestBody = "";
    if ("POST".equalsIgnoreCase(request.getMethod())) {
      BufferedReader reader = request.getReader();
      requestBody = reader.lines().collect(Collectors.joining(System.lineSeparator()));

      int result = validatePOSTRequestData(requestBody);
      if (result < 0) {
        response.setStatus(HttpStatus.SC_BAD_REQUEST);
        message.setMessage("Complete data not provided!");
      } else if (result == 0) {
        response.setStatus(HttpStatus.SC_BAD_REQUEST);
        message.setMessage("Incorrect data provided in request!");
      } else {
        message.setMessage("Records Created Successfully!");
        response.setStatus(HttpStatus.SC_CREATED);
      }
    } else if ("PUT".equalsIgnoreCase(request.getMethod()) || "DELETE".equalsIgnoreCase(request.getMethod())) {
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

    final SkierVertical skierVertical = new SkierVertical("Mission Ridge", 56734);

    int validatedResult = validateGetRequest(URI);
    if (validatedResult > 0) {
      response.setStatus(HttpStatus.SC_OK);
    } else if (validatedResult < 0) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      message.setMessage("Invalid inputs supplied for: " + URI);
    } else {
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      message.setMessage("Data not found");
    }

    String jsonMessage = new Gson().toJson(message);
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    response.setCharacterEncoding("UTF-8");

    if (!message.toString().equals(""))
      out.println(jsonMessage);
    else
      out.println(this.gson.toJson(skierVertical));
  }

  int validateGetRequest(String URI) {
    // /skiers/{resortID}/days/{dayID}/skiers/{skierID}
    String[] splitURI = URI.split("/");
    if (splitURI.length == 3) {
      if (!(splitURI[2].equals("vertical"))) return 0;
      String skierID = splitURI[1];
      try {
        Integer.parseInt(skierID);
      } catch (NumberFormatException ex) {
        return 0;
      }
    } else if (splitURI.length == 6) {
      if (!(splitURI[2].equals("days") && splitURI[4].equals("skiers"))) return 0;
      String dayID = splitURI[3];
      String skierID = splitURI[5];
      try {
        Integer.parseInt(skierID);
        Integer.parseInt(dayID);
      } catch (NumberFormatException ex) {
        return 0;
      }
    } else
      return -1;
    return 1;
  }

  boolean validatePOSTRequest(String URI) {
    if (!URI.equals("/liftrides")) return false;
    return true;
  }

  int validatePOSTRequestData(String body) {
    LiftRide liftRide = null;

    try {
      liftRide = new Gson().fromJson(body, LiftRide.class);
    } catch (Exception ex) {
      return 0;
    }

    if (Objects.isNull(liftRide.getSkierID()) || Objects.isNull(liftRide.getDayID()) || Objects.isNull(liftRide.getDayID()) ||
            Objects.isNull(liftRide.getResortID()) || Objects.isNull(liftRide.getTime()) || liftRide.getSkierID() == 0
            || liftRide.getTime() == 0 || liftRide.getLiftID() == 0 || liftRide.getDayID() == 0 || liftRide.getResortID().equals("")) {
      return -1;
    }
    return 1;
  }
}
