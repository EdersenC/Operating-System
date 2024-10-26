package KernalLand;
import Devices.Device;
import Devices.VFS;
import UserLand.Process;
import UserLand.UserLandProcess;
import os.Os;

import java.util.ArrayList;
import java.util.Deque;

public class Kernel extends Process implements Device {
    private final Scheduler scheduler = new Scheduler();
    private final VFS vfs = new VFS();

    /**
     * This method is used to create a new process
     * @return the current process from the Scheduler
     */
    public PCB getCurrentProcess(){
        return scheduler.currentUserProcess;
    }


    /**
     * This method is used to start the kernel
     */
    @Override
    public void main() throws Exception {
        while (true) {
            switch (Os.currentCall) {
                case CreateProcess -> {
                     Os.returnVal = scheduler.createProcess(
                             (UserLandProcess) Os.parameters.removeFirst(),
                             (PCB.Priority)  Os.parameters.removeFirst()
                     );
                }
                case SwitchProcess ->{
                    scheduler.switchProcess();
                    Os.returnVal = true;
                }
                case Sleep->{
                    scheduler.sleep((int)Os.parameters.removeFirst());
                    Os.returnVal = true;
                }
                case Exit -> {
                   scheduler.exit();
                    PCB currentProcess = getCurrentProcess();
                    int empty = -1;
                    int[] currentTranslator = currentProcess.idTranslator;
                    for (int i = 0; i <currentTranslator.length ; i++) {
                       if (currentTranslator[i] == empty)
                           continue;
                       vfs.close(currentTranslator[i]);
                    }
                }
                case open -> {
                    System.out.println("Calling open in kernal");
                    Os.returnVal = open(
                            (String) Os.parameters.removeFirst()
                    );

                }
                case close -> {
                   close((int) Os.parameters.removeFirst() );
                }
                case read -> {
                   Os.returnVal = read(
                           (int) Os.parameters.removeFirst(),
                           (int) Os.parameters.removeFirst()
                   );
                }
                case write -> {
                    Os.returnVal = write(
                            (int) Os.parameters.removeFirst(),
                            (byte[]) Os.parameters.removeFirst()
                    );
                }
                case seek -> {
                    seek(
                            (int) Os.parameters.removeFirst(),
                            (int) Os.parameters.removeFirst()
                    );
                }

                case Halt -> {
                    scheduler.Halt();
                    Os.returnVal = true;
                }
                case Proceed -> {
                    scheduler.Proceed();
                    Os.returnVal = true;
                }
            }
            if(scheduler.currentUserProcess !=null){
                scheduler.currentUserProcess.start();
            }
            stop();
        }
    }


    @Override
    public VFS.deviceProtocol getProtocol() {
        return null;
    }

    /**
     * Opens a device object for reading or writing.
     *
     * @param object the name or identifier of the device object to open
     * @return an integer representing the unique ID of the opened device instance
     */
    @Override
    public int open(String object) throws Exception {
       int failed = -1;
       PCB currentProcess = getCurrentProcess();
       int[] currentTranslator = currentProcess.idTranslator;
        for (int i = 0; i < currentTranslator.length; i++) {
           if (currentTranslator[i] == failed){
              currentTranslator[i] = vfs.open(object);
              return i;
           }
        }

       return failed;
    }

    /**
     * Closes an open device instance.
     *
     * @param id the unique ID of the device instance to close
     */
    @Override
    public void close(int id) throws Exception {
        PCB currentProcess = getCurrentProcess();
        int[] currentTranslator = currentProcess.idTranslator;
        vfs.close(currentTranslator[id]);
    }

    /**
     * Reads data from the device instance.
     *
     * @param id   the unique ID of the device instance to read from
     * @param size the number of bytes to read
     * @return a byte array containing the data read from the device
     */
    @Override
    public byte[] read(int id, int size) throws Exception {
    return vfs.read(getCurrentProcess().idTranslator[id],size);
    }

    /**
     * Moves the read/write pointer to a specific position within the device instance.
     *
     * @param id the unique ID of the device instance
     * @param to the position to seek to within the device instance
     */
    @Override
    public void seek(int id, int to) throws Exception {
        vfs.seek(getCurrentProcess().idTranslator[id],to);
    }

    /**
     * Writes data to the device instance.
     *
     * @param id   the unique ID of the device instance to write to
     * @param data the byte array of data to write to the device
     * @return an integer representing the number of bytes successfully written
     */
    @Override
    public int write(int id, byte[] data) throws Exception {
        return vfs.write(getCurrentProcess().idTranslator[id],data);
    }

    @Override
    public void isValidIndex(int id) throws Exception {

    }
}
