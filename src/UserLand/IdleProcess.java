package UserLand;
import KernalLand.PCB;

public class IdleProcess extends UserLandProcess{

   private final String message ="Idle";

    /**
     * This is the main method of the IdleProcess class
     * runs the idle process
     */
    @Override
    public void main()  {
        while (true){
            System.out.printf("%s Idle process running \n ",message);
            cooperate();
            try {
                Thread.sleep(50);
            }catch (Exception ignored){
            };
        }
    }



}
