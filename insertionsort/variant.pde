/**
 * Variants of sonification.
 * =========================
 * This file saves the paths to the osc
 * listeners defined in the SuperCollider files.
 * A empty string for a path means, that the
 * sonification does not need a listener for this
 * event.
 *
 * @author ekzyis
 * @date 11 February 2018
 */
public enum Sonification
{
    WAVE("WAVE", "/wave_start_INSERTIONSORT", "/wave_pause_INSERTIONSORT", "/wave_resume_INSERTIONSORT", "/wave_set_INSERTIONSORT", "/wave_free_INSERTIONSORT", "/hellowave_INSERTIONSORT", "/boot_wave_INSERTIONSORT"),
    SCALE("SCALE", "/scale_start_INSERTIONSORT", "", "", "/scale_play_INSERTIONSORT", "", "/helloscale_INSERTIONSORT", "/boot_scale_INSERTIONSORT");
    public final String NAME, STARTPATH, PAUSEPATH, RESUMEPATH, MODPATH, FREEPATH, STATUSPATH, BOOTPATH;
    Sonification(String name, String start, String pause, String resume, String mod, String free, String status, String boot)
    {
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
