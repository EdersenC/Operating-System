package KernalLand;

import UserLand.UserLandProcess;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class Scheduler {

    private LinkedList<UserLandProcess> userProcessList = new LinkedList<>();
    private Timer timer = new Timer();
    private TimerTask task;
    public  UserLandProcess currentUserProcess = null;


    /**
     * This is the constructor for the Scheduler class
     * It creates a TimerTask that interrupts the current process
     */
   public Scheduler(){
       long interruptTime = 250;
       task = new TimerTask() {
           public void run() {
                   if (currentUserProcess == null) {
                       switchProcess();
                       System.out.println("Current userLandProcess is null");
                   } else {
                       currentUserProcess.requestStop();
                       System.out.println("Timer Interrupt");
                   }

           }
       };

       timer.schedule(task,interruptTime,interruptTime);
   }


     /**
      * This method is used to create a new process and adds it to a list
      * inorder to be scheduled
      * @param userProcess the process to be created
      * @return the pid of the process
      */
  public int createProcess(UserLandProcess userProcess){
       int success = userProcessList.add(userProcess)? 0: 1;
      if (currentUserProcess == null){
              switchProcess();
      }
     return userProcess.id;
  }


  /**
   * This method is used to switch the current process
   * to the next process in the list
   */
  public void switchProcess(){
       if (currentUserProcess !=null){
           if (currentUserProcess.isDone()){
               userProcessList.addLast(currentUserProcess);
           }
       }
       if (!userProcessList.isEmpty())
           currentUserProcess = userProcessList.pop();
  }






}
