package KernalLand;
import UserLand.Process;
import UserLand.UserLandProcess;
import os.Os;

import java.util.ArrayList;

public class Kernel extends Process{

    private final Scheduler scheduler = new Scheduler();

    /**
     * This method is used to create a new process
     * @return the current process from the Scheduler
     */
    public UserLandProcess getCurrentProcess(){
        return scheduler.currentUserProcess;
    }


    /**
     * This method is used to start the kernel
     */
    @Override
    public void main(){
        System.out.println("Starting Kernel");
        while (true) {
            switch (Os.currentCall) {
                case CreateProcess -> {
                    while (!Os.parameters.isEmpty()) {
                        Os.returnVal = scheduler.createProcess((UserLandProcess) Os.parameters.removeFirst());

                    }
                }
                case SwitchProcess ->{
                    scheduler.switchProcess();
                }
                case Sleep->{
                    scheduler.sleep();
                }
            }
            System.out.println(Os.parameters);
            System.out.println(Os.currentCall);
            if(scheduler.currentUserProcess !=null){
                scheduler.currentUserProcess.start();
                scheduler.currentUserProcess.run();
            }
            System.out.println("Kernel thread stopped");
            stop();
        }
    }


//    public void createProcess(UserLandProcess process){
//        scheduler.createProcess(process);
//    }


}
