/*
 * @Author: ekzyis
 * @Date:   17-02-2018 23:48:24
 * @Last Modified by:   ekzyis
 * @Last Modified time: 18-02-2018 01:00:15
 */
package algosound.net;

import algosound.ui.Algosound;
import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

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
    private static OSC instance;

    private final OscP5 OSC;
    private final NetAddress SUPERCOLLIDER;
    private final int SC_PORT = 57120;
    private final int OSC_PORT = 12000;

    // Status of connection.
    private boolean connected;
    // SuperCollider status reply.
    private final String SC_REPLY = "/hello";
    // Osc address of listeners.
    private String STATUS;

    // Status thread which checks periodically for sc3-server.
    private final Thread status;

    // Standardized time format
    private final java.text.SimpleDateFormat TIMEFORMAT = new java.text.SimpleDateFormat("### [yyyy/M/dd HH:mm:ss] ");

    private OSC() {
        OSC = new OscP5(this, OSC_PORT);
        SUPERCOLLIDER = new NetAddress("127.0.0.1", SC_PORT);
        connected = false;
        // Send boot message.
        sendMessage(Algosound.getInstance().getAlgorithm().getSelectedSonification().BOOTPATH);
        // Start a thread which periodically checks if sc3-server is still running.
        status = new Thread() {
            @Override
            public void run() {
                System.out.print(TIMEFORMAT.format(new java.util.Date()));
                System.out.println("PROCESS @ checkSC3Status started.");
                while (!isInterrupted()) {
                    /**
                     * TODO: This implementation depends on order of execution. If 1. connected gets
                     * set to false, 2. a frame gets drawn, the ICP status is marked as lost in the
                     * frame even though connection may not be lost. -> Find a way without setting
                     * connected to false before checking the sc3-server. (Another 'connected'
                     * variable could work, but would be messy?)
                     */
                    connected = false;
                    sendMessage(Algosound.getInstance().getAlgorithm().getSelectedSonification().STATUSPATH);
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        // Exception clears the interrupted flag. Reset it.
                        this.interrupt();
                    }
                }
                System.out.print(TIMEFORMAT.format(new java.util.Date()));
                System.out.println("PROCESS @ checkSC3Status stopped.");
            }
        };
        status.start();
    }

    // Listen for messages.
    public void oscEvent(OscMessage msg) {
        System.out.print(TIMEFORMAT.format(new java.util.Date()));
        System.out.println("OSC @ RECV_MSG: " + msg.addrPattern());
        // SC3 will fire SC_REPLY-message if OSC did fire OSC_STATUS-message.
        if (msg.checkAddrPattern(SC_REPLY))
            connected = true;
    }

    /**
     * Send a message to an osc listener with given path and arguments.
     *
     * @param path path to osc listener.
     * @args arguments within osc message
     */
    public void sendMessage(String path, int[] args) {
        System.out.print(TIMEFORMAT.format(new java.util.Date()));
        System.out.print("OSC @ SEND_MSG to: " + path);
        OscMessage msg = new OscMessage(path);
        System.out.print(", ARGS: [ ");
        for (int n : args) {
            msg.add(n);
            System.out.print(n + " ");
        }
        System.out.print("]");
        if (OSC != null)
            OSC.send(msg, SUPERCOLLIDER);
        else System.err.print(" FAILED SENDING MESSAGE");
        System.out.println();
    }

    public void sendMessage(String path, float[] args) {
        System.out.print(TIMEFORMAT.format(new java.util.Date()));
        System.out.print("OSC @ SEND_MSG to: " + path);
        OscMessage msg = new OscMessage(path);
        System.out.print(", ARGS: [ ");
        for (float n : args) {
            msg.add(n);
            System.out.print(n + " ");
        }
        System.out.print("]");
        if (OSC != null)
            OSC.send(msg, SUPERCOLLIDER);
        else System.err.print(" FAILED SENDING MESSAGE");
        System.out.println();
    }

    /**
     * Send a message to an osc listener with given path and arguments (generic-version).
     * Does not work because java primitives like int and float are no real "Types" like Number.
     *
     * @param path path to osc listener.
     */
    /*
    public <T> void sendMessage(String path, T[] args) {
        System.out.println("osc: sending message to: " + path);
        OscMessage msg = new OscMessage(path);
        System.out.print("--- arguments[ ");
        for (T n : args) {
            float add = (float) n;
            msg.add((float) n);
            System.out.print(n+" ");
        }
        System.out.println("]");
        if (OSC != null)
            OSC.fire(msg, SUPERCOLLIDER);
    }
    */

    // Convenience method.
    public void sendMessage(String path) {
        sendMessage(path, new int[0]);
    }

    public boolean getStatus() {
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
