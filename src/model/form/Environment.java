package model.form;

import javafx.util.Pair;
import model.jade.EnvironmentContainer;
import model.jade.HumanAgent;
import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;
import view.Constant;

import java.util.*;

public class Environment extends SimState {

    public SparseGrid2D grid = new SparseGrid2D(Constant.horizontalGridSize, Constant.verticalGridSize);
    private SuperposableFactory factory = new SuperposableFactory();

    private EnvironmentContainer jadeEnvironmentContainer;

    private Statistics statistics;

    public Environment(long seed) {
        super(seed);
    }

    @Override
    public void start() {
        System.out.println("Initialization of Jade.");
        jadeEnvironmentContainer = new EnvironmentContainer("model/jade/environment.properties");

        statistics = new Statistics();

        System.out.println("Simulation initialized.");
        grid.clear();
        super.start();

        addContour();
        addExits();
        addCarsEntering();
        addLeavingCars();
        addHumanAgents();
        addFire();
    }

    private void addFire() {
            Int2D location = getTheLocation();
            Fire fire = new Fire(location.x, location.y);
            grid.setObjectLocation(fire, location.x, location.y);
            fire.setStoppable(schedule.scheduleRepeating(fire));
    }

    //dodajemy samochody
    private void addCarsEntering() {
        for (int i = 0; i < model.form.Constant.Number_Of_Cars_Entering; i++) {
            Int2D location = getTheLocationForCarEntering();
            CarsIn cars = new CarsIn(location.x, location.y, model.form.Constant.Max_Cell_Capacity); //rozmiar auta to rozmiar jednej komórki
            grid.setObjectLocation(cars, location.x, location.y);
        }
    }

    private void addLeavingCars() {
        for (int i = 0; i < model.form.Constant.Number_Of_Leaving_Cars; i++) {
            Int2D location = getTheLocationForLeavingCars();
            CarsOut cars = new CarsOut(location.x, location.y, model.form.Constant.Max_Cell_Capacity); //rozmiar auta to rozmiar jednej komórki
            grid.setObjectLocation(cars, location.x, location.y);
        }
    }

    //dodajemy agentów
    private void addHumanAgents() {
        for (int i = 0; i < model.form.Constant.NumberOfHeroes; i ++) {
            HumanAgent agent = new HumanAgent();
            String agentName = "HumanAgent#" + UUID.randomUUID();
            jadeEnvironmentContainer.addAndStartAgent(agentName, agent);

            Int2D location = getTheLocation();
            Human human = new Human(location.x, location.y, agent, model.form.Constant.Hero);

            grid.setObjectLocation(human, location.x, location.y);
            human.setStoppable(schedule.scheduleRepeating(human));
        }
        for (int i = 0; i < model.form.Constant.NumberOfEgoists; i ++) {
            HumanAgent agent = new HumanAgent();
            String agentName = "HumanAgent#" + UUID.randomUUID();
            jadeEnvironmentContainer.addAndStartAgent(agentName, agent);

            Int2D location = getTheLocation();
            Human human = new Human(location.x, location.y, agent, model.form.Constant.Egoist);

            grid.setObjectLocation(human, location.x, location.y);
            human.setStoppable(schedule.scheduleRepeating(human));
        }
        for (int i = 0; i < model.form.Constant.NumberOfFearful; i ++) {
            HumanAgent agent = new HumanAgent();
            String agentName = "HumanAgent#" + UUID.randomUUID();
            jadeEnvironmentContainer.addAndStartAgent(agentName, agent);

            Int2D location = getTheLocation();
            Human human = new Human(location.x, location.y, agent, model.form.Constant.Fearful);

            grid.setObjectLocation(human, location.x, location.y);
            human.setStoppable(schedule.scheduleRepeating(human));
        }
    }

    public void addFire(int x, int y) {
        Fire fire = new Fire(x, y);
        grid.setObjectLocation(fire, x, y);
        fire.setStoppable(schedule.scheduleRepeating(fire));
    }

    public void removeFire(Fire fire) {
        grid.setObjectLocation(new BurningGround(fire.getX(), fire.getY()), fire.getX(), fire.getY());
        fire.getStoppable().stop();
        grid.remove(fire);
    }

    //dodaj kontur (tunelu)
    private void addContour() {
        for (int i = 0; i < grid.getWidth(); i ++) {
            grid.setObjectLocation(new Wall(i, 0), i, 0);
            grid.setObjectLocation(new Wall(i, grid.getHeight()-1), i, grid.getHeight()-1);

        }
        for (int j = 1; j < grid.getHeight() - 1; j++) {

            grid.setObjectLocation(new Exit(1,0, j), 0, j);
            grid.setObjectLocation(new Exit(1,grid.getWidth() - 1, j), grid.getWidth() - 1, j);
        }
    }

    //dodaj wyjścia ewakuacyjne
    private void addExits() {

        int NumberOfExitInOneSide = model.form.Constant.Number_Of_Exits / 2;
        int distance = grid.getWidth() / (NumberOfExitInOneSide+1) ;
        int x = 0;
        int y;
        int[] row = new int[grid.getWidth()-1];
        int tmp = x;

        for (int i = 0; i < grid.getWidth()-2; i ++) {
            row[i] = x;
            y = 0;
            if (tmp == distance){
                grid.removeObjectsAtLocation(x, y);
                Exit exit = new Exit(1, x, y);
                grid.setObjectLocation(exit, x, y);

                tmp = 0;
            }
            tmp ++;
            x ++;
        }

        x = 0;
        tmp = x;

        for (int i = 0; i < grid.getWidth()-2; i ++) {
            row[i] = x;
            y = grid.getHeight() - 1;
            if (tmp == distance){
                grid.removeObjectsAtLocation(x, y);
                Exit exit = new Exit(1, x, y);
                grid.setObjectLocation(exit, x, y);
                tmp =0;
            }
            tmp ++;
            x ++;
        }
    }

    public Exit getNearestExit(int x, int y) {

        Map<Exit, Integer> outputByDistance = new HashMap<>(model.form.Constant.Number_Of_Exits);

        for (int i = 0; i < grid.getWidth(); i ++)
            for (int j = 0; j < grid.getHeight(); j ++)
                if (grid.getObjectsAtLocation(i, j) != null) {
                    Arrays.stream(grid.getObjectsAtLocation(i, j).objs).forEach(o -> {
                        if (o instanceof Exit)
                            outputByDistance.put((Exit) o, calculateDistance(x, y, ((Exit) o).x, ((Exit) o).y));
                    });
                }
        if (outputByDistance.isEmpty())
            return null;
        return outputByDistance.entrySet().stream().min(Comparator.comparingInt(Map.Entry::getValue)).get().getKey();
    }

    public static int calculateDistance (int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt((y2-y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    private List<Superposable> getListedGridObject() {
        List<Superposable> x = new ArrayList<>(Constant.verticalGridSize * Constant.horizontalGridSize * 5);  //<- poczatkowa pojemnoisć
        int sizeBag;

        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j ++) {
                if (grid.getObjectsAtLocation(i, j) != null) {
                    sizeBag = grid.getObjectsAtLocation(i, j).numObjs;
                    for (int idx = 0; idx < sizeBag; idx ++)
                        x.add((Superposable) grid.getObjectsAtLocation(i, j). objs[idx]);
                } else {
                    x.add(this.factory.getSuperposableFactory(i, j));
                }
            }
        }
        return x;
    }

    public List<Superposable> getSortedObjectInList(Human human, int visionScope) {
        List<Superposable> toSortList = getListedGridObject();

        toSortList.sort((s1, s2) -> {
            if (calculateDistance(human.getX(), human.getY(), s1.getX(), s1.getY()) == calculateDistance(human.getX(), human.getY(), s2.getX(), s2.getY() ))
                return 0;
            return calculateDistance(human.getX(), human.getY(), s1.getX(), s1.getY()) < calculateDistance(human.getX(), human.getY(), s2.getX(), s2.getY()) ? -1 : 1;
        });

        toSortList.remove(0);
        return toSortList.subList(0, visionScope);
    }

    public List<Pair<Integer, Integer>> getNonTraversables(boolean isFireTraversable) {
        List<Pair<Integer, Integer>> nonTraversable = new ArrayList<>();
        for (int i = 0; i < grid.getWidth(); i ++) {
            for (int j = 0; j < grid.getHeight(); j ++) {
                if (grid.getObjectsAtLocation(i, j) != null) {
                    if (Superposable.ifCellFull(this, i, j))
                        nonTraversable.add(new Pair<>(i, j));
                    else if (!isFireTraversable && isOnFire(i, j))
                        nonTraversable.add(new Pair<>(i, j));
                }
            }
        }
        return nonTraversable;
    }

    public boolean isOnFire(int x, int y) {
        if (this.grid.getObjectsAtLocation(x, y) == null) {
            return false;
        }
        for (Object o : this.grid.getObjectsAtLocation(x, y).objs) {
            if(o instanceof Fire) return true;
        }
        return false;
    }

    private Int2D getTheLocation() {
        Int2D location = new Int2D(random.nextInt(grid.getWidth()), random.nextInt(grid.getHeight()));
        Object x;
        while ((x = grid.getObjectsAtLocation(location.x, location.y)) != null) {
            location = new Int2D(random.nextInt(grid.getWidth()), random.nextInt(grid.getHeight()));
        }
        return location;
    }

    private Int2D getTheLocationForCarEntering() {
        Int2D location1 = new Int2D(random.nextInt(grid.getWidth()), 3);
        Object tmp;
        while ((tmp = grid.getObjectsAtLocation(location1.x, location1.y)) != null) {
            location1 = new Int2D(random.nextInt(grid.getWidth()), 3);
        }
        return location1;
    }

    private Int2D getTheLocationForLeavingCars() {
        Int2D location1 = new Int2D(random.nextInt(grid.getWidth()), 2);
        Object tmp;
        while ((tmp = grid.getObjectsAtLocation(location1.x, location1.y)) != null) {
            location1 = new Int2D(random.nextInt(grid.getWidth()), 2);
        }
        return location1;
    }

    public void kill(Human human) {
        grid.remove(human);
        grid.setObjectLocation(new Body(human.getX(), human.getY()), human.getX(), human.getY());
        human.getStoppable().stop();

        statistics.kill(human.getBehaviour());
    }

    public void goOut(Human human) {
        grid.remove(human);
        human.getStoppable().stop();

        statistics.exit(human.getBehaviour());
    }

    @Override

    public void finish() {
        System.out.println("Simulation finished. \n Printing the report ...");
        System.out.println(statistics.getTheFinalReport());
        jadeEnvironmentContainer.kill();
    }
}
