package Test;

import Hardware.Hardware;
import KernalLand.PCB;
import UserLand.*;
import os.Os;

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
      String Story = """
              Halloween ends:
              never told you how I spent my childhood.
               I learned how to shoot a gun when I was eight.
               I learned how to fight. I had nightmares about the basement
              """;

      String Story2 = """
      the quick brown fox jumped over the lazy dog
      the quick brown fox jumped over the lazy dog
      the quick brown fox jumped over the lazy dog
      """;

      //Make sure to view TestProcess test Functions as well!!!
      // they show comparisons if what was written is whats read

      // Use this Block to test Functionality
      // Only one function can run at a time so uncomment desired func
      // comment all others
      // as well to make it easy to test just run the
      // testAllFunctionality func
      // I made it so that important events are printed to
      // Stderr for syntax HighLighting NOT ACTUAL ERRORS
      // Lastly run a couple times for consistency

      // Start of Paging Test Block:
//      testAllFunctionality(Story,Story2);
//      testFreeMemory(Story);
//      testPageFault();
//      testNoOverwriting(Story);
      // End Of Paging Test Block

      testPiggy(Story);
//      Init init = new Init(startUpProcesses, true);
//      Os.startUp(init);


   }


   private static void testPiggy(String story){
      ArrayList<UserLandProcess> startUpProcesses = new ArrayList<>();
      for (int i = 0; i < 21; i++) {
         boolean exits = (i % 2) != 0;
         Piggy storyWriter= new Piggy("Gonna write a Story",100, PCB.Priority.Background, false);
//         Piggy pig= new Piggy("Gonna write a Story",20, PCB.Priority.Background,exits);
         startUpProcesses.add(storyWriter);
//         startUpProcesses.add(pig);
         storyWriter.story = story;
      }
      Init init = new Init(startUpProcesses, true);
      Os.startUp(init);
   }


   private static void testAllFunctionality(String story, String story2){
      ArrayList<UserLandProcess> startUpProcesses = new ArrayList<>();
      for (int i = 0; i <2 ; i++) {
         TestProcess storyWriter= new TestProcess("Gonna write a Story",20, PCB.Priority.Interactive,false);
         TestProcess freeMemory= new TestProcess("Gonna Free 20 Pages",20,PCB.Priority.RealTime,false);
         storyWriter.story = story;
         startUpProcesses.add(storyWriter);
         startUpProcesses.add(freeMemory);
      }
      // forced exits bye kernel
      TestProcess writer = new TestProcess("Gonna Touch a unAllocated Page",1,PCB.Priority.RealTime,false);
      TestProcess testProcess2 = new TestProcess("Writing a story and exiting",6, PCB.Priority.Interactive,true);
      testProcess2.story = story2;
      // exits
      startUpProcesses.add(writer);
      startUpProcesses.add(testProcess2);

      Init init = new Init(startUpProcesses, true);
      Os.startUp(init);
   }



   private static void testNoOverwriting(String story){
      ArrayList<UserLandProcess> startUpProcesses = new ArrayList<>();
      TestProcess writer = new TestProcess("Writing number",6,PCB.Priority.RealTime,false);
      TestProcess storyWriter= new TestProcess("Making Story",6, PCB.Priority.Interactive,false);
      TestProcess testProcess1= new TestProcess("Test process23",6,PCB.Priority.RealTime,false);
      TestProcess testProcess2 = new TestProcess("Test Process234",6, PCB.Priority.Interactive,false);
      storyWriter.story = story;
      Init init = new Init(startUpProcesses, true);
      Os.startUp(init);
      Os.halt();
   }

   private static void testFreeMemory(String story){
      ArrayList<UserLandProcess> startUpProcesses = new ArrayList<>();
      TestProcess writer = new TestProcess("Gonna Free 22 pages",22,PCB.Priority.RealTime,true);
      TestProcess storyWriter= new TestProcess("Writing and freeing 36 pages",34, PCB.Priority.Interactive,true);
      storyWriter.story = story;
      startUpProcesses.add(writer);
      startUpProcesses.add(storyWriter);
      Init init = new Init(startUpProcesses, true);
      Os.startUp(init);
   }

   private static void testPageFault(){
      ArrayList<UserLandProcess> startUpProcesses = new ArrayList<>();
      TestProcess writer = new TestProcess("Gonna memory thats not mine",1,PCB.Priority.RealTime,false);
      startUpProcesses.add(writer);
      Init init = new Init(startUpProcesses, true);
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

