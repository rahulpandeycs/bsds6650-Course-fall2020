package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "liftRide")
@IdClass(SkierCompositeKey.class)
public class LiftRide {

  public LiftRide(){
  }

  @Id
  @Column(name="skierID")
  int skierID;

  @Id
  @Column(name="resortID")
  String resortID;

  @Id
  @Column(name="dayID")
  int dayID;

  @Id
  @Column(name="time")
  int time;

  @Column(name="liftID")
  int liftID;

  public LiftRide(String resortID, int dayID, int skierID, int time, int liftID) {
    this.resortID = resortID;
    this.dayID = dayID;
    this.skierID = skierID;
    this.time = time;
    this.liftID = liftID;
  }

  public String getResortID() {
    return resortID;
  }

//  public void setResortID(String resortID) {
//    this.resortID = resortID;
//  }

  public int getDayID() {
    return dayID;
  }

  public int getSkierID() {
    return skierID;
  }


  public int getTime() {
    return time;
  }

  public int getLiftID() {
    return liftID;
  }

  public void setLiftID(int liftID) {
    this.liftID = liftID;
  }
}
