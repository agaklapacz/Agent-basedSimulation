package model.form;

public class Exit extends Inactive{

    public Exit(int output,int x, int y) {
        super(x, y);

        if (output >= Constant.Max_Cell_Capacity)
            throw new IllegalArgumentException("The flow must be less than the maximum capacity of a cell");

        if (output <= 0)
            throw new IllegalArgumentException("The flow must be strictly greater than 0");

        setSize(Constant.Max_Cell_Capacity - output);

    }
}
