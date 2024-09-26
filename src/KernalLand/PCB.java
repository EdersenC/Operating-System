package KernalLand;

import UserLand.Process;
import UserLand.UserLandProcess;
import os.Os;

public class PCB {
    public UserLandProcess process;
    public Priority currentPriority;

    private int sleepTime = 500;
    public int wakeUpTime;

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
        PID = process.id;
    }


    public void stop(){
        while (!process.isStopped()) {
            try {
                process.stop();
                Thread.sleep(50);
            } catch (Exception ignored) {
            }
        }
    }

    public Boolean isDone(){
       return process.isDone();
    }

    public void start(){
        process.start();
    }




    public void requestStop(){
        process.requestStop();
    }

}
