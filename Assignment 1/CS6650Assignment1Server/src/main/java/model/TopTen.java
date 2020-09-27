package model;

import java.util.List;

public class TopTen {
  List<Skiers> topTenSkiers;

  public TopTen(List<Skiers> topTenSkiers) {
    this.topTenSkiers = topTenSkiers;
  }

  public List<Skiers> getTopTenSkiers() {
    return topTenSkiers;
  }

  public void setTopTenSkiers(List<Skiers> topTenSkiers) {
    this.topTenSkiers = topTenSkiers;
  }


}
