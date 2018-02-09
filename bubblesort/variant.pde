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
    WAVE("/wave_start", "/wave_pause", "/wave_resume", "/wave_set", "/wave_free"),
    SCALE("/midi_start", "", "", "/midi_play", "");
    public final String STARTPATH, PAUSEPATH, RESUMEPATH, MODPATH, FREEPATH;
    Sonification(String start, String pause, String resume, String mod, String free)
    {
        this.STARTPATH = start;
        this.PAUSEPATH = pause;
        this.RESUMEPATH = resume;
        this.MODPATH = mod;
        this.FREEPATH = free;
    }
}
