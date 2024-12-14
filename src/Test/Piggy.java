package Test;

import Hardware.Hardware;
import KernalLand.PCB;

public class Piggy extends TestProcess {
    public Piggy(String message, int maxAllocations, PCB.Priority requestedPriority, boolean exits) {
        super(message, maxAllocations, requestedPriority, exits);
    }

    /**
     * This is the main method of the GoodByeWorld class
     * runs the GoodByeWorld process
     */
    @Override
    public void main() {
        while (true){
            if (doingOperations){
                // System.out.println(testCrude("file Test.txt", """the Great danny phantom"""));
                doingOperations = false;
                //testStoryWriter(100);
                testWrite(maxAllocations);
            }
            System.out.println("Oink Oink");
            cooperate();
            try {
                Thread.sleep(50);
            }catch (Exception e){
            }
            taskCount++;
//            testSleep();
//            testExit();
        }
    }







}
