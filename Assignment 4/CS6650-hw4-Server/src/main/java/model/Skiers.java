package model;

public class Skiers {

  String skierID;
  int verticalTotal;

  public Skiers(String skierID, int verticalTotal) {
    this.skierID = skierID;
    this.verticalTotal = verticalTotal;
  }

  public String getSkierID() {
    return skierID;
  }

  public void setSkierID(String skierID) {
    this.skierID = skierID;
  }

  public int getVerticalTotal() {
    return verticalTotal;
  }

  public void setVerticalTotal(int verticalTotal) {
    this.verticalTotal = verticalTotal;
  }

  @Override
  public String toString() {
    return "Skiers{" +
            "skierID='" + skierID + '\'' +
            ", verticalTotal=" + verticalTotal +
            '}';
  }
}
