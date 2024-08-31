package os;

import KernalLand.Kernel;
import UserLand.Init;
import UserLand.UserLandProcess;

import java.util.ArrayList;
import java.util.Arrays;

public class Os {

   private static Kernel kernel;

   public enum callType{
      CreateProcess,
      SwitchProcess,
   }
  public static callType currentCall;

  public static ArrayList<Object> parameters = new ArrayList<>();

  public static ArrayList<UserLandProcess> userLandProcesses = new ArrayList<>();

  public static Object returnVal;




  public static int createProcess(UserLandProcess userProcess){
      userLandProcesses.add(userProcess);
      currentCall = callType.CreateProcess;
    return 1;
  }

  public static void startUp(UserLandProcess init){
      kernel = new Kernel();
      kernel.start();
      kernel.run();
     int success = 0;
     for (int i = 0; i <2; i++) {
         createProcess(init);
      switchProcess();
     }

     System.out.println(success);
  }


   public static void switchProcess()  {
       UserLandProcess currentProcess = kernel.getCurrentProcess();
       System.out.printf("Is the kernel Stopped:%s \n",kernel.isStopped());
       currentCall = callType.SwitchProcess;
      if (currentProcess != null){
          kernel.start();
          kernel.run();
          System.out.println("Stopping current process");
          kernel.run();
          currentProcess.stop();
       }


       while (returnVal ==null){
          try {
              Thread.sleep(10);
              System.out.println("Waiting for Process");
          }catch (Exception e){
              System.out.println("Error Waiting for Process in OS");
          }
     }

   }

   /*
   /@param parm and array of objects to be replaced to Parameters List
    */
  public static void newParameters(Object []parm){
      parameters.clear();
      parameters.addAll(Arrays.asList(parm));
  }



}
