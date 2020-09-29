public class PhaseExecutionParameter {


  private int numGet;
  private int numPost;
  private int startSkierId;
  private int endSkierId;
  private int startTime;
  private int endTime;
  private int numLifts;
  private int threadsToExecute;


  public PhaseExecutionParameter(int numGet, int numPost, int startSkierId, int endSkierId, int startTime, int endTime, int numLifts, int threadsToExecute) {
    this.numGet = numGet;
    this.numPost = numPost;
    this.startSkierId = startSkierId;
    this.endSkierId = endSkierId;
    this.startTime = startTime;
    this.endTime = endTime;
    this.numLifts = numLifts;
    this.threadsToExecute = threadsToExecute;
  }


  public int getNumGet() {
    return numGet;
  }

  public int getNumPost() {
    return numPost;
  }

  public int getStartSkierId() {
    return startSkierId;
  }

  public int getEndSkierId() {
    return endSkierId;
  }

  public int getStartTime() {
    return startTime;
  }

  public int getEndTime() {
    return endTime;
  }

  public int getNumLifts() {
    return numLifts;
  }

  public int getThreadsToExecute() {
    return threadsToExecute;
  }


  public void setStartSkierId(int startSkierId) {
    this.startSkierId = startSkierId;
  }

  public void setEndSkierId(int endSkierId) {
    this.endSkierId = endSkierId;
  }

}
