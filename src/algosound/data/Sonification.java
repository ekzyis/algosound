package algosound.data;

/**
 * Variants of sonification. This file saves the paths to the osc listeners
 * defined in the SuperCollider files. A empty string for a path means, that the
 * sonification does not need a listener for this event.
 * ================================
 *
 * @author ekzyis
 * @date 17/02/2018
 */
// TODO Define sonifications individually for algorithms
public enum Sonification {
    WAVE("WAVE", "/wave_start_BUBBLESORT", "/wave_pause_BUBBLESORT", "/wave_resume_BUBBLESORT", "/wave_set_BUBBLESORT",
            "/wave_free_BUBBLESORT", "/hellowave_BUBBLESORT", "/boot_wave_BUBBLESORT"),
    SCALE("SCALE","/scale_start_BUBBLESORT", "", "", "/scale_play_BUBBLESORT", "", "/helloscale_BUBBLESORT",
            "/boot_scale_BUBBLESORT");
    public final String NAME, STARTPATH, PAUSEPATH, RESUMEPATH, MODPATH, FREEPATH, STATUSPATH, BOOTPATH;

    // Minimal and maximal frequency for "SCALE"
    public static final int FREQ_MIN = 200;
    public static final int FREQ_MAX = 4000;

    Sonification(String name, String start, String pause, String resume, String mod, String free, String status,
            String boot) {
        this.NAME = name;
        this.STARTPATH = start;
        this.PAUSEPATH = pause;
        this.RESUMEPATH = resume;
        this.MODPATH = mod;
        this.FREEPATH = free;
        this.STATUSPATH = status;
        this.BOOTPATH = boot;
    }
}
