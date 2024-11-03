package Test;

import KernalLand.PCB;
import UserLand.*;
import UserLand.Process;
import os.Os;

import java.util.ArrayList;

import java.util.ArrayList;

/**
 * The Test class simulates process handling including sleeping, demotion, and exit.
 */
public class Test {

   /**
    * Main method initializes and runs tests on processes.
    *
    * @param args Command-line arguments (not used).
    */
   public static void main(String[] args) {
      System.out.println("Starting Test");
      ArrayList<UserLandProcess> startUpProcesses = new ArrayList<>();

      // Create test processes with different properties
      TestProcess demoted = new TestProcess("Never gonna Sleep", 0, false, PCB.Priority.RealTime);
      TestProcess exits = new TestProcess("Gonna Exit", 0, true, PCB.Priority.Background);
      TestProcess normal = new TestProcess("I Will Sleep", 10000, false, PCB.Priority.Interactive);
      TestProcess readWrite = new TestProcess("I Will Sleep", 100, false, PCB.Priority.Interactive);

      //this here is to test messaging with goodbue and hello
      // Look at the output and see it say: Pong From 2 to: 1 what: #, Ping From 1 to: 2 what: #
      // "what is being incremented"
      // might be inconsistent so reRun for clean timing and consistency
      GoodByeWorld messageBye = new GoodByeWorld(true);
      HelloWorld messageHello = new HelloWorld(true);
      startUpProcesses.add(messageHello);
      startUpProcesses.add(messageBye);
      // Initialize and start OS
      Init init = new Init(startUpProcesses, false);
      Os.startUp(init);

   }




   /**
    * Test the exit of a process.
    *
    * @param exitedProcess The process expected to exit.
    */
   private static void testExit(TestProcess exitedProcess) {
      while (!exitedProcess.Exited) {
         sleep(50);
      }
      Os.halt();
      System.out.printf("""
                The Process: %s has Exited. You should not see: %s\n
                """, exitedProcess, exitedProcess.message);
      sleep(5000);
      Os.proceed();
   }

   /**
    * Test the demotion of a process.
    *
    * @param demotionProcess The process expected to be demoted.
    * @param demotedTo The priority to which the process should be demoted.
    */
   private static void testDemotion(TestProcess demotionProcess, PCB.Priority demotedTo) {
      while (demotionProcess.PCBSetPriority != demotedTo) {
         sleep(50);
      }
      Os.halt();
      sleep(5000);
      System.out.printf("\n\n Process: %s has been demoted to: %s\n", demotionProcess.message, demotionProcess.PCBSetPriority);
      sleep(5000);
      Os.proceed();
   }

   /**
    * Test the sleeping and waking of a process.
    *
    * @param sleeper The process expected to sleep and wake.
    */
   public static void testSleeping(TestProcess sleeper) {
      while (!sleeper.Sleeping) {
         sleep(50);
      }
      Os.halt();
      System.out.printf("Process: %s has been put to sleep\n", sleeper.message);
      sleep(5000);
      Os.proceed();

      while (sleeper.Sleeping) {
         sleep(50);
      }
      Os.halt();
      System.out.printf("Process: %s has been woken up\n", sleeper.message);
      sleep(5000);
      Os.proceed();
   }



   public static void reader(){
      TestProcess readWrite = new TestProcess("I Will Sleep",
              0,
              false,
              PCB.Priority.Interactive
      );

   }

   public static void random(){

   }


   /**
    * Utility method to sleep for a specified duration.
    *
    * @param milli Milliseconds to sleep.
    */
   public static void sleep(int milli) {
      try {
         Thread.sleep(milli);
      } catch (Exception e) {
      }
   }
}

