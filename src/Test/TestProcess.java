package Test;


import KernalLand.PCB;
import UserLand.UserLandProcess;

public class TestProcess extends UserLandProcess {
    private String message = "";
    public int taskCount = 0;
    public Boolean exits;
    public boolean exited = false;
    public PCB.Priority requestedPriority;

    /**
     * This is the constructor of the GoodByeWorld class
     * @param message the message to be displayed
     */
    public TestProcess (String message,int sleepTime, Boolean exits, PCB.Priority requestedPriority){
        this.message = message;
        this.sleepTime = sleepTime;
        this.exits = exits;
        this.requestedPriority = requestedPriority;
    }

    /**
     * This is the main method of the GoodByeWorld class
     * runs the GoodByeWorld process
     */
    @Override
    public void main() {
        while (true){
            try {
                Thread.sleep(30);
            }catch (Exception e){
            }
            System.out.printf("%s process: %s, Available permits: %s Count: %s \n ",
                    message,this,semaphore.availablePermits(),taskCount);
            taskCount++;
            testSleep();
            testExit();
        }
    }

   public void testSleep(){
       if (taskCount > 10 && sleepTime >0){
           sleep(sleepTime);
           taskCount = 0;
       }
   }


   public void testExit(){
       if (taskCount >100 && exits) {
           taskCount = 0;
           exited = true;
           Exit();
       }
       if (!exited){
           cooperate();
       }
   }


}
