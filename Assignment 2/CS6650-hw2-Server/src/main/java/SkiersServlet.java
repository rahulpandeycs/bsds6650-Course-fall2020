import com.google.gson.Gson;

import org.apache.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import dao.LiftRideDao;
import dao.SkierVerticalDao;
import model.LiftRide;
import model.ResponseMsg;
import model.SkierVertical;

@WebServlet(name = "SkierServlet")
public class SkiersServlet extends javax.servlet.http.HttpServlet {

  private Gson gson = new Gson();
  private static final long serialVersionUID = 1L;
  private LiftRideDao liftRideDao;
  private SkierVerticalDao skierVerticalDao;

  public void init() {
    liftRideDao = new LiftRideDao();
    skierVerticalDao = new SkierVerticalDao();
  }

  protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    String URI = request.getPathInfo();
    ResponseMsg message = new ResponseMsg("");

    if (!validatePOSTRequest(URI)) {
      response.setStatus(HttpStatus.SC_BAD_REQUEST);
      message.setMessage("Invalid input");
    }

    String requestBody = "";
    LiftRide liftRide = null;
    if ("POST".equalsIgnoreCase(request.getMethod())) {
      BufferedReader reader = request.getReader();
      requestBody = reader.lines().collect(Collectors.joining(System.lineSeparator()));

      try {
        liftRide = new Gson().fromJson(requestBody, LiftRide.class);
      } catch (Exception ex) {
        response.setStatus(HttpStatus.SC_BAD_REQUEST);
        message.setMessage("Incorrect data provided in request!");
      }

      int result = validatePOSTRequestData(liftRide);
      if (result < 0) {
        response.setStatus(HttpStatus.SC_BAD_REQUEST);
        message.setMessage("Complete data not provided!");
      } else {
        liftRideDao.saveLiftRide(liftRide);
        //       SkierVerticalDao skierVerticalDao = new SkierVerticalDao();
        skierVerticalDao.saveSkierVertical(new SkierVertical(liftRide.getResortID(), liftRide.getLiftID() * 10));

        response.setStatus(HttpStatus.SC_CREATED);
      }
    } else if ("PUT".equalsIgnoreCase(request.getMethod()) || "DELETE".equalsIgnoreCase(request.getMethod())) {
      response.setStatus(HttpStatus.SC_NOT_IMPLEMENTED);
    }

    String jsonMessage = new Gson().toJson(message);
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();

    if (!message.toString().equals(""))
      out.println(jsonMessage);
    else
      out.println(this.gson.toJson(liftRide));
  }

  protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    String URI = request.getPathInfo();
    ResponseMsg message = new ResponseMsg("");

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
    else {
      String outputJson = "";
      String[] urlSplit = URI.split("/");
      if (urlSplit.length > 3 && urlSplit.length == 6) {
        // Get the total vertical for the skier for the specified ski day
        int skierID = Integer.parseInt(urlSplit[5]);
        String resortID = urlSplit[1];
        int dayID = Integer.parseInt(urlSplit[3]);
        SkierVertical skierVertical = skierVerticalDao.getSkierVertical(resortID);
        outputJson = this.gson.toJson(skierVertical);
      } else if (urlSplit.length == 3) {
        // Get the total vertical for the skier the specified resort.
        String query = request.getQueryString();
        String skierID = urlSplit[1];
        String resortID = query.split("=")[1];
        SkierVertical skierVertical = skierVerticalDao.getSkierVertical(resortID);
        outputJson = this.gson.toJson(skierVertical);
      }

      out.println(outputJson);
    }
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

  int validatePOSTRequestData(LiftRide liftRideModel) {
    if (Objects.isNull(liftRideModel.getSkierID()) || Objects.isNull(liftRideModel.getDayID()) || Objects.isNull(liftRideModel.getDayID()) ||
            Objects.isNull(liftRideModel.getResortID()) || Objects.isNull(liftRideModel.getTime()) || liftRideModel.getSkierID() == 0
            || liftRideModel.getTime() == 0 || liftRideModel.getLiftID() == 0 || liftRideModel.getDayID() == 0 || liftRideModel.getResortID().equals("")) {
      return -1;
    }
    return 1;
  }

}
