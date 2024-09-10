package os;

import KernalLand.Kernel;
import UserLand.IdleProcess;
import UserLand.Init;
import UserLand.UserLandProcess;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Os {

   private static Kernel kernel;
   public static callType currentCall;
   public enum callType{
       CreateProcess,
       SwitchProcess,
       Sleep,
   }

  public static ArrayList<Object> parameters = new ArrayList<>();
  public static Object returnVal;





/**
 *  Creates a new process and adds it to the userLandProcesses
 *  Which will be checked by the kernel that removes the process from the list
 * and adds it to the scheduler
 * @param userProcess the process to be created
 * @return the pid of the process
 */
  public static int createProcess(UserLandProcess userProcess){
      //kernel.createProcess(userProcess);
      currentCall = callType.CreateProcess;
      parameters.add(userProcess);
    return userProcess.id;
  }


/**
 * Initializes a new Kernel and starts it
 * @param init this takes in a init process that handles startUp useLand process
 */
  public static void startUp(UserLandProcess init){
      kernel = new Kernel();
     int success = 0;
     for (int i = 0; i <1; i++) {
         createProcess(init);
         createProcess(new IdleProcess());
         try {
             Thread.sleep(50);
         }catch (Exception e){
         }
     }
      kernel.start();
      kernel.run();
      switchProcess();

     System.out.println(success);
  }


  /**
   * Stops the current userLand process and restarts kernel
   * <p>
   * Loops/sleeps until the kernel sets a return value
   */
   public static void switchProcess()  {
       UserLandProcess currentProcess = kernel.getCurrentProcess();
       System.out.printf("Is the kernel Stopped:%s \n",kernel.isStopped());
       if (parameters.isEmpty()){
           currentCall = callType.SwitchProcess;
       }else {
           currentCall = callType.CreateProcess;
       }
      if (currentProcess != null){
          kernel.start();
          kernel.run();
          System.out.println("Stopping current process");
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


   public void sleep(int milliseconds){
       try {
           Thread.sleep(10);
           System.out.println("Waiting for Process");
       }catch (Exception e){
           System.out.println("Error Waiting for Process in OS");
       }
   }



}
