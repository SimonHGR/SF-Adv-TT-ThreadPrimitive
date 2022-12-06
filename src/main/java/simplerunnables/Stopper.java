package simplerunnables;

public class Stopper {
  private static volatile boolean stop = false;

  public static void main(String[] args) throws InterruptedException {
    Runnable mt = () -> {
      System.out.println(Thread.currentThread().getName()
        + " starting (I'm the stopper :)");

      while (! stop)
        /*System.out.print(".");*/;

      System.out.println(Thread.currentThread().getName()
          + " stopping!");
    };

    new Thread(mt).start();
    System.out.println("Worker thread started...");
    Thread.sleep(1_000);
    System.out.println("setting stop flag");
    stop = true;
    System.out.println("stop flag set, main exiting");
  }
}
