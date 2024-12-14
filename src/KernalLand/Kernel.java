package KernalLand;
import Devices.Device;
import Devices.VFS;
import Hardware.Hardware;
import UserLand.Process;
import UserLand.UserLandProcess;
import os.Os;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Kernel extends Process implements Device {
    private final Scheduler scheduler = new Scheduler();
    private final VFS vfs = new VFS();
    private final boolean[] freeMemoryMap = new boolean[Hardware.PAGESIZE];
    private final int UNALLOCATED = -1;
    private final Random random = new Random();



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
                    PCB currentProcess = getCurrentProcess();
                    int[] currentTranslator = currentProcess.idTranslator;
                    for (int i = 0; i <currentTranslator.length ; i++) {
                       if (currentTranslator[i] == UNALLOCATED)
                           continue;
                       vfs.close(currentTranslator[i]);
                    }
                    freeMemory(0, currentProcess.physicalMappings.length);
                    scheduler.exit();
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
                case allocate -> {
                   Os.returnVal = allocate(
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
        currentTranslator[id] = UNALLOCATED;
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

    /**
     * Maps a virtual page to a physical page in the TLB for the current process.
     * If the virtual page is not mapped, triggers a page fault and exits the process.
     *
     * @param virtualPageNumber the virtual page number to map
     */
    public void getMapping(int virtualPageNumber) {
        PCB currentProcess = getCurrentProcess();
        VirtualToPhysicalMapping virtualMappings = currentProcess.physicalMappings[virtualPageNumber];
        if(virtualMappings == null){
            pageFault(virtualPageNumber);
            return;
        }
        if (allocatePhysicalMemory(virtualMappings,virtualPageNumber));
    }


    private void pageFault(int virtualPageNumber){
        System.err.printf(
                "Page Fault Process: %s Tried To Access UnAllocated Memory Exiting Process " +
                        "Virtual Page: %s ",
                getCurrentProcess().name, virtualPageNumber
        );
        // Page fault handling and process exit
        freeMemory(0, getCurrentProcess().physicalMappings.length);
        scheduler.exit();
    }

   private void updateTLB(VirtualToPhysicalMapping virtualMappings, int virtualPageNumber){
       int row = random.nextInt(0, Hardware.TLB.length);
       if (!Objects.equals(virtualMappings.physicalPage, UNALLOCATED)) {
           Hardware.TLB[row][Hardware.physicalPageIndex] = virtualMappings.physicalPage;
           Hardware.TLB[row][Hardware.virtualPageIndex] = virtualPageNumber;
       }
   }


    private boolean allocatePhysicalMemory(VirtualToPhysicalMapping virtualMappings, int virtualPageNumber){
        List<Integer> unUsedMemory = availablePhysicalPages(1);
        if (unUsedMemory.isEmpty()){
            return pageSwap(virtualMappings,Hardware.getVirtualAddress(virtualPageNumber));

        }

        claimPhysicalPages(unUsedMemory);
        virtualMappings.physicalPage = unUsedMemory.removeFirst();

        if (virtualMappings.diskPage != UNALLOCATED)
            hasDiskMapping(virtualMappings,Hardware.getVirtualAddress(virtualPageNumber));

        updateTLB(virtualMappings,virtualPageNumber);
        return true;
    }

 private boolean hasDiskMapping(VirtualToPhysicalMapping virtualMappings,int virtualAddress){
        int swapFile = vfs.getSwapFile();
        try {
            seek(swapFile,virtualMappings.diskPage*Hardware.PAGESIZE);
            byte[] fileData = vfs.read(vfs.getSwapFile(),Hardware.PAGESIZE);
            for (int i = 0; i < fileData.length; i++) {
                Hardware.write(virtualAddress+i,fileData[i]);
            }
           return true;
        }catch (Exception exception){
            System.out.println("Failed To Seek and Write Memory from Disk");
           return false;
        }
    }

    private boolean pageSwap(VirtualToPhysicalMapping virtualMapping,int virtualAddress){
        PCB victim = scheduler.getRandomProcess();
        int swapFile = vfs.getSwapFile();
        byte[] data = new byte[Hardware.PAGESIZE];
        Arrays.fill(data,(byte) '0');
        byte overWrite  = 0;
        for (VirtualToPhysicalMapping mapping: victim.physicalMappings){
            if (mapping == null) return pageSwap(virtualMapping,virtualAddress);
            if (mapping.diskPage != UNALLOCATED) continue;
            if (mapping.physicalPage != UNALLOCATED){
                virtualMapping.physicalPage = mapping.physicalPage;
                mapping.physicalPage = -1;
                for (int i = 0; i < data.length; i++) {
                    data[i] = read(virtualAddress+i);
                    Hardware.write(virtualAddress+i,overWrite);
                }
            }
                try {
                    mapping.diskPage = vfs.swapFilePosition;
                    vfs.seek(swapFile,vfs.swapFilePosition);
                    vfs.write(swapFile,data);
                    vfs.incrementSwapFile();
                }catch (Exception exception){
                    System.out.println("Failed to write memory to disk"+exception.getMessage());
                }
        }

        return false;
    }



    /**
     * Checks if the given size is valid and aligned to the page size.
     *
     * @param size the size to check
     * @return true if the size is aligned to the page size, false otherwise
     */
    private boolean isValidSize(int size) {
        int remainder = (size * Hardware.PAGESIZE) % Hardware.PAGESIZE;
        return Objects.equals(remainder, 0);
    }
    /**
     * Allocates a specified number of pages in memory for the current process.
     *
     * @param size the number of pages to allocate
     * @return the starting virtual address of the allocated memory, or -1 if allocation fails
     */
    public int allocate(int size) {
        int failed = -1;
        if (!isValidSize(size)) return failed;

        int virtualAddress =  claimVirtualPages(size);
        if (virtualAddress == UNALLOCATED){
            System.err.println("Max Allocations Reached Killing process:");
            scheduler.exit();
        }
        return virtualAddress;
    }

    /**
     * Claims specified physical pages by marking them as used in the free memory map.
     *
     * @param physicalPages the list of physical pages to claim
     */
    private void claimPhysicalPages(List<Integer> physicalPages) {
        for (int i = 0; i < physicalPages.size(); i++) {
            freeMemoryMap[physicalPages.get(i)] = false; // Mark page as occupied
        }
    }

    /**
     * Claims virtual pages for the current process, mapping them to specified physical pages.
     *
     * @param size the
     * @return the starting virtual address for the claimed virtual pages, or -1 if mapping fails
     */
    private int claimVirtualPages(int size) {
        VirtualToPhysicalMapping[] virtual = getCurrentProcess().physicalMappings;
        int startPage = findContiguousMemory(size);
        if (startPage == UNALLOCATED) return UNALLOCATED; // No contiguous memory found
        for (int i = 0; i < size; i++) {
            virtual[startPage + i] = new VirtualToPhysicalMapping();
        }

        return startPage * Hardware.PAGESIZE; // Return starting virtual address
    }

    private int findContiguousMemory(int size) {
        VirtualToPhysicalMapping[] virtual = getCurrentProcess().physicalMappings;
        int complete = size;
        int start = 0;
        boolean foundStart = false;
        for (int i = 0; i < virtual.length; i++) {
            if (virtual[i] != null) {
                complete = size;
                foundStart = false;
            } else {
                if (!foundStart) {
                    start = i;
                    foundStart = true;
                }
                complete--;
                if (complete == 0) {
                    return start;
                }
            }
        }
        return UNALLOCATED;
    }

    /**
     * Retrieves a list of available physical pages based on the requested size.
     *
     * @param size the number of pages needed
     * @return a list of indices representing available physical pages
     */
    public List<Integer> availablePhysicalPages(int size) {
        List<Integer> availableMemory = new ArrayList<>();
        for (int i = 0; (i < freeMemoryMap.length) && size > 0; i++) {
            if (freeMemoryMap[i]) {
                availableMemory.add(i);
                size--;
            }
        }
        return availableMemory;
    }


    /**
     * Frees a specified number of pages in memory starting from a given pointer.
     *
     * @param pointer the starting pointer to free memory from
     * @param size the number of pages to free
     * @return true if memory was successfully freed, false otherwise
     */
    public boolean freeMemory(int pointer, int size) {
        if (!isValidSize(pointer) && !isValidSize(size)) return false;
        PCB currentProcess = getCurrentProcess();
        VirtualToPhysicalMapping[] physicalPageNumbers = currentProcess.physicalMappings;
        for (int i = pointer; i < size; i++) {
            if (physicalPageNumbers[i]==null)continue;
            int physicalPage = physicalPageNumbers[i].physicalPage;
            if (Objects.equals(physicalPage, -1)) continue; // Skip if page is already free
            physicalPageNumbers[pointer + i] = null; // Mark page as free in process mapping
            freeMemoryMap[physicalPage] = true; // Mark page as available in memory map
            Hardware.freePage(physicalPage); // Free the physical page
            System.err.println("Freeing Page: " + physicalPage);
        }
        return true;
    }





}
