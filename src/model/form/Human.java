package model.form;

import javafx.util.Pair;
import model.jade.HumanAgent;
import model.jade.HumanAgentInterface;
import model.pathfinding.Node;
import model.pathfinding.Star;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.util.Bag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Human extends Superposable implements Steppable, HumanAgentInterface {

    private boolean[][] visionMask;
    private int hp = Constant.HP;
    private List<Status> statuses;
    private HumanAgent humanAgent;
    private Behaviour behaviour;
    private Stoppable  stoppable;

    public Human (int x, int y, HumanAgent humanAgent, Behaviour behaviour) {
        super(x, y);

        visionMask = new boolean[view.Constant.horizontalGridSize][view.Constant.verticalGridSize];
        resetMask();

        this.statuses = new ArrayList<>();
        setSize(1);

        this.behaviour = behaviour;
        this.humanAgent = humanAgent;
    }

    @Override
    public void step(SimState simState) {
        Environment environment = (Environment) simState;

        Exit nearestExit = environment.getNearestExit(x,y);

        if (hp <= 0 || (nearestExit != null && isOut(environment, nearestExit)))
            return;

        if (!is(Status.OnAlert) && alertReceived())
            addStatus(Status.OnAlert);

        if (is(Status.OnTheGround)){
            getTrampled(environment);
            tryToRise(environment);
        }

        if(this.behaviour.pushSbOver && !this.is(Status.OnTheGround)) {
            push(environment);
        }

        if (is(Status.InFire)) {
            burn();
        }
        else {
            potentiallyCatchFire(environment);
        }

        perceive(environment);

        if (is(Status.OnAlert) && !this.is(Status.OnTheGround)) {
            humansAlert(environment);

            if (nearestExit == null)
                randomDisplacement(environment);
            else if (!this.behaviour.quenchSb && !this.behaviour.helpSbUp) {
                tryToGetOut(environment, nearestExit);
            }
            else {
                if(this.behaviour.quenchSb) {
                    boolean off = quenchSb(environment);
                    if (!off) {
                        tryToGetOut(environment, nearestExit);
                    }
                }
                if (this.behaviour.helpSbUp) {
                    boolean off = helpSbUp(environment);
                    if (!off) {
                        tryToGetOut(environment, nearestExit);
                    }
                }
            }
        }else {
            sToAlert(environment);
        }
        if (nearestExit != null && isOut(environment, nearestExit))
            environment.goOut(this);
        if (hp <= 0)
            environment.kill(this);
    }

    private boolean randomDisplacement (Environment environment) {
        switch (new Random().nextInt(4 - 1 + 1) + 1) {
            case 1:
                return tryToMove(environment, x + 1, y);
            case 2:
                return tryToMove(environment, x, y + 1);
            case 3:
                return tryToMove(environment, x - 1, y);
            case 4:
                return tryToMove(environment, x, y - 1);
        }
        return false;
    }

    private void sToAlert(Environment environment) {
        for (int i = 0; i < view.Constant.horizontalGridSize; i++) {
            for (int j = 0; j < view.Constant.verticalGridSize; j ++) {
                Bag bag = environment.grid.getObjectsAtLocation(i, j);
                if (visionMask[i][j] && bag != null && Arrays.stream(bag.objs).anyMatch(s -> s instanceof Fire)) {
                    addStatus(Status.OnAlert);
                    return;
                }
            }
        }
    }

    private boolean quenchSb (Environment environment) {
        boolean off = false;
        if (environment.grid.getObjectsAtLocation(this.x, this.y) == null) {
            return off = false;
        }
        for (Object o : environment.grid.getObjectsAtLocation(this.x, this.y).objs) {
            if (o instanceof Human && o != this && ((Human) o).is(Status.InFire)) {
                ((Human) o).removeStatus(Status.InFire);
                int chanceToBurn = (int) (Math.random()*4);
                if (chanceToBurn == 0 && !this.is(Status.InFire)) {
                    this.addStatus(Status.InFire);
                }
                off = true;
            }
        }
        return off;
    }

    private boolean helpSbUp(Environment environment) {
        boolean off = false;
        if ( environment.grid.getObjectsAtLocation(this.x, this.y) == null) {
            return off;
        }
        for (Object o : environment.grid.getObjectsAtLocation(this.x, this.y).objs) {
            if(o instanceof Human && o != this && ((Human) o).is(Status.OnTheGround)) {
                ((Human) o).removeStatus(Status.OnTheGround);
                off = true;
            }
        }
        return off;
    }

    private boolean tryToGetOut(Environment environment, Exit exit) {
        Star star = new Star(
                environment.grid.getWidth(),
                environment.grid.getHeight(),
                this,
                exit.getX(),
                exit.getY());

        List<Pair<Integer, Integer>> nonTraversables =environment.getNonTraversables(this.behaviour.walkOnFire);
        List<Node> path;
        int[][] blockArray = new int[nonTraversables.size()][2];

        for(int i = 0; i < blockArray.length; i++){
            for(int j = 0; j < blockArray[0].length; j++){
                if(j == 0)
                    blockArray[i][j] = nonTraversables.get(i).getKey();
                else
                    blockArray[i][j] = nonTraversables.get(i).getValue();
            }
        }

        star.setBlocks(blockArray);
        path = star.findPath();

        if (path.size() == 0 && !is(Status.InFire)) {
            environment.grid.remove(exit);
            environment.grid.setObjectLocation(new FalseExit(exit.getX(), exit.getY()), exit.getX(), exit.getY());
        }



        try {
            tryToMove(environment, path.get(1).getRow(), path.get(1).getCol());
        } catch(IndexOutOfBoundsException e){
            return false;
        }
        return true;
    }

    private boolean canMove(Environment environment, int x, int y) {
        return !Superposable.ifCellFull(environment, x, y)
                && !is(Status.OnTheGround)
                && Math.abs(this.x - x) <= 1
                && Math.abs(this.y - y) <= 1
                && (this.x == x || this.y == y);
    }

    private boolean tryToMove(Environment environment, int x, int y) {
        if (canMove(environment, x, y) && (environment.grid.setObjectLocation(this, x, y))) {
            this.x = x;
            this.y = y;
            return true;
        }
        return false;
    }

    public boolean isOut (Environment environment, Exit exit) {
        return exit.getX() == this.x && exit.getY() == this. getY();
    }

    public boolean is(Status status) {
        return this.statuses.contains(status);
    }

    public void addStatus(Status status) {
        if(!is(status))
            this.statuses.add(status);
    }

    public void removeStatus(Status status) {
        if (is(status))
            this.statuses.remove(status);
    }

    private void tryToRise(Environment environment) {
          if(is(Status.InFire))
              return;
          int tmp = (int) (Math.random() * 4);
          if (tmp == 0)
              removeStatus(Status.OnTheGround);
    }

    private void getTrampled(Environment environment) {
        if (environment.grid.getObjectsAtLocation(this.x, this.y) == null) {
            return;
        }
        for(Object o : environment.grid.getObjectsAtLocation(this.x, this.y).objs) {
            if (o instanceof Human && o != this && !((Human)o).is(Status.OnTheGround)) {
                hp -= 1;
            }
        }
    }

    private void push (Environment environment) {
        if (environment.grid.getObjectsAtLocation(this.x, this.y) == null) {
            return;
        }
        for (Object o : environment.grid.getObjectsAtLocation(this.x, this.y).objs) {
            if (o instanceof Human && o != this && !((Human)o).is(Status.OnTheGround)) {
                ((Human)o).addStatus(Status.OnTheGround);
            }
        }
    }

    private void burn() {
        hp -= 2;
    }

    private void potentiallyCatchFire (Environment environment) {
        for(Object o : environment.grid.getObjectsAtLocation(this.x, this.y).objs) {
            if (o instanceof Fire) {
                this.addStatus(Status.InFire);
                return;
            }
            else if(o instanceof Human && o != this && ((Human)o).is(Status.InFire)) {
                this.addStatus(Status.InFire);
                return;
            }
        }
    }

    private void perceive(Environment environment) {
        List<Superposable> objSorted = environment.getSortedObjectInList(this, Constant.VisionScope);
        List<Superposable> objVisible = new ArrayList<>();
        boolean isObjectBetweenAB = false;

        //Pierwsza jest zawsze widoczna, ponieważ jest to obiekt najbliżej człowieka i usuwamy go, aby uniknąć dwukrotnego dodania
        objVisible.add(objSorted.get(0));
        objSorted.remove(0);

        for (Superposable b : objSorted){
            for (Superposable c : objVisible) {
                //Jeśli między A i B nie ma obiektu, a ABC są współliniowe
                if (!isObjectBetweenAB) {
                    if (areCollinear(b.x, b.y, c.x, c.y)) {
                        //Widzimy więc, czy C jest między A i B i czy jest zadowalające
                        isObjectBetweenAB = isBetweenTwoPoints(b.x, b.y, c.x, c.y) && !c.reversible();
                    }
                }
            }
            if (!isObjectBetweenAB)
                objVisible.add(b);
            isObjectBetweenAB = false;
        }
        updateMask(objVisible);
    }

    private void updateMask(List<Superposable> objVisible) {
        resetMask();
        for (Superposable visible : objVisible)
            this.visionMask[visible.x][visible.y] = true;
    }

    private void resetMask() {
        Arrays.stream(this.visionMask).forEach(line -> {
            Arrays.fill(line, false);
        });
    }

    private boolean areCollinear(int Bx, int By, int Cx, int Cy) {
        int BAx = Bx - this.x;
        int CAy = Cy - this.y;
        int BAy = By - this.y;
        int CAx = Cx - this.x;

        double tmp = BAx * CAy - BAy * CAx;
        if (tmp == 0)
            return true;
        else
            return false;
    }

    private boolean isBetweenTwoPoints(int Bx, int By, int Cx, int Cy) {

        int ACx = this.x - Cx;
        int ACy = this.y - Cy;
        int BCx = Bx - Cx;
        int BCy = By - Cy;

        double tmp = ((ACx * BCx) + (ACy * BCy));
        return tmp <= 0;
    }

    private void humansAlert(Environment environment) {
        for (int i = 0; i < view.Constant.horizontalGridSize; i ++) {
            for (int j = 0; j < view.Constant.verticalGridSize; j ++) {
                Bag bag = environment.grid.getObjectsAtLocation(i, j);
                if (visionMask[i][j] && bag != null) {
                    Arrays.stream(bag.objs).forEach(s -> {
                        if (s instanceof Human && !((Human) s).is(Status.OnAlert))
                            toAlert(((Human) s).getHumanAgent().getName());
                    });
                }
            }
        }
    }

    @Override
    public void toAlert(String humanAgentName) {
        this.humanAgent.toAlert(humanAgentName);
    }

    @Override
    public boolean alertReceived() {
        return this.humanAgent.alertReceived();
    }

    public HumanAgent getHumanAgent() {
        return humanAgent;
    }

    public Stoppable getStoppable() {
        return stoppable;
    }

    public void setStoppable(Stoppable stoppable) {
        this.stoppable = stoppable;
    }

    public Behaviour getBehaviour() {
        return behaviour;
    }
}
