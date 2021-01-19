package model.form;

public class Wall extends Inactive{

    public Wall(int x, int y) {
        super(x, y);
        impassableSet();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
