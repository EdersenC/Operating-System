package os;

import KernalLand.Kernel;
import KernalLand.Messaging;
import KernalLand.PCB;
import UserLand.*;
import jdk.jshell.spi.ExecutionControl;

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
       open,
       close,
       read,
       write,
       seek,
       getPid,
       getPidByName,
       sendMessage,
       waitForMessage,
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
         waitForReturn();
     }
      while (!init1.initialized){
          try {
              Thread.sleep(50);
          }catch (Exception e){
          }
      }
      createProcess(new IdleProcess(),PCB.Priority.Background);
      waitForReturn();
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
        return false;
    }



    private static void waitForReturn(){
        while (returnVal ==null){
            try {
                Thread.sleep(50);
                System.out.println("Waiting for Process");
            }catch (Exception e){
            }
        }
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

   public static int open(String object){
       returnVal = null;
       parameters.clear();
       parameters.add(object);
       System.out.println( currentCall);
       invokeKernel(callType.open);
       System.out.println( currentCall);
      return  (int) returnVal;
   }

   public static void close(int id){
       returnVal = null;
       parameters.clear();
       parameters.add(id);
       invokeKernel(callType.close);
   }

   public static void seek(int id, int to){
       returnVal = null;
       parameters.clear();
       parameters.add(id);
       parameters.add(to);
       invokeKernel(callType.seek);
   }

   public static int write(int id, byte[] data){
       returnVal = null;
       parameters.clear();
       parameters.add(id);
       parameters.add(data);
       invokeKernel(callType.write);
       return (int) returnVal;
   }

   public static byte[] read(int id, int size){
       returnVal = null;
       parameters.clear();
       parameters.add(id);
       parameters.add(size);
       invokeKernel(callType.read);
       return (byte[]) returnVal;
   }

  public static int getPid(){
      returnVal = null;
      parameters.clear();
      invokeKernel(callType.getPid);
       return (int)returnVal;
  }

  public static int getPidByName(String name){
      returnVal = null;
      parameters.clear();
      parameters.add(name);
      invokeKernel(callType.getPidByName);
      return (int)returnVal;
  }

   public static void sendMessage(Messaging kernelMessage){
       returnVal = null;
       parameters.clear();
       parameters.add(kernelMessage);
       invokeKernel(callType.sendMessage);
   }
   public static Messaging waitForMessage(){
       returnVal = null;
       parameters.clear();
       invokeKernel(callType.waitForMessage);
       return (Messaging) returnVal;
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
