package UserLand;

public class IdleProcess extends UserLandProcess{

    /**
     * This is the main method of the IdleProcess class
     * runs the idle process
     */
    @Override
    public void main()  {
        while (true){
            System.out.printf("Idle process Running\n");
            cooperate();
            try {
                Thread.sleep(50);
            }catch (Exception ignored){

            };
        }
    }



}
