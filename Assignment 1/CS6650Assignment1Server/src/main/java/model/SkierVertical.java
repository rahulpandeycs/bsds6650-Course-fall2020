package model;

public class SkierVertical {

  String resortID;
  int totalVert;

  public SkierVertical(String resortID, int totalVert) {
    this.resortID = resortID;
    this.totalVert = totalVert;
  }

  public int getTotalVert() {
    return totalVert;
  }

  public void setTotalVert(int totalVert) {
    this.totalVert = totalVert;
  }


  public String getResortID() {
    return resortID;
  }

  public void setResortID(String resortID) {
    this.resortID = resortID;
  }

  @Override
  public String toString() {
    return "SkierVertical{" +
            "resortID='" + resortID + '\'' +
            ", totalVert=" + totalVert +
            '}';
  }
}
