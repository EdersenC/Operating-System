package UserLand;

import KernalLand.Messaging;
import os.Os;

import java.nio.charset.StandardCharsets;

public class GoodByeWorld extends UserLandProcess{
    private String message = "";
    int what = 0;
    private boolean allowMessaging;
    /**
     * This is the constructor of the GoodByeWorld class
     * @param allowMessaging the message to be displayed
     */
    public GoodByeWorld(boolean allowMessaging){
        this.allowMessaging = allowMessaging;
    }

    public GoodByeWorld(){
        this.message = "GoodBye World";
    }


    /**
     * This is the main method of the GoodByeWorld class
     * runs the GoodByeWorld process
     */
    @Override
    public void main() {
        boolean sentMessage = false;
        while (true){
            System.out.println(message);
            if (allowMessaging) {
                if (!sentMessage) {
                    if (testReceiveMessage()) {
                        sentMessage = true;
                    }
                } else {
                    testSendMessage();
                    sentMessage = false;
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
    public void testSendMessage(){
        String messageString = "2";
        Messaging message = new Messaging(
                Os.getPid(),
                Os.getPidByName(HelloWorld.class.getSimpleName()),
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
    public boolean testReceiveMessage(){
        Messaging received;
        if ((received = waitForMessage())==null){
            System.out.println("Waiting for Message");
            return false;
        }
        System.out.printf("\n Pong From 2 to: %s what: %s\n ",
                new String(received.data),
                received.what
        );
        return true;
    }



}

