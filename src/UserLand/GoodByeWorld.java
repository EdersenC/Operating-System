package UserLand;

import KernalLand.PCB;
import os.Os;

import java.util.Objects;

public class GoodByeWorld extends UserLandProcess{
    private String message = "";

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

        while (true){
            System.out.println(message);
            try {
            Thread.sleep(50);
            }catch (Exception e){
        }
            cooperate();
        }
    }
}

