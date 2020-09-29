public class SharedGlobalCount {

  int counter;

  synchronized public int getCounter() {
    return counter;
  }

  synchronized public void incrementCounter() {
    this.counter++;
  }

  synchronized public void incrementCounterBy(int value) {
    this.counter += value;
  }

}
