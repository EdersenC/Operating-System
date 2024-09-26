package KernalLand;

import UserLand.UserLandProcess;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

public class Scheduler {

    private final LinkedList<PCB> realTime = new LinkedList<>();
    private final LinkedList<PCB> interactive = new LinkedList<>();
    private final LinkedList<PCB> background = new LinkedList<>();
    private final LinkedList<PCB> sleepers = new LinkedList<>();
    private Timer timer = new Timer();
    private TimerTask task;
    private final Clock clock = Clock.systemDefaultZone();
    public  PCB currentUserProcess = null;


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
  public int createProcess(UserLandProcess userProcess,PCB.Priority priority){
      PCB pcb = new PCB(userProcess, priority);
      getQueue(pcb.currentPriority).add(pcb);
      if (currentUserProcess == null){
              switchProcess();
      }
      return pcb.PID;
  }



  public LinkedList<PCB> getQueue(PCB.Priority priority){
     switch (priority){
         case RealTime -> {
             return realTime;
         }
         case Interactive -> {
             return interactive;
         }
         case Background -> {
             return background;
         }
     }
     return null;
  }





  /**
   * This method is used to switch the current process
   * to the next process in the list
   */
  public void switchProcess(){
      System.out.println("amount of processes woken up: "+wakeUp());
       if (currentUserProcess!=null){
           if (!currentUserProcess.isDone()){
               getQueue(currentUserProcess.currentPriority)
                       .add(currentUserProcess);
           }
       }
       LinkedList<PCB> nextQueue = queuePicker();
       if (!nextQueue.isEmpty())
           currentUserProcess = nextQueue.pop();
  }

  public LinkedList<PCB> queuePicker(){
      Random bot = new Random();
      int realTimeInt = 5;
      int backGroundInt = 9;
      int bounds;
     if (realTime.isEmpty()) {
         bounds = 4;
         backGroundInt = 3;
         realTimeInt = -1;
     } else if (interactive.isEmpty()) {
         return background;
     } else {
         bounds = 10;
     }
      int randomInt = bot.nextInt(bounds);
      if (randomInt<=realTimeInt){
          return realTime;
      }
      if (randomInt==backGroundInt) {
          return background;
      }
      return interactive;
  }



/**
 * This function puts a process that wants to sleep into a sleeping queue
 * and dose so in a way were the first to wake up are inserted at the start of the list
 * @param milliseconds the amount of time the process needs to sleep for
 */
  public void sleep(int milliseconds){
      int wakeUpTime = (int) clock.millis()+milliseconds;
      currentUserProcess.wakeUpTime = wakeUpTime;
      if (sleepers.isEmpty()){
          sleepers.add(currentUserProcess);
          return;
      }
      // compares wait times so waking up process is more efficient
      for (int i = 0; i < sleepers.size() ; i++) {
          if (sleepers.get(i).wakeUpTime >= wakeUpTime){
              sleepers.add(i,currentUserProcess);
              switchProcess();
              return;
          }
      }
  }

 /**
  * this function is used in order to wake up a sleeping processes
  * this checks from the start of the list and wakes up any process that have
  * a time less than or equal to the current clock.
  * if this boolean expression is violated at anypoint we return out the loop
  * waking a process means we have added it back to its priority queue.
  * @return the amount of processes that have been woken
  */
  public int wakeUp(){
      int woken = 0;
      for (int i = 0; i < sleepers.size() ; i++) {
          if (sleepers.get(i).wakeUpTime <= (int) clock.millis()){
              PCB waking = sleepers.remove(i);
              getQueue(waking.currentPriority).add(waking);
              woken++;
          }else {
              return woken;
          }
      }
      return woken;
  }


}
