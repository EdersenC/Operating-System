package UserLand;

import KernalLand.PCB;
import Test.TestProcess;
import os.Os;

import java.util.ArrayList;

public class Init extends UserLandProcess {

    public boolean initialized = false;
    private final ArrayList<UserLandProcess> starUpProcesses;
    private boolean testMode = false;

   /**
    *
    */
  public Init(ArrayList<UserLandProcess> starUpProcesses){
   this.starUpProcesses = starUpProcesses;
  }

  public Init(ArrayList<UserLandProcess> starUpProcesses,boolean testMode){
   this.starUpProcesses = starUpProcesses;
   this.testMode = testMode;
  }


    /**
     * Processes that run at startUp
     */
    public void init(){
        if (!starUpProcesses.isEmpty()) {
            if(testMode){
                TestProcess process = (TestProcess ) starUpProcesses.removeFirst();
                Os.createProcess(process, process.requestedPriority);
                System.out.printf("%s process: %s, Creating Process: %s \n ",
                        message,this,process);
            }else {
                UserLandProcess process = starUpProcesses.removeFirst();
                Os.createProcess(process);
                System.out.printf("%s process: %s, Creating Process: %s \n ",
                        message,this,process);
            }
        }else {
            initialized = true;
            Exit();
        }
    }
    public String message = "Init";
    /**
     * This is the main method that will be called when the process is created
     */
    @Override
    public void main(){
        while (true) {
            init();
            try {
                Thread.sleep(50);
            } catch (Exception e) {
            }
        }
    }


}
