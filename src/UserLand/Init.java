package UserLand;

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
        Os.createProcess(hola);
        Os.createProcess(bye);
        Os.createProcess(new GoodByeWorld());
    }
    /**
     * This is the main method that will be called when the process is created
     */
    @Override
    public void main(){
        if (!initialized){
            init();
            initialized = true;
        }


        while (true) {
            cooperate();
            try {
                Thread.sleep(50);
            } catch (Exception e) {
            }
        }
    }


}
