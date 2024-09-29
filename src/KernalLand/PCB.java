package KernalLand;

import UserLand.Process;
import UserLand.UserLandProcess;
import os.Os;

public class PCB {
    public UserLandProcess process;
    public Priority currentPriority;

    private int sleepTime = 500;
    public int wakeUpTime;

    public boolean Exited = false;

    public int timedOut = 0;

    public int PID;
    public int nextPID;

    public enum Priority{
        RealTime,
        Interactive,
        Background,
    }

    public PCB(UserLandProcess process,Priority priority){
        this.process = process;
        this.currentPriority = priority;
        this.process.PCBSetPriority = priority;
        PID = process.id;
    }


    public void stop(){
            while (!process.isStopped()) {
                try {
                    Thread.sleep(10);
                    process.stop();
                } catch (Exception ignored) {
                }
            }
            process.stop();
    }

    public Boolean isDone(){
       return process.isDone();
    }

    public void start(){
            process.start();
    }



    private void managePriority(boolean promote){
       switch(currentPriority){
           case RealTime ->{
              currentPriority = (promote)?
                      Priority.RealTime: Priority.Interactive;
           }
           case Interactive -> {
               currentPriority = (promote)?
                       Priority.RealTime: Priority.Background;
           }
           case Background -> {
               currentPriority = (promote)?
                       Priority.Interactive: Priority.Background;
           }
       }
    }

    public void requestStop() {
            process.requestStop();
            timedOut++;
            if (timedOut >= 5) {
                managePriority(false);
                process.PCBSetPriority = currentPriority;
                timedOut = 0;
            }
    }

}
