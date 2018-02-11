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
    WAVE("WAVE","/wave_start_QUICKSORT", "/wave_pause_QUICKSORT", "/wave_resume_QUICKSORT", new String[]{"/wave_set1_QUICKSORT", "/wave_set2_QUICKSORT", "/wave_set3_QUICKSORT"}, "/wave_free_QUICKSORT", "/hellowave_QUICKSORT", "/boot_wave_QUICKSORT"),
    SCALE("SCALE","/scale_start_QUICKSORT", "", "", new String[]{ "/scale_play_QUICKSORT", "/scale_play_QUICKSORT", "/scale_play_QUICKSORT" }, "" , "/helloscale_QUICKSORT", "/boot_scale_QUICKSORT");
    public final String NAME, STARTPATH, PAUSEPATH, RESUMEPATH, FREEPATH, STATUSPATH, BOOTPATH;
    public final String[] MODPATH;
    Sonification(String name, String start, String pause, String resume, String[] mod, String free, String status, String boot)
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
