package UserLand;

import java.util.concurrent.Semaphore;
import os.Os;

public abstract class Process implements Runnable {

    private Boolean isExpired = false;
    private final Semaphore semaphore = new Semaphore(0);
    private final Thread  thread = new Thread();

    public abstract void main();

    public Boolean isStopped(){
        return semaphore.availablePermits() <=0;
    }

    public Boolean isDone(){
        return !thread.isAlive();
    }

    public void start() {
        semaphore.release();
    }

    @Override
    public void run(){
        try {
            semaphore.acquire();
            main();
        }catch (InterruptedException e){
            System.out.printf("Error While trying to acquire permit in run: %s",e);
        }
    }


    public void stop() {
        try {
            semaphore.acquire();
            System.out.println("Stopping Process");
        }catch (InterruptedException e){
            System.out.printf("Error While trying to acquire permit in stop: %s",e);
        }
    }

    public void requestStop(){
        isExpired = true;
    }

    public void cooperate(){
        if(isExpired){
            isExpired = false;
            System.out.printf("Cooperating with other process: isExpired:%s, Permits: %s \n",isExpired,semaphore.availablePermits());
            Os.switchProcess();
        }
    }


}
