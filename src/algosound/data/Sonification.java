package algosound.data;

/**
 * Sonification-class. This class is used in the implemented algorithms
 * to define the paths for the specific sonifications.
 * ================================
 *
 * @author ekzyis
 * @date 17/02/2018
 */
public class Sonification {
    // Name of this sonification and paths for the osc listeners.
    public final String NAME, STARTPATH, PAUSEPATH, RESUMEPATH, MODPATH, FREEPATH, STATUSPATH, BOOTPATH, REALTIMEMOD;

    public Sonification(String name, String start, String pause, String resume, String mod, String free, String status,
            String boot, String realtime) {
        this.NAME = name;
        this.STARTPATH = start;
        this.PAUSEPATH = pause;
        this.RESUMEPATH = resume;
        this.MODPATH = mod;
        this.FREEPATH = free;
        this.STATUSPATH = status;
        this.BOOTPATH = boot;
        this.REALTIMEMOD = realtime;
    }
}
