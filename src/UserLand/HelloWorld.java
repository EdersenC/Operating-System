package UserLand;

import KernalLand.Messaging;
import os.Os;

import java.nio.charset.StandardCharsets;

public class HelloWorld extends UserLandProcess {
    int what = 0;
    /**
     * This is the main method of the HelloWorld class
     * runs the HelloWorld process
     */
    @Override
    public void main() {
         boolean sentMessage = true;
           while (true){
               System.out.println("Hello World");
               if (sentMessage){
                  sendMessage();
                  sentMessage =false;
               }else {
                   if (receiveMessage()){
                       sentMessage =true;
                   }
               }
               cooperate();
               try {
               Thread.sleep(50);
               }catch (Exception e){
               }
           }
    }


//    public void pingPong(){
//        if (!sentMessage) {
//            if (receiveMessage()) {
//                sentMessage = true;
//            }
//        }else {
//            sendMessage();
//            sentMessage =false;
//        }
//    }


    public void sendMessage(){
       String messageString = "1";
       Messaging message = new Messaging(
               Os.getPid(),
               Os.getPidByName(GoodByeWorld.class.getSimpleName()),
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
       System.out.printf("\n Ping From 1 to: %s what: %s\n",
               new String(received.data),
               received.what
       );
       return true;
   }


}
