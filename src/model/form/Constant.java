package model.form;

public class Constant {

    public static final int Number_Of_Cars_Entering = 5;
    public static final int Number_Of_Leaving_Cars = 5;
    public static final int Number_Of_Exits= 6;

    public static final int NumberOfHeroes = 5;
    public static final int NumberOfEgoists = 5;
    public static final int NumberOfFearful = 5;

    public final static int HP = 5;
    public static final int Max_Cell_Capacity = 5;

    public static final int FireLifetime = 15;
    public static final int VisionScope = 70;


    public final static Behaviour Hero = new Behaviour("Hero", true,false,true, true, true);
    public final static Behaviour Egoist = new Behaviour("Egoist", false,true, false, false, false);
    public final static Behaviour Fearful = new Behaviour("Fearful", false,true, false, false, true);

    public final static int NumberOfPeople = NumberOfHeroes + NumberOfEgoists + NumberOfFearful;
}
