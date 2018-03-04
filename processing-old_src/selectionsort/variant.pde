/*
* @Author: ekzyis
* @Date:   10-02-2018 01:07:26
* @Last Modified by:   ekzyis
* @Last Modified time: 16-02-2018 22:05:02
*/
/**
 * Variants of sonification.
 * =========================
 * This file saves the paths to the osc
 * listeners defined in the SuperCollider files.
 * A empty string for a path means, that the
 * sonification does not need a listener for this
 * event.
 */
public enum Sonification
{
    WAVE("WAVE", "/wave_start_SELECTIONSORT", "/wave_pause_SELECTIONSORT", "/wave_resume_SELECTIONSORT", new String[]{"/wave_set_SELECTIONSORT", "/min_set_SELECTIONSORT"}, "/wave_free_SELECTIONSORT", "/hellowave_SELECTIONSORT", "/boot_wave_SELECTIONSORT"),
    SCALE("SCALE", "/scale_start_SELECTIONSORT", "", "", new String[]{"/scale_play_SELECTIONSORT", "/scale_play_SELECTIONSORT"}, "", "/helloscale_SELECTIONSORT", "/boot_scale_SELECTIONSORT");
    public final String NAME, STARTPATH, PAUSEPATH, RESUMEPATH, FREEPATH, STATUSPATH, BOOTPATH;
    public final String[]  MODPATH;
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
