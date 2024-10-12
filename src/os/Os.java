package os;

import KernalLand.Kernel;
import KernalLand.PCB;
import UserLand.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Os {

   private static Kernel kernel;
   public static callType currentCall;
   public enum callType{
       Normal,
       CreateProcess,
       SwitchProcess,
       Sleep,
       Exit,
       Halt,
       Proceed
   }

  public static ArrayList<Object> parameters = new ArrayList<>();
  public static Object returnVal;


/**
 * Initializes a new Kernel and starts it
 * @param init this takes in a init process that handles startUp useLand process
 */
  public static void startUp(UserLandProcess init){
      kernel = new Kernel();
      Init init1 = (Init) init;
     for (int i = 0; i <1; i++) {
      createProcess(init1);
     }
      while (!init1.initialized){
          try {
              Thread.sleep(50);
          }catch (Exception e){
          }
      }
      createProcess(new IdleProcess(),PCB.Priority.Background);
  }

    private static boolean invokeKernel(callType call){

        if (call == callType.Halt)
            return false;

        currentCall = call;
        kernel.start();

        PCB currentProcess = kernel.getCurrentProcess();
        if (currentProcess!= null) {
            currentProcess.stop();
        }
        while (returnVal ==null){
            try {
                Thread.sleep(50);
                System.out.println("Waiting for Process");
            }catch (Exception e){
            }
        }

        return false;
    }


    /**
     *  Creates a new process and adds it to the userLandProcesses
     *  Which will be checked by the kernel that removes the process from the list
     *  adds the initial priority status of interactive.
     * and adds it to the scheduler
     * @param userProcess the process to be created
     * @return the pid of the process
     */
    public static int createProcess(UserLandProcess userProcess){
        returnVal = null;
        parameters.clear();
        parameters.add(userProcess);
        parameters.add(PCB.Priority.Interactive);
        invokeKernel(callType.CreateProcess);
        return userProcess.id;
    }

    /**
     *  Creates a new process and adds it to the userLandProcesses
     *  Which will be checked by the kernel that removes the process from the list
     * and adds it to the scheduler
     * @param userProcess the process to be created
     * @param priority the Starting Priority of a pcb
     * @return the pid of the process
     */
    public static int createProcess(UserLandProcess userProcess,PCB.Priority priority ){
        returnVal = null;
        parameters.clear();
        parameters.add(userProcess);
        parameters.add(priority);
        invokeKernel(callType.CreateProcess);
        return userProcess.id;
    }

  /**
   * Stops the current userLand process and restarts kernel
   * <p>
   * Loops/sleeps until the kernel sets a return value
   */
   public static void switchProcess(){
       returnVal = null;
       parameters.clear();
      invokeKernel(callType.SwitchProcess);
   }

  public static void exit(){
      returnVal = null;
      parameters.clear();
      invokeKernel(callType.Exit);
  }

  public static void proceed(){
      returnVal = null;
      parameters.clear();
      invokeKernel(callType.Proceed);
  }


  public static void halt(){
       kernel.getCurrentProcess().stop();
      returnVal = null;
      parameters.clear();
      invokeKernel(callType.Halt);
    }

   public static void sleep(int milliseconds){
       returnVal = null;
       parameters.clear();
       parameters.add(milliseconds);
       invokeKernel(callType.Sleep);
   }



}
