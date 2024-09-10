package UserLand;

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
            cooperate();
            try {
            Thread.sleep(60);
            }catch (Exception e){
        }
        }
    }
}

