/*
* @Author: ekzyis
* @Date:   01-02-2018 01:03:02
* @Last Modified by:   ekzyis
* @Last Modified time: 16-02-2018 22:02:01
*/
/**
 * Open sound control file.
 * ===============================================
 * All methods and logic about sending and receiving of
 * OSC messages is implemented here.
 */

// Initialize open sound control for communication with sc3-server.
void initOSC()
{
    OSC = new OscP5(this, OSC_PORT);
    // Initialize address to local sc server
    SUPERCOLLIDER = new NetAddress("127.0.0.1", SC_PORT);
    // Check if server is running with a status message.
    connected = false;
    sendMessage(OSC_STATUS);
    // Send boot message.
    sendMessage(OSC_BOOT);
    // Start a thread which periodically checks if sc3-server is still running.
    status = new Thread()
    {
        @Override
        public void run()
        {
            while(!isInterrupted())
            {
                /**
                 * TODO: This implementation depends on order of execution.
                 * If 1. connected gets set to false, 2. a frame gets drawn,
                 * the ICP status is marked as lost in the frame even though
                 * connection may not be lost.
                 * -> Find a way without setting connected to false before checking
                 * the sc3-server.
                 * (Another 'connected' variable could work, but would be messy?)
                 */
                connected = false;
                sendMessage(OSC_STATUS);
                try
                {
                    sleep(1000);
                }
                catch(InterruptedException e)
                {
                    // Exception clears the interrupted flag. Reset it.
                    this.interrupt();
                }
            }
            println("--- checkSC3Status-thread has terminated.");
        }
    };
    status.start();
}

// Listen for messages.
void oscEvent(OscMessage msg)
{
    // SC3 will send SC_REPLY-message if OSC did send OSC_STATUS-message.
    if(msg.checkAddrPattern(SC_REPLY)) connected = true;
}

/**
 * Send a message to an osc listener with given path and arguments.
 */
void sendMessage(String path, float[] args)
{
    println("osc: sending message to: "+path);
    OscMessage msg = new OscMessage(path);
    for(float n : args)
    {
        msg.add(n);
    }
    if(OSC!=null) OSC.send(msg,SUPERCOLLIDER);
}
// Convenience method.
void sendMessage(String path)
{
    sendMessage(path, new float[0]);
}
