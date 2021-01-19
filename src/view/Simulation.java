package view;

import model.form.*;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.simple.ImagePortrayal2D;
import sim.portrayal.simple.RectanglePortrayal2D;

import javax.swing.*;
import java.awt.*;

public class Simulation extends GUIState {

    SparseGridPortrayal2D portrayal = new SparseGridPortrayal2D();
    public Display2D display;
    public JFrame jFrame;
    
    public Simulation(SimState state) {
        super(state);
    }

    public static String getName() {
        return "Agent-based evacuation simulation. ";
    }

    public void start() {
        super.start();
        createRepresentations();
    }

    public void load(SimState state) {
        super.load(state);
        createRepresentations();
    }

    public void createRepresentations() {
        Environment environment = (Environment) state;
        portrayal.setField(environment.grid);
        portrayal.setPortrayalForClass(Human.class, getAgentHumanRepresentation());
        portrayal.setPortrayalForClass(Fire.class, getAgentFireRepresentation());
        portrayal.setPortrayalForClass(Wall.class, getAgentWallRepresentation());
        portrayal.setPortrayalForClass(Exit.class, getAgentExitRepresentation());
        portrayal.setPortrayalForClass(FalseExit.class, getAgentFalseExitRepresentation());
        portrayal.setPortrayalForClass(BurningGround.class, getBurningGround());
        portrayal.setPortrayalForClass(Body.class, getAgentCorpsRepresentation());
        portrayal.setPortrayalForClass(CarsIn.class, getAgentCarInRepresentation());
        portrayal.setPortrayalForClass(CarsOut.class, getAgentCarOutRepresentation());

        display.reset();
        display.setBackdrop(Color.white);
        display.repaint();
    }

    @Override
    public Object getSimulationInspectedObject() {
        return super.state;
    }

    private ImagePortrayal2D getAgentHumanRepresentation() {
        return new ImagePortrayal2D(new ImageIcon("images/happy.png")) {
            @Override
            public void draw(Object o, Graphics2D graphics, DrawInfo2D info) {
                if (o instanceof Human) {
                    if (((Human) o).is(Status.OnTheGround)) {
                        if (((Human) o).is(Status.InFire))
                            image = new ImageIcon("images/onTheGroundInFire.png").getImage();
                        else
                            image = new ImageIcon("images/onTheGround.png").getImage();
                    }else if (((Human) o).is(Status.OnAlert)) {
                        if (((Human) o).is(Status.InFire))
                            image = new ImageIcon("images/onAlertInFire.png").getImage();
                        else
                            image = new ImageIcon("images/onAlert.png").getImage();
                    }else {
                        image = new ImageIcon("images/happy.png").getImage();
                    }
                }
                super.draw(o, graphics, info);
            }
        };
    }

    private ImagePortrayal2D getAgentCorpsRepresentation() {
        return new ImagePortrayal2D(new ImageIcon("images/scull.png"));
    }

    private RectanglePortrayal2D getAgentFireRepresentation() {
        RectanglePortrayal2D r = new RectanglePortrayal2D();
        r.paint = new Color(200, 0, 0, 100);
        return r;
    }

    private ImagePortrayal2D getAgentExitRepresentation() {
        return new ImagePortrayal2D(new ImageIcon("images/exit.PNG")) {
            @Override
            public void draw(Object o, Graphics2D graphics, DrawInfo2D info) {
                if (o instanceof Exit)
                    if (((Exit) o).getX() == 0 || ((Exit) o).getX() == Constant.horizontalGridSize - 1)
                        image = new ImageIcon("img/exit.png").getImage();
                    else
                        image = new ImageIcon("img/exit.png").getImage();

                super.draw(o, graphics, info);
            }
        };
    }

    private ImagePortrayal2D getAgentCarInRepresentation() {
        return new ImagePortrayal2D(new ImageIcon("images/car.PNG"));
    }

    private ImagePortrayal2D getAgentCarOutRepresentation() {
        return new ImagePortrayal2D(new ImageIcon("images/carOut.PNG"));
    }

    private ImagePortrayal2D getAgentWallRepresentation() {
        return new ImagePortrayal2D(new ImageIcon("images/wall.PNG")) {
            @Override
            public void draw(Object o, Graphics2D graphic, DrawInfo2D info){
                if (o instanceof Wall)
                    if (((Wall) o).getX() == 0 || ((Wall) o).getX() == Constant.horizontalGridSize - 1)
                        image = new ImageIcon("images/exit.PNG").getImage();
                    else
                        image = new ImageIcon("images/wall.PNG").getImage();

                    super.draw(o, graphic, info);
            }
        };
    }

    private ImagePortrayal2D getAgentFalseExitRepresentation() {
        return new ImagePortrayal2D(new ImageIcon("images/falseExit.PNG")) {
            @Override
            public void draw(Object o, Graphics2D graphics2D, DrawInfo2D info) {
                if(o instanceof FalseExit)
                    if(((FalseExit) o).getX() == 0 || ((FalseExit) o).getX() == Constant.horizontalGridSize - 1)
                        image = new ImageIcon("images/falseExit.PNG").getImage();
                    else
                        image = new ImageIcon("images/exit.PNG").getImage();
                    super.draw(o, graphics2D, info);
            }
        };
    }

    private RectanglePortrayal2D getBurningGround() {
        RectanglePortrayal2D r = new RectanglePortrayal2D();
        r.paint = new Color(200, 50, 0, 100);
        return r;
    }

    public void init(Controller c) {
        super.init(c);
        display = new Display2D(Constant.windowSizeHorizontal, Constant.windowSizeVertical, this);
        display.setClipping(false);
        jFrame = display.createFrame();
        jFrame.setTitle("Agent-based evacuation simulation. ");
        c.registerFrame(jFrame);
        jFrame.setVisible(true);
        display.attach(portrayal, "Yard");
    }
}
