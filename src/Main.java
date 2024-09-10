import UserLand.GoodByeWorld;
import UserLand.HelloWorld;
import UserLand.Init;
import UserLand.UserLandProcess;
import os.Os;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {


    public static void main(String[] args) {
        Init init = new Init(new HelloWorld(),new GoodByeWorld("Chicken and rice"));
        Os.startUp(init);
    }
}