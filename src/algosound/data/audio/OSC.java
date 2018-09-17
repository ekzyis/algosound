/*
 * @Author: ekzyis
 * @Date:   17-02-2018 23:48:24
 * @Last Modified by:   ekzyis
 * @Last Modified time: 18-02-2018 01:00:15
 */
package algosound.data.audio;

import algosound.ui.Algosound;
import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

import java.util.HashMap;
import java.util.Map;

/**
 * Open sound control file. All methods and logic about sending and receiving of
 * OSC messages is implemented here. Extends PApplet to be able to receive
 * messages via oscEvent().
 * ================================
 *
 * @author ekzyis
 * @date 17/02/2018
 */
public class OSC extends PApplet {
    private interface LambdaInterface {
        void call(String s1);
    }

    private static OSC instance;

    private final OscP5 OSC;
    private final NetAddress SUPERCOLLIDER;
    private final int SC_PORT = 57120;
    private final int OSC_PORT = 12000;

    // Status of connections
    Map<String, Boolean> connected = new HashMap<String, Boolean>();
    // Osc address of listeners.
    private String STATUS;

    // Status thread which checks periodically for sc3-server.
    private final Thread status;

    // Standardized time format
    private final java.text.SimpleDateFormat TIMEFORMAT = new java.text.SimpleDateFormat("### [yyyy/M/dd HH:mm:ss] ");

    private OSC() {
        OSC = new OscP5(this, OSC_PORT);
        SUPERCOLLIDER = new NetAddress("127.0.0.1", SC_PORT);
        Sonification sonification = Algosound.getInstance().getAlgorithm().getSelectedSonification();
        // Send boot message.
        sendMessage(sonification.BOOTPATH);
        // Start a thread which periodically checks if sc3-server is still running.
        status = new Thread() {
            @Override
            public void run() {
                String out = TIMEFORMAT.format(new java.util.Date());
                out += "PROCESS @ checkSC3Status started.";
                System.out.println(out);
                LambdaInterface resetPathStatus = (String p) -> {
                    // reset status
                    connected.put(p, false);
                    // send message to see if OSCdef replies
                    sendMessage(p, "status");
                };
                while (!isInterrupted()) {
                    // Update sonification (has maybe been changed)
                    Sonification sonification = Algosound.getInstance().getAlgorithm().getSelectedSonification();
                    String[] paths = sonification.getPaths();
                    for(String p : paths) {
                        resetPathStatus.call(p);
                    }
                    /**
                     * TODO: This implementation depends on order of execution. If 1. connected gets
                     * set to false, 2. a frame gets drawn, the ICP status is marked as lost in the
                     * frame even though connection may not be lost. -> Find a way without setting
                     * connected to false before checking the sc3-server. (Another 'connected'
                     * variable could work, but would be messy?)
                     */
                    // Send messages to detect if other OSCdefs are also responding.
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        // Exception clears the interrupted flag. Reset it.
                        this.interrupt();
                    }
                }
                out = TIMEFORMAT.format(new java.util.Date());
                out += "PROCESS @ checkSC3Status stopped.";
                System.out.println(out);
            }
        };
        status.start();
    }

    // Listen for messages.
    // NOTE we only expect status replies
    public void oscEvent(OscMessage msg) {
        // SC3 will reply with path of received OSCdef
        String key = msg.addrPattern();
        String out = TIMEFORMAT.format(new java.util.Date());
        if(connected.containsKey(key)) {
            out += "OSC @ RECV_MSG: " + key;
            System.out.println(out);
            connected.put(key, true);
        }
        else {
            out += "OSC @ RECV_MSG: ERROR: UNEXPECTED MESSAGE: " + key;
            System.err.println(out);
        }
    }

    // NOTE use `Object` as arg type so you only need one method?
    /**
     * Send a message to an osc listener with given path and arguments.
     *
     * @param path path to osc listener.
     * @args arguments within osc message
     */
    public void sendMessage(String path, int[] args) {
        if(path==null) return;
        String out = TIMEFORMAT.format(new java.util.Date());
        out += "OSC @ SEND_MSG to: " + path;
        OscMessage msg = new OscMessage(path);
        out += ", ARGS: [ ";
        for (int n : args) {
            msg.add(n);
            out += n + " ";
        }
        out += "]";
        if (OSC != null) {
            OSC.send(msg, SUPERCOLLIDER);
            System.out.println(out);
        }
        else System.err.println(out + " FAILED SENDING MESSAGE");
    }

    public void sendMessage(String path, float[] args) {
        if(path==null) return;
        String out = TIMEFORMAT.format(new java.util.Date());
        out += "OSC @ SEND_MSG to: " + path;
        OscMessage msg = new OscMessage(path);
        out += ", ARGS: [ ";
        for (float n : args) {
            msg.add(n);
            out += n + " ";
        }
        out += "]";
        if (OSC != null) {
            OSC.send(msg, SUPERCOLLIDER);
            System.out.println(out);
        }
        else System.err.println(out + " FAILED SENDING MESSAGE");
    }

    public void sendMessage(String path, String msg) {
        if(path==null) return;
        String out = TIMEFORMAT.format(new java.util.Date());
        out += "OSC @ SEND_MSG to: " + path;
        OscMessage oscmsg = new OscMessage(path);
        out += ", ARGS: [ " + msg + " ]";
        oscmsg.add(msg);
        if (OSC != null) {
            OSC.send(oscmsg, SUPERCOLLIDER);
            System.out.println(out);
        }
        else System.err.println(out + " FAILED SENDING MESSAGE");
    }

    // Convenience method.
    public void sendMessage(String path) {
        if(path==null) return;
        sendMessage(path, new int[0]);
    }

    public boolean getStatus(String s) {
        if(connected.get(s) == null) return false;
        return connected.get(s);
    }

    public Map<String, Boolean> getStatusMap() {
        return connected;
    }

    public Thread getStatusThread() {
        return status;
    }

    public void dispose() {
        OSC.dispose();
    }

    public static OSC getInstance() {
        if (instance == null) {
            instance = new OSC();
        }
        return instance;
    }
}
