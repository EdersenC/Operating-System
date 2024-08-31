package KernalLand;
import UserLand.Process;
import UserLand.UserLandProcess;
import os.Os;

import java.util.ArrayList;

public class Kernel extends Process{

    private final Scheduler scheduler = new Scheduler();

    public UserLandProcess getCurrentProcess(){
        return scheduler.currentUserProcess;
    }



    @Override
    public void main(){
        while (true) {
            System.out.println("fsdfsdfsf");
            switch (Os.currentCall) {
                case CreateProcess -> {
                        while (!Os.userLandProcesses.isEmpty())
                            scheduler.createProcess(Os.userLandProcesses.removeFirst());
                }
                case SwitchProcess ->{
                    scheduler.switchProcess();
                }
            }
            if(scheduler.currentUserProcess !=null){
                scheduler.currentUserProcess.start();
                scheduler.currentUserProcess.run();
            }
            System.out.println("Kernel thread stopped");
            Os.returnVal = new Object();
            stop();
        }
    }




}
