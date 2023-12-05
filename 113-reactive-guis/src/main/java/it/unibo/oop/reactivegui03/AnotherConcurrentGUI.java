package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class AnotherConcurrentGUI extends JFrame {

  private static final long serialVersionUID = 1L;
  private static final double WIDTH_PERC = 0.2;
  private static final double HEIGHT_PERC = 0.1;
  private final JLabel display = new JLabel();
  private final JButton stop = new JButton("stop");
  private final JButton up = new JButton("up");
  private final JButton down = new JButton("down");
  private final Agent myThread = new Agent();

  /**
   * Builds a new CGUI.
   */
  public AnotherConcurrentGUI() {
    super();
    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    final JPanel panel = new JPanel();
    panel.add(display);
    panel.add(stop);
    panel.add(up);
    panel.add(down);
    this.getContentPane().add(panel);
    this.setVisible(true);
    /*
     * Create the counter agent and start it. This is actually not so good:
     * thread management should be left to
     * java.util.concurrent.ExecutorService
     */
    ExecutorService executor = Executors.newFixedThreadPool(2);

    executor.submit(myThread);
    executor.submit(new Runnable() {

      private static final int SECONDS = 10;

      @Override
      public void run() {
        try {
          Thread.sleep(1000 * SECONDS);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        stop();
      }
    });

    /*
     * Register a listener that stops it
     */
    stop.addActionListener((e) -> stop());
    up.addActionListener((e) -> myThread.countUp());
    down.addActionListener((e) -> myThread.countDown());
  }

  synchronized private void stop() {
    myThread.stopCounting();
    for (var btn : List.of(up, down, stop)) {
      btn.setEnabled(false);
    }
  }

  /*
   * The counter agent is implemented as a nested class. This makes it
   * invisible outside and encapsulated.
   */
  private class Agent implements Runnable {
    /*
     * Stop is volatile to ensure visibility. Look at:
     * 
     * http://archive.is/9PU5N - Sections 17.3 and 17.4
     * 
     * For more details on how to use volatile:
     * 
     * http://archive.is/4lsKW
     * 
     */
    private volatile boolean stop;
    private volatile boolean countUp = true;
    private int counter;

    @Override
    public void run() {
      while (!this.stop) {
        try {
          // The EDT doesn't access `counter` anymore, it doesn't need to be volatile
          final var nextText = Integer.toString(this.counter);
          SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
          this.counter += countUp ? 1 : -1;
          Thread.sleep(100);
        } catch (InvocationTargetException | InterruptedException ex) {
          /*
           * This is just a stack trace print, in a real program there
           * should be some logging and decent error reporting
           */
          ex.printStackTrace();
        }
      }
    }

    /**
     * External command to stop counting.
     */
    public void stopCounting() {
      this.stop = true;
    }

    public void countDown() {
      this.countUp = false;
    }

    public void countUp() {
      this.countUp = true;
    }
  }
}