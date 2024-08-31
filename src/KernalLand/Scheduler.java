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


   public Scheduler(){
       long interruptTime = 250;
       task = new TimerTask() {
           public void run() {
               System.out.println("In the interuot dude");
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




  public int createProcess(UserLandProcess userProcess){
       int success = userProcessList.add(userProcess)? 0: 1;
      if (currentUserProcess == null){
              switchProcess();
      }
     return success;
  }

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
