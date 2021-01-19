package model.form;

import view.Constant;

public class SuperposableFactory {

    private Superposable[][] grid;

    public SuperposableFactory() {
        grid = new Superposable[Constant.horizontalGridSize][Constant.verticalGridSize];

        for (int i = 0; i < grid.length; i ++) {
            for (int j = 0; j < grid[0]. length; j ++) {
                grid[i][j] = new Superposable(i, j) {};
            }
        }
    }

    public Superposable getSuperposableFactory (int i, int j) {
        return grid[i][j];
    }

}
