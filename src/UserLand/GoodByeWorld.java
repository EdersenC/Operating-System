package UserLand;

public class GoodByeWorld extends UserLandProcess{

    @Override
    public void main() {
        while (true){
            System.out.println("GoodBye World");
            cooperate();
            try {
            Thread.sleep(60);
            }catch (Exception e){
        }
        }
    }
}

