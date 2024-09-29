package Test;

import KernalLand.PCB;
import UserLand.Init;
import UserLand.UserLandProcess;
import os.Os;

import java.util.ArrayList;

public class Test {

   public static void main(String[]arg){
      System.out.println("Staring Test");
      ArrayList<UserLandProcess> starUpProcesses = new ArrayList<>();

      TestProcess demoted = new TestProcess(
              "Never gonna Sleep",
              0,
              false,
              PCB.Priority.RealTime
      );
      TestProcess normal = new TestProcess(
              "I Will Sleep",
              5000,
              false,
              PCB.Priority.Background
      );

      starUpProcesses.add(demoted);
      starUpProcesses.add(normal);

      Init init = new Init(starUpProcesses,true);
      Os.startUp(init);

      testDemotion(demoted, PCB.Priority.Interactive);
   }

   private static void testDemotion(TestProcess demotionProcess, PCB.Priority demotedTo){
      while (demotionProcess.PCBSetPriority!= demotedTo){
         try {
            Thread.sleep(50);
         }catch (Exception e){

         }
      }
      Os.halt();
      try {
         Thread.sleep(50);
      }catch (Exception e){

      }
      System.out.printf("\n\n Process: %s has successfully been Demoted to: %s"
              ,demotionProcess,demotionProcess.PCBSetPriority);
   }
}
