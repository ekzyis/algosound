/*
* @Author: ekzyis
* @Date:   10-02-2018 01:07:22
* @Last Modified by:   ekzyis
* @Last Modified time: 16-02-2018 22:01:56
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
    WAVE("WAVE", "/wave_start_MERGESORT", "/wave_pause_MERGESORT", "/wave_resume_MERGESORT", "/wave_set_MERGESORT", "/wave_free_MERGESORT", "/hellowave_MERGESORT", "/boot_wave_MERGESORT"),
    SCALE("SCALE", "/scale_start_MERGESORT", "", "", "/scale_play_MERGESORT", "", "/helloscale_MERGESORT", "/boot_scale_MERGESORT");
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
