package Hardware;

public class Hardware {

    public static final int PAGESIZE = 1024;
    private static final byte[] MEMORY = new byte[1024*1024];



    public static byte read(int address){
       return MEMORY[address];
    }

    public static void write(int address,byte value){
        MEMORY[address] = value;
    }

    public static int getVirtualPage(int virtualAddress){
        return virtualAddress/PAGESIZE;
    }
    public static int getPageOffset(int virtualAddress){
        return virtualAddress%PAGESIZE;
    }
    public static int getVirtualAddress(int virtualPage){
        return virtualPage*PAGESIZE;
    }
    public static int getPhysicalAddress(int physicalPage, int offset){
        return (physicalPage*PAGESIZE)+offset;
    }



}

