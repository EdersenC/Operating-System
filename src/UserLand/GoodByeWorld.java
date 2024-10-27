package UserLand;

import KernalLand.Messaging;
import KernalLand.PCB;
import os.Os;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class GoodByeWorld extends UserLandProcess{
    private String message = "";
    int what = 0;
    /**
     * This is the constructor of the GoodByeWorld class
     * @param message the message to be displayed
     */
    public GoodByeWorld(String message){
        this.message = message;
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
           if (!sentMessage) {
               if (receiveMessage()) {
                   sentMessage = true;
               }
           }else {
               sendMessage();
               sentMessage = false;
           }

            cooperate();
            try {
            Thread.sleep(50);
            }catch (Exception e){

            }
        }


    }



//   public void pingPong(){
//       if (!sentMessage) {
//           if (receiveMessage()) {
//               sentMessage = true;
//           }
//       }else {
//          sendMessage();
//          sentMessage =false;
//       }
//   }

    public void sendMessage(){
        String messageString = "2";
        Messaging message = new Messaging(
                Os.getPid(),
                Os.getPidByName(HelloWorld.class.getSimpleName()),
                what++,
                messageString.getBytes(StandardCharsets.UTF_8)
        );
        Os.sendMessage(message);
    }

    public boolean receiveMessage(){
        Messaging received;
        if ((received = Os.waitForMessage())==null){
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

