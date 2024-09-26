package UserLand;

import KernalLand.PCB;

public class HelloWorld extends UserLandProcess {

    /**
     * This is the main method of the HelloWorld class
     * runs the HelloWorld process
     */
    @Override
    public void main() {
       while (true){
           System.out.println("Hello World");
           cooperate();
           try {
           Thread.sleep(50);
           }catch (Exception e){
           }
       }
    }


}
