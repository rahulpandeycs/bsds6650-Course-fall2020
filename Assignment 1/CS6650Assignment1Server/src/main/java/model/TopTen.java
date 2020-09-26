package model;

import java.util.List;

public class TopTen {
  public List<Skiers> getTopTenSkiers() {
    return topTenSkiers;
  }

  public void setTopTenSkiers(List<Skiers> topTenSkiers) {
    this.topTenSkiers = topTenSkiers;
  }

  List<Skiers> topTenSkiers;
}
