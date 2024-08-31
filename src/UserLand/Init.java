package UserLand;

import os.Os;

public class Init extends UserLandProcess {

  public Init(HelloWorld hello, GoodByeWorld bye){
      Os.createProcess(hello);
      Os.createProcess(bye);
  }

}
