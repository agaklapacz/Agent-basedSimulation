package model.jade;

import jade.core.AID;
import jade.core.Agent;

public class HumanAgent extends Agent implements HumanAgentInterface{

    private boolean onAlert = false;

    //setup() - metoda, wykonywana natychmiast po uruchomieniu agenta
    @Override
    protected void setup() {
        addBehaviour( new WaitForBehaviourAlert(this));
    }

    @Override
    public boolean alertReceived() {
        return this.onAlert;
    }

    public void setOnAlert(boolean onAlert) {
        this.onAlert = onAlert;
    }

    @Override
    public void toAlert(String humanAgentName) {
        addBehaviour( new AlertBehaviour(this, new AID(humanAgentName, true)));
    }
/*
    //takeDown()
    protected void takeDown() {
    }*/
}
