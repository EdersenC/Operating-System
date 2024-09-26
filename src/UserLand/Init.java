package UserLand;

import KernalLand.PCB;
import os.Os;

public class Init extends UserLandProcess {

    private HelloWorld hola;
    private GoodByeWorld bye;
    private Boolean initialized = false;

   /**
    * @param hello a new hello world process
    * @param bye a new bye world process
    */
  public Init(HelloWorld hello, GoodByeWorld bye){
      this.hola = hello;
      this.bye = bye;
  }


    /**
     * Processes that run at startUp
     */
    public void init(){
        Os.createProcess(hola, PCB.Priority.RealTime);
        Os.createProcess(new GoodByeWorld());
    }
    /**
     * This is the main method that will be called when the process is created
     */
    @Override
    public void main(){
        if (!initialized){
            initialized = true;
            init();
        }

        while (true) {
            try {
                Thread.sleep(50);
                cooperate();
            } catch (Exception e) {
            }
        }
    }


}
