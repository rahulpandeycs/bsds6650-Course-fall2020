import com.google.gson.Gson;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.ResponseMsg;
import model.Skiers;
import model.TopTen;

@WebServlet(name = "ResortServlet")
public class ResortsServlet extends javax.servlet.http.HttpServlet {
  private Gson gson = new Gson();
  protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
    response.setStatus(HttpStatus.SC_NOT_IMPLEMENTED);
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
    String URI = request.getPathInfo();
    ResponseMsg message = new ResponseMsg("");

    final Skiers skiers = new Skiers("888899",30400);
    List<Skiers> topTenList = new ArrayList<Skiers>();
    topTenList.add(skiers);
    final TopTen topTen = new TopTen(topTenList);

    if(validateRequest(URI)) {
      response.setStatus(HttpStatus.SC_OK);
    } else {
      response.setStatus(HttpStatus.SC_NOT_FOUND);
      message.setMessage("Invalid input");
    }
    String jsonMessage = new Gson().toJson(message);
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();
    if(!message.toString().equals(""))
      out.println(jsonMessage);
    else
      out.println(this.gson.toJson(topTen));
  }

  boolean validateRequest(String URI) {
    if(!URI.equals("/day/top10vert")) return false;
    return true;
  }
}
