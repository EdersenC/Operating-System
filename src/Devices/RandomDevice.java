package Devices;

import java.util.Random;

public class RandomDevice implements Device {


    private final Random[] randoms = new Random[10];
    private final VFS.device name;
    public final VFS.deviceProtocol protocol;


    public RandomDevice(VFS.device name, VFS.deviceProtocol random) {
        this.name = name;
        this.protocol = random;
    }


    @Override
    public VFS.deviceProtocol getProtocol() {
        return protocol;
    }

    /**
     * Opens a device object for reading or writing.
     *
     * @param seed the name or identifier of the device object to open
     * @return an integer representing the unique ID of the opened device instance
     */
    @Override
    public int open(String seed) {
        int newSeed;
        int failed = -1;

        if (seed == null || seed.isEmpty())
            return failed;

        try {
            newSeed = Integer.parseInt(seed);
        } catch (Exception e) {
            return failed;
        }

        for (int i = 0; i< randoms.length;i++){
           if(randoms[i]==null){
               randoms[i] = new Random(newSeed);
               return i;
           } else if (i == randoms.length-1) {
               return failed;
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
        randoms[id] = null;
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
        isValidIndex(id);

        byte[] bytes = new byte[size];
        randoms[id].nextBytes(bytes);

        return bytes;
    }

    /**
     * Moves the read/write pointer to a specific position within the device instance.
     *
     * @param id the unique ID of the device instance
     * @param to the position to seek to within the device instance
     */
    @Override
    public void seek(int id, int to) throws Exception {
       read(id, to);
    }

    /**
     * Writes data to the device instance.
     *
     * @param id   the unique ID of the device instance to write to
     * @param data the byte array of data to write to the device
     * @return an integer representing the number of bytes successfully written
     */
    @Override
    public int write(int id, byte[] data) {
        return 0;
    }

    @Override
    public void isValidIndex(int id) throws Exception {
        if (id > randoms.length-1)
            throw new Exception("Invalid Index");
    }
}
