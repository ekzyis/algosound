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
 * @date 10 February 2018
 */
public enum Sonification
{
    WAVE("/wave_start", "/wave_pause", "/wave_resume", new String[]{"/wave_set", "/min_set"}, "/wave_free", "/hellowave"),
    SCALE("/scale_start", "", "", new String[]{"/scale_play", "/scale_play"}, "", "/helloscale");
    public final String STARTPATH, PAUSEPATH, RESUMEPATH, FREEPATH, STATUSPATH;
    public final String[]  MODPATH;
    Sonification(String start, String pause, String resume, String[] mod, String free, String status)
    {
        this.STARTPATH = start;
        this.PAUSEPATH = pause;
        this.RESUMEPATH = resume;
        this.MODPATH = mod;
        this.FREEPATH = free;
        this.STATUSPATH = status;
    }
}
