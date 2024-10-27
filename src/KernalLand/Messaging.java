package KernalLand;

import java.util.Arrays;

public class Messaging {

    public int senderPid;
    public int targetPid;
    public int what;
    public byte[] data;



    /**
     *Used to Create a copy of a message inorder to avoid using shared memory
     *
     * @param sender The pid of the process wanting to send message
     * @param target The pid of the senders target process
     * @param what   An int the target process will use to determine how to handle
     *               the incoming data
     */
    public Messaging(int sender, int target, int what, byte[] data){
        senderPid = sender;
        targetPid = target;
        this.what = what;
        this.data = data;
    }

    /**
     *Used to Create a copy of a Message class inorder to avoid using shared memory
     * between processes
     *
     * @param kernelMessage
     */
    public Messaging(Messaging kernelMessage) {
        senderPid = kernelMessage.senderPid;
        targetPid = kernelMessage.targetPid;
        this.what = kernelMessage.what;
        this.data = kernelMessage.data.clone();
    }


    public String toString(){
    return String.format(
            """
                    Created Message:\
                    Sender: %s
                    target: %s
                    data: %s and what: %s\s
                    """,
                senderPid,targetPid, new String(data),what
        );
    }

}
