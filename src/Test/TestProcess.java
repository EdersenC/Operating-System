package Test;


import KernalLand.Messaging;
import KernalLand.PCB;
import UserLand.UserLandProcess;
import os.Os;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TestProcess extends UserLandProcess {
    public String message = "";
    public int taskCount = 0;
    public boolean exits;
    public PCB.Priority requestedPriority;
    public boolean doingOperations = false;

    /**
     * This is the constructor of the GoodByeWorld class
     * @param message the message to be displayed
     */
    public TestProcess (String message,int sleepTime, Boolean exits,
                        PCB.Priority requestedPriority
    ){
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
            System.out.println(testCrude("file hello.txt",
                    "jojos adventures"));
            System.out.printf("%s process: %s, Count: %s \n ",message,this,taskCount);
            taskCount++;
            testSleep();
            testExit();
        }
    }

   public void testSleep(){
       if (taskCount > 100 && sleepTime >0){
           sleep(sleepTime);
           taskCount = 0;
       }
   }






  public String testCrude(String file, String data){
        doingOperations = true;
        byte[] dataBytes = data.getBytes();
        int openedFile = Os.open(file);
        Os.write(openedFile,dataBytes);
        byte[] bytesRead = Os.read(openedFile,100);
      Os.close(openedFile);
        doingOperations = false;
        String convertedString = new String(bytesRead, StandardCharsets.UTF_8);
        return convertedString;
  }


   public void testExit(){
       if (taskCount >100 && exits) {
           taskCount = 0;
           Exit();
       }
       if (!doingOperations){
           cooperate();
       }
   }


}
