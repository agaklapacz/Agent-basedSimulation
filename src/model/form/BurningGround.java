package model.form;

public class BurningGround extends Superposable{

    public BurningGround(int x, int y) {
        super(x, y);
        this.x = x;
        this.y = y;
        setSize(0);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
