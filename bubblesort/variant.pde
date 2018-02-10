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
 * @date 09 February 2018
 */
public enum Sonification
{
    WAVE("WAVE", "/wave_start", "/wave_pause", "/wave_resume", "/wave_set", "/wave_free", "/hellowave"),
    SCALE("SCALE", "/scale_start", "", "", "/scale_play", "", "/helloscale");
    public final String NAME, STARTPATH, PAUSEPATH, RESUMEPATH, MODPATH, FREEPATH, STATUSPATH;
    Sonification(String name, String start, String pause, String resume, String mod, String free, String status)
    {
        this.NAME = name;
        this.STARTPATH = start;
        this.PAUSEPATH = pause;
        this.RESUMEPATH = resume;
        this.MODPATH = mod;
        this.FREEPATH = free;
        this.STATUSPATH = status;
    }
}
