package model.form;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

public class Fire extends Superposable implements Steppable {

    private int lifetime;
    private Stoppable stoppable;

    public Fire(int x, int y) {
        super(x, y);
        this.lifetime = Constant.FireLifetime;
        setSize(0);
    }

    @Override
    public void step(SimState simState) {

        Environment environment = (Environment)simState;
        int extinguishProbability = (int) (Math.random() * 10);
        if (extinguishProbability == 0)
            lifetime --;

        if (lifetime > 0) {
            spread(environment);
            lifetime --;
        }
        else
            extinguish(environment);
    }

    private void spread(Environment environment) {

        int tmp = (int)(Math.random() * 1);
        if (canItSpread(environment, this.x + 1, this.y) && tmp == 0)
            environment.addFire(this.x + 1, this.y);

        tmp = (int)(Math.random() * 3);
        if (canItSpread(environment, x - 1, y) && tmp == 0)
            environment.addFire(this.x - 1, this.y);

        tmp = (int)(Math.random() * 3);
        if (canItSpread(environment, x, y + 1) && tmp == 0)
            environment.addFire(this.x, this.y + 1);

        tmp = (int)(Math.random() * 3);
        if (canItSpread(environment, x, y - 1) && tmp == 0)
            environment.addFire(this.x, this.y - 1);
    }

    private boolean canItSpread(Environment environment, int x, int y) {
        if (environment.grid.getObjectsAtLocation(x, y) == null) {
            return true;
        }
        for (Object o : environment.grid.getObjectsAtLocation(x, y).objs) {
            if (o instanceof Fire || o instanceof Wall || o instanceof BurningGround ||
                    o instanceof Exit || o instanceof FalseExit)
                return false;

        }
        return true;
    }


    private void extinguish(Environment environment) {
        environment.removeFire(this);
    }

    public int getX() { return x;}
    public void setX(int x) { this.x = x;}

    public int getY() { return y;}
    public void setY(int y) { this.y = y;}


    public Stoppable getStoppable() {
        return stoppable;
    }

    public void setStoppable(Stoppable stoppable) {
        this.stoppable = stoppable;
    }
}
