import processing.core.*;

public class PAppletExample extends PApplet
{
    public void settings()
    {
        size(640,360);
    }

    public void draw()
    {
        background(25);
        ellipse(mouseX,mouseY,10,10);
    }

    public static void main(String[] passedArgs)
    {
        String[] appletArgs = new String[] { "PAppletExample" };
        if (passedArgs != null)
        {
            PApplet.main(concat(appletArgs, passedArgs));
        }
        else
        {
            PApplet.main(appletArgs);
        }
    }
}
