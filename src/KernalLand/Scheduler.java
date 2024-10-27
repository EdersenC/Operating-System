package KernalLand;

import UserLand.Init;
import UserLand.UserLandProcess;
import os.Os;

import java.time.Clock;
import java.util.*;

public class Scheduler {

    private final LinkedList<PCB> realTime = new LinkedList<>();
    private final LinkedList<PCB> interactive = new LinkedList<>();
    private final LinkedList<PCB> background = new LinkedList<>();
    private final LinkedList<PCB> sleepers = new LinkedList<>();
    private final LinkedList<PCB> waitingForMessage = new LinkedList<>();
    private final HashMap<Integer,PCB> processList = new HashMap<Integer, PCB>();
    private Timer timer = new Timer();
    private TimerTask task;
    private final Clock clock = Clock.systemDefaultZone();
    public  PCB currentUserProcess = null;
    private final long interruptTime = 250;

    /**
     * This is the constructor for the Scheduler class
     * It creates a TimerTask that interrupts the current process
     */
   public Scheduler(){
       task = new TimerTask() {
           int pastId = 0;
           public void run() {
                   if (currentUserProcess == null) {
                       switchProcess();
                       System.out.println("Current userLandProcess is null");
                   } else {
                       currentUserProcess.requestStop();
                       if (pastId != currentUserProcess.PID){
                           currentUserProcess.timedOut = 0;
                           pastId = currentUserProcess.PID;
                       }
                   }
               System.out.println("Timer Interrupt");
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
      processList.put(pcb.PID,pcb);
      if (currentUserProcess == null){
              switchProcess();
      }
      return pcb.PID;
  }


    /**
     * Returns the queue corresponding to the given priority.
     * This method retrieves the appropriate process queue based on the
     * provided priority value. The available priorities are RealTime,
     * Interactive, and Background.
     *
     * @param priority The priority of the process (RealTime, Interactive, or Background).
     * @return A LinkedList of PCB that corresponds to the given priority.
     *         Returns null if the priority does not match any predefined categories.
     */
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
      wakeUp();
      wakeUpMessageWaiters();
       if (currentUserProcess!=null){
           if (!currentUserProcess.isDone()){
               getQueue(currentUserProcess.currentPriority)
                       .add(currentUserProcess);
           }
       }
       LinkedList<PCB> nextQueue = queuePicker();
       if (!nextQueue.isEmpty()) {
           currentUserProcess = nextQueue.pop();
       }
       assert  currentUserProcess!= null;
  }





    /**
     * Selects a queue based on random selection and process availability.
     * This method uses a random number generator to pick one of three process queues:
     * realTime, background, or interactive. The selection logic adjusts based on
     * whether the realTime or background queues are empty.
     *
     * @return A LinkedList of PCB representing the chosen queue (realTime, background, or interactive).
     */
    public LinkedList<PCB> queuePicker() {
        Random bot = new Random();
        int realTimeInt = 5;
        int backGroundInt = 9;
        int bounds;

        // Adjust bounds and selection values if realTime queue is empty
        if (realTime.isEmpty()) {
            bounds = 4;
            backGroundInt = 3;
            realTimeInt = -1;
        } else {
            bounds = 10;
        }

        int randomInt = bot.nextInt(bounds);

        // Return realTime queue if the random number is within its range
        if (randomInt <= realTimeInt) {
            return realTime;
        }

        boolean backgroundChosen = randomInt == backGroundInt;
        if ((backgroundChosen && !background.isEmpty()) || interactive.isEmpty()) {
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
      currentUserProcess.setSleeping();
      if (sleepers.isEmpty()){
          sleepers.add(currentUserProcess);
          currentUserProcess = null;
          switchProcess();
          return;
      }
      // compares wait times so waking up process is more efficient
      for (int i = 0; i < sleepers.size() ; i++) {
          if (sleepers.get(i).wakeUpTime >= wakeUpTime){
              sleepers.add(i,currentUserProcess);
              currentUserProcess = null;
              switchProcess();
              return;
          }
      }
  }

  public void exit(){
      currentUserProcess.process.Exited = true;
      processList.remove(currentUserProcess.PID);
      currentUserProcess = null;
      switchProcess();
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
              waking.setSleeping();
              woken++;
          }else {
              return woken;
          }
      }
      return woken;
  }

    /**
     * Halts the current process by stopping the active timer and task,
     * and switching to the next process.
     * This method stops the current user process, purges any remaining
     * tasks in the timer, cancels the current task, and then switches
     * to the next available process.
     */
  public void Halt(){
      timer.purge();
      task.cancel();
      currentUserProcess.stop();
      switchProcess();
  }



    /**
     * Halts the current process by stopping the active timer and task,
     * and switching to the next process.

     * This method stops the current user process, purges any remaining
     * tasks in the timer, cancels the current task, and then switches
     * to the next available process.
     */
  public void Proceed(){
      if (currentUserProcess!=null)
          currentUserProcess.start();
  }


    public int getPid() {
        return currentUserProcess.PID;
    }

    public int getPidByName(String name) {
      for (PCB pbg: processList.values()){
          if (pbg.name.equals(name))
              return pbg.PID;
      }
      return -1;
    }



   public int wakeUpMessageWaiters(){
      int woken = 0;
       for (int i = 0; i < waitingForMessage.size(); i++) {
           if (waitingForMessage.get(i).messages.isEmpty())
               continue;
           PCB waiting = waitingForMessage.remove(i);
           getQueue(waiting.currentPriority).add(waiting);
       }
       return woken;
   }


    public Messaging waitForMessage() {
      if (currentUserProcess.messages.isEmpty()){
         waitingForMessage.add(currentUserProcess);
          currentUserProcess = null;
          switchProcess();
          return null;
      }
        return currentUserProcess.messages.removeFirst();
    }

    public void sendMessage(Messaging message) {
      Messaging copy = new Messaging(message);
      copy.senderPid = currentUserProcess.PID;
      PCB target = processList.getOrDefault(copy.targetPid,null);
      if (target==null){
         System.out.println("Could not find process with that PID");
         return;
      }
      target.messages.add(copy);
    }
}
