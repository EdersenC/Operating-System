package UserLand;

public class HelloWorld extends UserLandProcess {
    @Override
    public void main() {
       while (true){
           System.out.println("Hello World");
           cooperate();
           try {
           Thread.sleep(60);
           }catch (Exception e){

           }
       }
    }


}
