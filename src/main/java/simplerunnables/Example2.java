package simplerunnables;

class MyTask2 implements Runnable {
  // NOPE, volatile addresses issues of visibility,
  // but NOT issues of transactional correctness.
  /*volatile*/ long counter = 0;
  private Object rendezvous = new Object();

  @Override
  public void run() {
    for (int i = 0; i < 500_000_000; i++) {
      synchronized (this.rendezvous) {
        counter++; // read-modify-write
      }
    }
    System.out.println("updater finished");
  }
}

public class Example2 {
  public static void main(String[] args) throws InterruptedException {
    long start = System.nanoTime();
    MyTask2 mt2 = new MyTask2();
//    mt2.run();
    Thread t2 = new Thread(mt2);
    t2.start();
    Thread t3 = new Thread(mt2);
    t3.start();
//    Thread.sleep(18_000);
    t2.join();
    t3.join();
    long time = System.nanoTime() - start;
    System.out.println("Counter is " + mt2.counter);
    System.out.printf("Time taken: %7.3f\n", (time / 1_000_000_000.0));
  }
}
