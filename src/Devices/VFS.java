package Devices;

import java.util.Arrays;

public class VFS implements Device{

    public enum deviceProtocol{
        file,
        random,
    }

    public enum device{
        FakeFileSystem,
        RandomDevices,
    }

    private final int EMPTY = -1;
    private final int[] translator = new int[10];
    private final Device[] devicesReference = new Device[10];
    private final Device[] devices = {
            new FakeFileSystem(device.FakeFileSystem,deviceProtocol.file),
            new RandomDevice(device.RandomDevices,deviceProtocol.random),
};


    public VFS(){
        Arrays.fill(translator, -1);
    }

    @Override
    public deviceProtocol getProtocol() {
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
        String[] deviceRequest= object.split(" ");

        deviceProtocol deviceProtocol = VFS.deviceProtocol
               .valueOf(deviceRequest[0]);
        String data = deviceRequest[1];
        Device requestedDevice = getDevice(deviceProtocol);
        if (requestedDevice == null)
            throw new NullPointerException();

        int translatorId = requestedDevice.open(data);

        for (int i = 0; i < translator.length; i++) {
           if (translator[i] == -1){
              translator[i] = translatorId;
              devicesReference[i] = requestedDevice;
              return i;
           }
        }
        return failed;
    }


    public Device getDevice(deviceProtocol protocol){
        for (int i = 0; i < devices.length ; i++) {
           if (devices[i].getProtocol() == protocol){
                return devices[i];
           }
        }
        return null;
    }



    /**
     * Closes an open device instance.
     *
     * @param id the unique ID of the device instance to close
     */
    @Override
    public void close(int id) throws Exception {
            if (translator[id]==-1)
                throw new Exception("Cannot close unOpened Device");

            int arrayId = translator[id];
            Device device = devicesReference[id];
            device.close(arrayId);
            translator[id] = EMPTY;

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
        if (translator[id]==-1)
            throw new Exception("Cannot close unOpened Device");

        int arrayId = translator[id];
        Device device = devicesReference[id];
        byte[] bytes = device.read(arrayId,size);
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
        if (translator[id]==-1)
            throw new Exception("Cannot close unOpened Device");

        int arrayId = translator[id];
        Device device = devicesReference[id];

        device.seek(arrayId,to);
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
        if (translator[id]==-1)
            throw new Exception("Cannot close unOpened Device");

        int arrayId = translator[id];
        Device device = devicesReference[id];

        return device.write(arrayId,data);
    }

    @Override
    public void isValidIndex(int id) throws Exception {

    }
}
