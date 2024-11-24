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
    private final int EMPTY = -1;


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
                       if (currentTranslator[i] == EMPTY)
                           continue;
                       vfs.close(currentTranslator[i]);
                    }
                    freeMemory(0, currentProcess.physicalPageNumbers.length);
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
        currentTranslator[id] = EMPTY;
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
        Random random = new Random();
        PCB currentProcess = getCurrentProcess();
        int failed = -1;
        int row = random.nextInt(0, Hardware.TLB.length);
        int virtualIndex = 0;
        int physicalIndex = 1;
        int physicalPageNumber = currentProcess.physicalPageNumbers[virtualPageNumber];

        // if we found a physical page
        if (!Objects.equals(physicalPageNumber, failed)) {
            Hardware.TLB[row][physicalIndex] = physicalPageNumber;
            Hardware.TLB[row][virtualIndex] = virtualPageNumber;
            return;
        }

        // Page fault handling and process exit
        System.err.printf(
                "Page Fault Process: %s Tried To Access UnAllocated Memory Exiting Process " +
                        "Virtual Page: %s " +
                        "Physical Address: %s ",
                currentProcess.name, virtualPageNumber, physicalPageNumber
        );
        freeMemory(0, currentProcess.physicalPageNumbers.length);
        scheduler.exit();
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
        List<Integer> physicalPages = availablePhysicalPages(size);
        assert !physicalPages.isEmpty();

        // Claim virtual pages and check if allocation succeeded
        int virtualAddress = claimVirtualPages(physicalPages);
        if (virtualAddress == -1) {
            System.err.println("No More Memory to allocate. Killing process");
            scheduler.exit();
        }

        claimPhysicalPages(physicalPages); // Mark claimed pages in memory map
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
     * @param physicalPages the list of physical pages to map
     * @return the starting virtual address for the claimed virtual pages, or -1 if mapping fails
     */
    private int claimVirtualPages(List<Integer> physicalPages) {
        int[] virtual = getCurrentProcess().physicalPageNumbers;
        int startPage = -1;
        boolean foundStart = false;
        for (int i = 0; i < virtual.length; i++) {
            if (!Objects.equals(virtual[i], -1)) continue; // Skip if page is already allocated
            if (!foundStart) startPage = i;
            foundStart = true;
            if (physicalPages.isEmpty()) break;
            virtual[i] = physicalPages.removeFirst(); // Map physical page to virtual page
        }
        if (!physicalPages.isEmpty()) // Check if all pages were mapped
            return -1;
        return startPage * Hardware.PAGESIZE; // Return starting virtual address
    }

    /**
     * Retrieves a list of available physical pages based on the requested size.
     *
     * @param size the number of pages needed
     * @return a list of indices representing available physical pages
     */
    public List<Integer> availablePhysicalPages(int size) {
        List<Integer> availableMemory = new ArrayList<>();
        for (int i = 0; (i < freeMemoryMap.length) && size != 0; i++) {
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
        int[] physicalPageNumbers = currentProcess.physicalPageNumbers;
        for (int i = pointer; i < size; i++) {
            int physicalPage = physicalPageNumbers[i];
            if (Objects.equals(physicalPage, -1)) continue; // Skip if page is already free
            physicalPageNumbers[pointer + i] = -1; // Mark page as free in process mapping
            freeMemoryMap[physicalPage] = true; // Mark page as available in memory map
            Hardware.freePage(physicalPage); // Free the physical page
            System.err.println("Freeing Page: " + physicalPage);
        }
        return true;
    }





}
