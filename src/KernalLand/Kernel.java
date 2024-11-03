package KernalLand;
import Devices.Device;
import Devices.VFS;
import Hardware.Hardware;
import UserLand.Process;
import UserLand.UserLandProcess;
import os.Os;

import java.util.*;

public class Kernel extends Process implements Device {
    private final Scheduler scheduler = new Scheduler();
    private final VFS vfs = new VFS();
    private final boolean[] freeMemoryMap = new boolean[Hardware.PAGESIZE];

    /**
     * This method is used to create a new process
     * @return the current process from the Scheduler
     */
    public PCB getCurrentProcess(){
        return scheduler.currentUserProcess;
    }


   public Kernel(){
       Arrays.fill(freeMemoryMap, true);
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
                case getPid -> {
                    Os.returnVal = scheduler.getPid();
                }
                case getPidByName -> {
                    Os.returnVal = scheduler.getPidByName(
                            (String) Os.parameters.removeFirst()
                    );
                }
                case sendMessage -> {
                    scheduler.sendMessage(
                            (Messaging)Os.parameters.removeFirst()
                    );
                }
                case waitForMessage ->{
                    Os.returnVal = scheduler.waitForMessage();
                }
                case getMapping -> {
                    getMapping(
                            (int) Os.parameters.removeFirst()
                    );
                }
                case Halt -> {
                    scheduler.Halt();
                }
                case Proceed -> {
                    scheduler.Proceed();
                }
            }
            PCB pcb = scheduler.currentUserProcess;
            if(pcb!=null){
                pcb.start();
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

    public void getMapping(int virtualPageNumber){
        Random random = new Random();
        int failed = -1;
        int row = random.nextInt(0,UserLandProcess.TLB.length+1);
        int physicalIndex = 1;
        int physicalPageNumber = getCurrentProcess().physicalPageNumbers [virtualPageNumber];


        int virtualAddress = Hardware.getVirtualAddress(virtualPageNumber) ;
        int offset = Hardware.getPageOffset(virtualAddress);
        int physicalAddress =Hardware.getPhysicalAddress(physicalPageNumber,offset) ;


        if (!Objects.equals(physicalPageNumber,failed)){
            UserLandProcess.TLB[row][physicalIndex] = physicalAddress;
        }
        /// not found in map
    }



    private boolean isValidSize(int size){
        int remainder = Hardware.PAGESIZE % size;
        return!Objects.equals(remainder,0);
    }


    public int allocate(int size){
        int failed = -1;
        int allocationSize =  size*Hardware.PAGESIZE;
        PCB currentProcess  = getCurrentProcess();
        if (!isValidSize(size))
            return failed;

        for (int physicalPage = 0; physicalPage < freeMemoryMap.length; physicalPage++) {
            boolean freePage = freeMemoryMap[physicalPage];
            if(!freePage)
                continue;

            if (hasContiguousMemory(physicalPage,size)){
                claimMemory(physicalPage,size);

                return Hardware.getVirtualAddress(physicalPage);
            }

        }

        return failed;
    }

   public void claimMemory(int virtualPage, int size){
        int physicalPageRange = virtualPage * size;
        for (int i = virtualPage; i < i+size ; i++) {
           freeMemoryMap[i] = false;
        }
        getCurrentProcess().physicalPageNumbers[virtualPage] = physicalPageRange;
   }

    public boolean hasContiguousMemory(int index, int size){
        for (int i = index; i < i+size; i++) {
            if (!freeMemoryMap[i]){
               return false;
            }
        }
       return true;
    }



    public boolean freeMemory(int pointer, int size){
        if (!isValidSize(pointer) && !isValidSize(size))
            return false;

        int physicalPage =0; // need to implement
        for (int i = 0; i < size; i++) {
            freeMemoryMap[physicalPage+i] = true;
        }

        return true;
    }





}
