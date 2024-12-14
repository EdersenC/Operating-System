package Test;


import Hardware.Hardware;
import KernalLand.Messaging;
import KernalLand.PCB;
import UserLand.UserLandProcess;
import os.Os;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class TestProcess extends UserLandProcess {
    public String message = "";
    public int taskCount = 0;
    public boolean exits;
    public PCB.Priority requestedPriority;
    public boolean doingOperations = true;
    public  boolean writeTested = false;
    public  int maxAllocations = 0;
    public String story = "";

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
   public TestProcess(String message, int maxAllocations, PCB.Priority requestedPriority,boolean exits){
        this.message = message;
        this.maxAllocations = maxAllocations;
        this.requestedPriority = requestedPriority;
        this.exits = exits;
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
                testPaging();
                doingOperations = false;
                Exit();
            }
            cooperate();
            try {
                Thread.sleep(200);
            }catch (Exception e){
            }
            taskCount++;
//            testSleep();
//            testExit();
        }
    }

   public void testSleep(){
       if (taskCount > 100 && sleepTime >0){
           sleep(sleepTime);
           taskCount = 0;
       }
   }



  public String testCrude(String file, String data){
        byte[] dataBytes = data.getBytes();
        int openedFile = Os.open(file);
        Os.write(openedFile,dataBytes);
        byte[] bytesRead = Os.read(openedFile,dataBytes.length);
        Os.seek(openedFile,dataBytes.length);
        dataBytes = "Dog water".getBytes(StandardCharsets.UTF_8);
        Os.write(openedFile,dataBytes);
      Os.close(openedFile);
        String convertedString = new String(bytesRead, StandardCharsets.UTF_8);
        return convertedString;
  }




  private void testPaging(){
      testWrite(maxAllocations);
      if (!story.isEmpty())
          testStoryWriter(maxAllocations);
      System.out.println(message);
      if (exits)
          Exit();
  }



    /**
     * Tests the functionality of writing bytes to memory and verifying correct storage.
     * Allocates a memory region, writes incremental byte values, and reads back to
     * ensure values are correctly stored.
     */
    public void testWrite(int maxAllocations) {
        if (writeTested) return;

        int virtualAddress = allocate(maxAllocations);
        int offset = 0; // Offset within the allocated memory
        byte compare = 0;

        for (int i = 0; i < maxAllocations*1024; i++) {
            if (i == 40) compare = (byte) i;
            byte g = (byte) i; // Convert loop index to byte for writing
            write(virtualAddress + offset, g);
            byte val = read(virtualAddress + offset);
            if (val != g) {
                System.err.println("Failed Comparison"); // Output if comparison fails
            }
            offset++;
        }
        System.err.println(message + ": Done Reading and Writing");
        // Verify the specific byte at position 40 is stored correctly by comparing with 'compare'
    }


    /**
     * Tests writing a string story to memory by converting it to a byte array,
     * writing the array to a memory location, and reading it back to verify the
     * content integrity.
     */
    public void testStoryWriter(int maxAllocations) {
        if (writeTested) return;
        writeTested = true;

        byte[] messageBytes = story.getBytes(StandardCharsets.UTF_8);
        int address = allocate(maxAllocations-1);

        // Write each byte of the story to consecutive addresses in memory
        for (int i = 0; i < messageBytes.length; i++) {
            write(address + i, messageBytes[i]);
        }

        // Read back the bytes from memory into a new byte array
        byte[] getMessageBytes = new byte[messageBytes.length];
        for (int i = 0; i < messageBytes.length; i++) {
            getMessageBytes[i] = read(address + i);
        }

        String getStringWritten = new String(getMessageBytes, StandardCharsets.UTF_8);

        // Check if the written string matches the original story
        if (getStringWritten.equals(story)) {
            System.out.println(getStringWritten);
            System.err.println("Successfully Read The Written Story"); // Success message
        } else {
            System.err.println("Failed to Read Written Story"); // Failure message
        }

        assert getStringWritten.equals(story); // Assert that the content matches
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
