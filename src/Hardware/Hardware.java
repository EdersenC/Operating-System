package Hardware;

import UserLand.UserLandProcess;
import os.Os;

import java.util.Arrays;
import java.util.Objects;

public class Hardware {

    public static final int PAGESIZE = 1024;
    public static final int[][] TLB = new int[2][2];
    private static final byte[] MEMORY = new byte[PAGESIZE*PAGESIZE];
    private static final int FAILED = -1;


    /**
     * Reads a byte from a specific address in memory. Translates the virtual address
     * to a physical address using the TLB lookup mechanism and retrieves the byte from memory.
     *
     * @param address the virtual address to read from
     * @return the byte value stored at the specified physical address in memory
     */
    public static byte read(int address) {
        int virtualPageNumber = Hardware.getVirtualPage(address);
        int offset = Hardware.getPageOffset(address);
        int physicalAddress = Hardware.getPhysicalAddress(
                Hardware.tlbLookUp(virtualPageNumber),
                offset
        );
        return MEMORY[physicalAddress];
    }

    /**
     * Writes a byte to a specific address in memory. Translates the virtual address
     * to a physical address using the TLB lookup mechanism and stores the byte in memory.
     *
     * @param address the virtual address to write to
     * @param value the byte value to write to the specified physical address in memory
     */
    public static void write(int address, byte value) {
        int virtualPageNumber = Hardware.getVirtualPage(address);
        int offset = Hardware.getPageOffset(address);
        int physicalAddress = Hardware.getPhysicalAddress(
                Hardware.tlbLookUp(virtualPageNumber),
                offset
        );
        MEMORY[physicalAddress] = value;
    }


    /**
     * Frees a specific page in memory by setting all bytes in the page to zero.
     *
     * @param page the page number to free
     */
    public static void freePage(int page) {
        assert page < MEMORY.length / PAGESIZE;
        for (int i = page; i < PAGESIZE; i++) {
            MEMORY[i] = 0;
        }
    }

    /**
     * Looks up the physical page number associated with a virtual page in the TLB.
     *
     * @param virtualPage the virtual page number to look up
     * @return the physical page number associated with the virtual page, or triggers a
     *         mapping request if the virtual page is not found
     */
    public static int tlbLookUp(int virtualPage) {
        int virtualPageIndex = 0;
        int physicalPageIndex = 1;
        for (int row = 0; row < TLB.length; row++) {
            int tlbVPage = TLB[row][virtualPageIndex];
            if (Objects.equals(tlbVPage, virtualPage)) {
                return TLB[row][physicalPageIndex];
            }
        }
        // If the virtual page is not in TLB, trigger a mapping request
        Os.getMapping(virtualPage);
        return tlbLookUp(virtualPage);
    }

    /**
     * Clears all entries in the TLB by resetting each entry to zero.
     */
    public static void clearTLB() {
        Arrays.fill(TLB, new int[]{0, 0});
    }

    /**
     * Calculates the virtual page number from a virtual address.
     *
     * @param virtualAddress the virtual address
     * @return the virtual page number associated with the virtual address
     */
    public static int getVirtualPage(int virtualAddress) {
        return virtualAddress / PAGESIZE;
    }

    /**
     * Calculates the page offset within a page from a virtual address.
     *
     * @param virtualAddress the virtual address
     * @return the offset within the page for the given virtual address
     */
    public static int getPageOffset(int virtualAddress) {
        return virtualAddress % PAGESIZE;
    }

    /**
     * Calculates the virtual address given a virtual page number.
     *
     * @param virtualPage the virtual page number
     * @return the starting virtual address of the given virtual page
     */
    public static int getVirtualAddress(int virtualPage) {
        return virtualPage * PAGESIZE;
    }

    /**
     * Calculates the physical address given a physical page number and an offset.
     *
     * @param physicalPage the physical page number
     * @param offset the offset within the physical page
     * @return the physical address computed from the physical page and offset
     */
    public static int getPhysicalAddress(int physicalPage, int offset) {
        return (physicalPage * PAGESIZE) + offset;
    }

}

