package algosound.data.visuals;

/**
 * Abstract class for visual elements of any type.
 * ================================
 *
 * @author ekzyis
 * @date 17/02/2018
 */
public abstract class Visual {
    protected int x, y, w, h;
    protected processing.core.PApplet inst;

    public Visual(int _x, int _y, int _w, int _h, processing.core.PApplet _inst) {
        x = _x;
        y = _y;
        w = _w;
        h = _h;
        inst = _inst;
    }

    public abstract void show();

    public int getX() {
        return x;
    }

    public void setX(int _x) {
        this.x = _x;
    }

    public int getY() {
        return y;
    }

    public void setY(int _y) {
        this.y = _y;
    }

    public int getWidth() {
        return w;
    }

    public void setWidth(int _w) {
        this.w = _w;
    }

    public int getHeight() {
        return h;
    }

    public void setHeight(int _v) {
        this.h = _v;
    }
}
