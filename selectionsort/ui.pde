/*
* @Author: ekzyis
* @Date:   11-02-2018 18:58:22
* @Last Modified by:   ekzyis
* @Last Modified time: 16-02-2018 22:05:11
*/
/**
 * Handling of GUI and Events.
 * ===========================
 */

/**
 * Initialize user interface which consists of buttons at the right side.
 */
void initGUI()
{
    // Create the y-coordinates for the buttons and save them in an array.
    Button.autoWidth = 50;
    Button.autoHeight = 20;
    int yInset = 10;
    int len = (int)(H/(yInset+Button.autoHeight));
    int[] yPos = new int[len];
    int y0 = yInset+Button.autoHeight;
    int x0 = W+10;
    for(int i=0;i<len;++i)
    {
        yPos[i] = (i+1)*y0 - Button.autoHeight;
    }
    start = cp5.addButton("start/pause").setPosition(x0,yPos[0]).setLabel("Start");
    /**
     * Naming the button like the exit()-function triggers the function when pressing
     * thus no need of defining a if-Statement for this button in controlEvent().
     */
    exit = cp5.addButton("exit").setPosition(x0,yPos[len-1]).setLabel("Exit");
    reset = cp5.addButton("reset").setPosition(x0,yPos[1]).setLabel("Reset");
    change = cp5.addButton("change").setPosition(x0,yPos[2]).setLabel("SCALE");
}

/**
 * Eventhandling of user interface.
 * TODO:
 * ---Bugfix#2
 *      When immediately pressing a button after opening the sketch,
 *      a InvocationTargetException is thrown but sketch keeps running fine after that.
 */
void controlEvent(ControlEvent event)
{
    Controller c = event.getController();
    if(c==start)
    {
        String currentLabel = c.getLabel();
        // Do action corresponding to current label.
        if(currentLabel.equals("Start"))
        {
            /**
             * Did thread already start? If not, start it.
             * (Execution never reaches this statement when it would be false since
             * the label will never be again "Start" so it's actually unnecessary.)
             */
            if(!sort.isAlive())
            {
                //println("---starting audio");
                sort.start();
                change.lock();
            }
            c.setLabel("Pause");
        }
        else if(currentLabel.equals("Pause"))
        {
            //println("---pause audio");
            sort.pause();
            c.setLabel("Resume");
        }
        else if(currentLabel.equals("Resume"))
        {
            //println("---resume audio");
            sort.unpause();
            c.setLabel("Pause");
        }
    }
    else if(c==reset)
    {
        start.setLabel("Start");
        sendMessage(OSC_FREEAUDIO);
        sort = new Selectionsort(N);
        change.unlock();
    }
    else if(c==change && !sort.isAlive())
    {
        listIndex++;
        listIndex = listIndex % sList.length;
        s = sList[listIndex];
        updatePaths();
        change.setLabel(s.NAME);
    }
}

void updatePaths()
{
    OSC_STARTAUDIO = s.STARTPATH;
    OSC_PAUSEAUDIO = s.PAUSEPATH;
    OSC_RESUMEAUDIO = s.RESUMEPATH;
    OSC_MODAUDIO1 = s.MODPATH[0];
    OSC_MODAUDIO2 = s.MODPATH[1];
    OSC_FREEAUDIO = s.FREEPATH;
    OSC_STATUS = s.STATUSPATH;
    OSC_BOOT = s.BOOTPATH;
}
