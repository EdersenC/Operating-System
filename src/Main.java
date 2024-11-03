import UserLand.GoodByeWorld;
import UserLand.HelloWorld;
import UserLand.Init;
import UserLand.UserLandProcess;
import os.Os;

import java.util.ArrayList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {


    public static void main(String[] args) {
        ArrayList<UserLandProcess> starUpProcesses = new ArrayList<>();
        starUpProcesses.add(new HelloWorld(false));
        starUpProcesses.add(new GoodByeWorld(false));
        Init init = new Init(starUpProcesses);

        Os.startUp(init);
    }
}