package model;

import java.io.Serializable;
import java.util.Objects;

public class SkierCompositeKey implements Serializable {

  String resortID;

  private int dayID;

  private int skierID;

  private int time;

  public SkierCompositeKey() {
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SkierCompositeKey that = (SkierCompositeKey) o;
    return dayID == that.dayID &&
            skierID == that.skierID &&
            time == that.time &&
            Objects.equals(resortID, that.resortID);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resortID, dayID, skierID, time);
  }

  public SkierCompositeKey(String resortID, int dayID, int skierID, int time) {
    this.resortID = resortID;
    this.dayID = dayID;
    this.skierID = skierID;
    this.time = time;
  }


}
