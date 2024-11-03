package UserLand;

import Hardware.Hardware;
import os.Os;

import java.util.Objects;

public class UserLandProcess extends Process {
    public int sleepTime = 0;
    private final int PAGESIZE = Hardware.PAGESIZE;
    public static final int[][] TLB = new int[2][2];
    public static final int FAILED = -1;



    public void main() {
    }


    private byte read(int address){
        int virtualPageNumber = Hardware.getVirtualPage(address);
        int physicalAddress = tlbLookUp(virtualPageNumber);
        assert physicalAddress!= FAILED;
        byte value = Hardware.read(physicalAddress);
        System.out.println("Read this value from Memory"+ value);
        return value;
    }


    private int tlbLookUp(int virtualPage){
        int virtualPageIndex = 0;
        int physicalPageIndex = 1;
        int virtualAddress = Hardware.getVirtualAddress(virtualPage);
        for (int row = 0; row < TLB.length; row++) {
            int tlbVPage =TLB[row][virtualPageIndex];
            if (Objects.equals(tlbVPage,virtualPage)){
                int physicalPage =  TLB[row][physicalPageIndex];
                int offset = Hardware.getPageOffset(virtualAddress);
                return Hardware.getPhysicalAddress(physicalPage,offset);
            }
        }
        // if out of this loop that mean is failed to return Phys addr
        Os.getMapping(virtualPage);
        return tlbLookUp(virtualPage);
    }



    private void write(int address, byte value){
        int virtualPageNumber = Hardware.getVirtualPage(address);
        int physicalAddress = tlbLookUp(virtualPageNumber);
        assert physicalAddress!= FAILED;
        Hardware.write(physicalAddress,value);
    }






}