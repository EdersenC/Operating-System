package Devices;


/**
 * The Device interface defines the basic operations that a device should support.
 * This includes opening, closing, reading, writing, and seeking within a device object.
 */
public interface Device {


    VFS.deviceProtocol getProtocol();

    /**
     *
     * Opens a device object for reading or writing.
     *
     * @param object the name or identifier of the device object to open
     * @return an integer representing the unique ID of the opened device instance
     */
    int open(String object) throws Exception;

    /**
     * Closes an open device instance.
     *
     * @param id the unique ID of the device instance to close
     */
    void close(int id) throws Exception;

    /**
     * Reads data from the device instance.
     *
     * @param id the unique ID of the device instance to read from
     * @param size the number of bytes to read
     * @return a byte array containing the data read from the device
     */
    byte[] read(int id, int size) throws Exception;

    /**
     * Moves the read/write pointer to a specific position within the device instance.
     *
     * @param id the unique ID of the device instance
     * @param to the position to seek to within the device instance
     */
    void seek(int id, int to) throws Exception;

    /**
     * Writes data to the device instance.
     *
     * @param id the unique ID of the device instance to write to
     * @param data the byte array of data to write to the device
     * @return an integer representing the number of bytes successfully written
     */
    int write(int id, byte[] data) throws Exception;

    void isValidIndex(int id) throws Exception;


}
