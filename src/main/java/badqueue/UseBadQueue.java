package badqueue;

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
      this.rendezvous.notify();
    }
  }

  public E take() throws InterruptedException {
    synchronized (this.rendezvous) {
      while (count == 0) {
        this.rendezvous.wait();
      }
      E rv = data[0];
      System.arraycopy(data, 1, data, 0, --count);
      this.rendezvous.notify(); // shakes "pillow" of rendezvous
      return rv;
    }
  }
}

public class UseBadQueue {
  public static void main(String[] args) {

  }
}
