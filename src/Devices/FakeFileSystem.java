package Devices;

import java.io.RandomAccessFile;

public class FakeFileSystem implements Device{
   private final RandomAccessFile[] randomAccessFiles = new RandomAccessFile[10];
   private final VFS.device name;
   public final VFS.deviceProtocol protocol;


   public FakeFileSystem(VFS.device name, VFS.deviceProtocol file){
       this.name = name;
       this.protocol = file;
  }


    @Override
    public VFS.deviceProtocol getProtocol() {
       return protocol;
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

        if (object == null || object.isEmpty())
            throw new Exception(name+"Null or Empty String passed in");


        for (int i = 0; i < randomAccessFiles.length; i++) {
            if(randomAccessFiles[i]==null){
               try{
                   RandomAccessFile newFile = new RandomAccessFile(object,"rws");
                  randomAccessFiles[i] = newFile;
               }catch (Exception e){
                   throw new Exception(name+"Failed To Create File");
               }
                return i;
            } else if (i == randomAccessFiles.length-1) {
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
        System.out.println("Closing soaddfsfasdfasdfasdfasdf");
        isValidIndex(id);
        randomAccessFiles[id].close();
        randomAccessFiles[id]= null;
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
        randomAccessFiles[id].read(bytes);

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
        isValidIndex(id);
        randomAccessFiles[id].seek(to);
    }

    /**
     * Writes data to the device instance.
     *
     * @param id   the unique ID of the device instance to write to
     * @param data the byte array of data to write to the device
     * @return an integer representing the number of bytes successfully written
     */
    @Override
    public int write(int id, byte[] data)throws Exception {
        isValidIndex(id);
        randomAccessFiles[id].write(data);
        return data.length;
    }

    @Override
    public void isValidIndex(int id) throws Exception {
        if (id > randomAccessFiles.length-1)
            throw new Exception("Invalid Index");
    }
}
