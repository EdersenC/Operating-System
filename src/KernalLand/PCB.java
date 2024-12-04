package KernalLand;

import Devices.Device;
import Hardware.Hardware;
import UserLand.Process;
import UserLand.UserLandProcess;
import os.Os;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;

public class PCB {
    public UserLandProcess process;
    public Priority currentPriority;
    public String name;


    public boolean Sleeping;
    public int wakeUpTime;

    public int timedOut = 0;

    public int PID;
    public int nextPID;

    public final int[] idTranslator = new int[10];
    public final LinkedList<Messaging> messages =  new LinkedList<>();
    public final VirtualToPhysicalMapping[] physicalMappings = new VirtualToPhysicalMapping[100];



    public enum Priority{
        RealTime,
        Interactive,
        Background,
    }

    public PCB(UserLandProcess process,Priority priority){
        this.process = process;
        this.currentPriority = priority;
        this.process.PCBSetPriority = priority;
        PID = process.id;
        this.name = process.getClass().getSimpleName();
        Arrays.fill(idTranslator,-1);
//        Arrays.fill(physicalMappings,new VirtualToPhysicalMapping());
    }



    public void stop(){
            while (!process.isStopped()) {
                    process.stop();
//                System.out.println("Stopping Process: "+process);
            }
            process.stop();
    }

    public Boolean isDone(){
       return process.isDone();
    }

    public void start(){
//        System.out.println("Starting process: "+process);
            process.start();
    }

    public void setSleeping(){
        if (Sleeping){
            Sleeping = false;
            process.Sleeping = false;
            return;
        }
        Sleeping = true;
        process.Sleeping = true;
    }

    private void managePriority(boolean promote){
       switch(currentPriority){
           case RealTime ->{
              currentPriority = (promote)?
                      Priority.RealTime: Priority.Interactive;
           }
           case Interactive -> {
               currentPriority = (promote)?
                       Priority.RealTime: Priority.Background;
           }
           case Background -> {
               currentPriority = (promote)?
                       Priority.Interactive: Priority.Background;
           }
       }
    }

    public void requestStop() {
            process.requestStop();
            timedOut++;
            if (timedOut >= 5) {
                managePriority(false);
                process.PCBSetPriority = currentPriority;
                timedOut = 0;
            }
    }

}
