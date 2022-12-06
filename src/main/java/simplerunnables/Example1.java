package simplerunnables;

class MyTask implements Runnable {

  @Override
  public void run() {
    for (int x = 0; x < 5; x++) {
      System.out.println("Hello, x is " + x);
    }
  }
}

public class Example1 {
  public static void main(String[] args) {
    MyTask mt = new MyTask();
    Thread t = new Thread(mt);
    System.out.println("About to start thread");
    t.start();
    System.out.println("Thread started");
  }
}
