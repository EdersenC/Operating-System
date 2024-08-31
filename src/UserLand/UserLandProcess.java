package UserLand;

public class UserLandProcess extends Process{



    public void idleProcess()  {
       while (true){
           cooperate();
           try {
               Thread.sleep(60);
           }catch (Exception e){

           };
       }
    }

    public void main() {
        System.out.println("soda");
    }


}
