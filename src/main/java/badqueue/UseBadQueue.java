package badqueue;

import java.util.Arrays;

class BadQueue<E> {
  private static final int CAPACITY = 10;
  private E[] data = (E[])(new Object[CAPACITY]);
  private int count = 0;
  private Object rendezvous = new Object();

  public void put(E e) throws InterruptedException {
    synchronized (this.rendezvous) {
      // overflow!!??
      while (count == CAPACITY) {
        // hang around a bit
        // MUST give the key back, to allow the chance
        // for this situation (full queue) to change!!!
//        Thread.sleep(1); // does not give key back :(
        // gives key back, but also RECOVERS IT
        // before continuing. MUST BE transactionally stable
        // when calling wait
        this.rendezvous.wait();
      }
      data[count++] = e;
//      this.rendezvous.notify();
      this.rendezvous.notifyAll(); // correct but unscalable
    }
  }

  public E take() throws InterruptedException {
    synchronized (this.rendezvous) {
      while (count == 0) {
        this.rendezvous.wait();
      }
      E rv = data[0];
      System.arraycopy(data, 1, data, 0, --count);
//      this.rendezvous.notify(); // shakes "pillow" of rendezvous
      this.rendezvous.notifyAll(); // unscalable but valid
      return rv;
    }
  }
}

public class UseBadQueue {
  public static void main(String[] args) throws InterruptedException {
    BadQueue<int[]> queue = new BadQueue<>();

    Thread pThread = new Thread(() -> {
      try {
        for (int i = 0; i < 10_000; i++) {
          int[] data = {i, 0};
          if (i < 500) {
            Thread.sleep(1);
          }
          data[1] = i; // phew now transactionally stable

          if (i == 5_000) {
            data[0] = -1; // test the test :)
          }
          queue.put(data);
        }
      } catch (InterruptedException ie) {
        System.out.println("Interesting shutdown requested!!!");
      }
    });
    Thread cThread = new Thread(() -> {
      try {
        for (int i = 0; i < 10_000; i++) {
          int [] data = queue.take();
          if (data[0] != data[1] || data[0] != i) {
            System.out.println("***** Error at position "
                + i + ", " + Arrays.toString(data));
          }
          if (i > 9_500) {
            Thread.sleep(1);
          }
        }
      } catch (InterruptedException ie) {
        System.out.println("Interesting shutdown requested!!!");
      }
    });
    pThread.start();
    cThread.start();
    pThread.join();
    cThread.join();
    System.out.println("All done");
  }
}
