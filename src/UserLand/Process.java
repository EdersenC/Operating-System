package UserLand;

import java.util.concurrent.Semaphore;
import os.Os;

public abstract class Process implements Runnable {

    private Boolean isExpired = false;
    private final Semaphore semaphore = new Semaphore(0);
    private final Thread  thread = new Thread(this);
    public final int id = (int) thread.threadId();

    /**
     * This method is used to run the main process
     */
    public abstract void main();

    /**
     * This method is used to check if the process is stopped
     * the process is stopped if the semaphore has no permits
     * @return Boolean
     */
    public Boolean isStopped(){
        return semaphore.availablePermits() <=0;
    }

    /**
     * This method is used to check if the process is done
     * the process is done if the thread is not alive
     * @return Boolean
     */
    public Boolean isDone(){
        return !thread.isAlive();
    }

    /**
     * This method is used to start the process/thread
     */
    public void start() {
        semaphore.release();
    }

    /**
     * This method is used to run the thread and acquire a permit from the semaphore
     * calls the process main method
     */
    @Override
    public void run(){
        try {
            semaphore.acquire();
            main();
        }catch (InterruptedException e){
            System.out.printf("Error While trying to acquire permit in run: %s",e);
        }
    }

    /**
     * This method is used to stop the process
     * It acquires a permit from the semaphore
     */
    public void stop() {
        try {
            semaphore.acquire();
            System.out.println("Stopping Process");
        }catch (InterruptedException e){
            System.out.printf("Error While trying to acquire permit in stop: %s",e);
        }
    }

    /**
     * This method is used to request the process to stop
     * It sets the isExpired(quantum) variable to true
     */
    public void requestStop(){
        isExpired = true;
    }

    /**
     * This method is used to cooperate with other processes
     * It sets the isExpired(quantum) variable to false
     * It calls the switchProcess method in the Os class allowing other processes to run
     */
    public void cooperate(){
        if(isExpired){
            isExpired = false;
            System.out.printf("Cooperating with other process: isExpired:%s, Permits: %s \n",isExpired,semaphore.availablePermits());
            Os.switchProcess();
        }
    }


}
