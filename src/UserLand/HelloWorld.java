package UserLand;

import KernalLand.Messaging;
import os.Os;

import java.nio.charset.StandardCharsets;

public class HelloWorld extends UserLandProcess {
    int what = 0;
    private boolean allowMessaging;

    public  HelloWorld(boolean allowMessaging){
        this.allowMessaging = allowMessaging;
    }


    /**
     * This is the main method of the HelloWorld class
     * runs the HelloWorld process
     */
    @Override
    public void main() {
         boolean sentMessage = true;
           while (true){
               System.out.println("Hello World");
              if (allowMessaging){
                  if (sentMessage){
                      sendMessage();
                      sentMessage =false;
                  }else {
                      if (receiveMessage()){
                          sentMessage =true;
                      }
                  }
              }

               cooperate();
               try {
               Thread.sleep(200);
               }catch (Exception e){
               }
           }
    }



    /**
     * Sends a message to the process with the specified identifier.
     * The message is constructed using the current process ID, target process ID,
     * an incrementing message identifier, and a message payload encoded in UTF-8.
     */
    public void sendMessage() {
        String messageString = "1";
        Messaging message = new Messaging(
                Os.getPid(),
                Os.getPidByName(GoodByeWorld.class.getSimpleName()),
                what++,
                messageString.getBytes(StandardCharsets.UTF_8)
        );
        sendMessage(message);
    }

    /**
     * Receives a message if available. If no message is received, it waits and
     * prints a waiting status message. Upon receiving a message, it prints the
     * sender information and the message identifier.
     *
     * @return true if a message was successfully received; false otherwise.
     */
    public boolean receiveMessage() {
        Messaging received;
        if ((received = waitForMessage()) == null) {
            System.out.println("Waiting for Message");
            return false;
        }
        System.out.printf("\n Ping From 1 to: %s what: %s\n",
                new String(received.data),
                received.what
        );
        return true;
    }



}
