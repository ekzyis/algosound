package algosound.data.algorithms;

import algosound.data.Sonification;
import algosound.net.OSC;
import algosound.util.AlgosoundUtil;

import static algosound.util.AlgosoundUtil.expmap;
import static processing.core.PApplet.map;

/**
 * Selectionsort implementation.
 * Handling of new frames and sending osc messages for sonification of selectionsort.
 * NOTE: The mod path does include two paths. They are meant to be splitted with the regex-pattern "~".
 * ================================
 *
 * @author ekzyis
 * @date 08/03/2018
 */
public class Selectionsort extends SortingThread {

    // Sonification variants for selectionsort.
    private static final Sonification WAVE = new Sonification("WAVE", "/wave_start_SELECTIONSORT", "/wave_pause_SELECTIONSORT", "/wave_resume_SELECTIONSORT", "/wave_set_SELECTIONSORT~/min_set_SELECTIONSORT",
            "/wave_free_SELECTIONSORT", "/hellowave_SELECTIONSORT", "/boot_wave_SELECTIONSORT");
    private static final Sonification SCALE = new Sonification("SCALE","/scale_start_SELECTIONSORT", "", "", "/scale_play_SELECTIONSORT~/scale_play_SELECTIONSORT", "", "/helloscale_SELECTIONSORT",
            "/boot_scale_SELECTIONSORT");
    private final int FREQ_MIN = 200, FREQ_MAX = 4000;

    public Selectionsort(int N) {
        super(N);
        name = "Selectionsort";
        sonifications.add(WAVE);
        sonifications.add(SCALE);
        selected_sonification = WAVE;
    }

    @Override
    public void run()
    {
        System.out.println("--- selectionsort-thread has started.");

        OSC osc = OSC.getInstance();
        Sonification sel = selected_sonification;
        if(sel == WAVE) {
            osc.sendMessage(sel.STARTPATH);
        } else if(sel == SCALE) {
            int[] args = {FREQ_MIN, FREQ_MAX};
            osc.sendMessage(sel.STARTPATH, args);
        }
        // Gain access to monitor. If not possible, wait here.
        synchronized(this)
        {
            // Wait until first frame has been drawn.
            notifyFrameReady();
            /**
             * ==================================
             * Start of actual sorting algorithm.
             * ==================================
             */
            int start = 0;
            do
            {
                int minIndex = start;
                for(int i=minIndex+1;i<a.length & !isInterrupted();++i)
                {
                    int arg1 = expmap(a[i], 0, AlgosoundUtil.H, FREQ_MIN, FREQ_MAX);
                    float pan = map(i, 0, elements.length-1, -1, 1);
                    if(a[i]<a[minIndex])
                    {
                        minIndex = i;
                        int arg2 = expmap(a[minIndex],0, AlgosoundUtil.H, FREQ_MIN, FREQ_MAX);
                        float[] args = {arg2, 0};
                        osc.sendMessage(sel.MODPATH.split("~")[1], args);
                    }
                    // Mark element which is getting compared with current smallest element.
                    mark(minIndex);
                    mark(i);
                    float[] args = {arg1, pan};
                    osc.sendMessage(sel.MODPATH.split("~")[0], args);
                    notifyFrameReady();
                }
                int tmp = a[minIndex];
                a[minIndex] = a[start];
                a[start] = tmp;
                swap(minIndex,start);
                elements[start].setSorted();
                notifyFrameReady();
                start++;
            }while(start<a.length & !isInterrupted());
        }
        osc.sendMessage(sel.FREEPATH);
        System.out.println("--- selectionsort-thread has terminated.");
    }
}
