package model.form;

public abstract class Superposable {

    private int size = 0;
    int x;
    int y;

    public Superposable(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (size < 0 || size > Constant.Max_Cell_Capacity)
            throw new IllegalArgumentException("The size must be between 0 nad Max_Cell_Capacity");
        this.size = size;
    }

    public void impassableSet() {
        this.setSize(Constant.Max_Cell_Capacity);
    }

    public boolean reversible() {
        return size < Constant.Max_Cell_Capacity;
    }

    public static int getCellSize(Environment environment, int xCell, int yCell) {
        int size = 0;
        if (environment.grid.getObjectsAtLocation(xCell,yCell) != null) {
            for (Object superposable : environment.grid.getObjectsAtLocation(xCell,yCell)) {
                if (superposable instanceof Superposable)
                    size += ((((Superposable) superposable).getSize()));
            }
        }
        return size;
    }

    public static boolean ifCellFull(Environment environment, int xCell, int yCell) {
        return getCellSize(environment, xCell, yCell) >= Constant.Max_Cell_Capacity;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
