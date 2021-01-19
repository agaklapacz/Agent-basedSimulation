package model.jade;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;


public class WaitForBehaviourAlert extends CyclicBehaviour {

    private HumanAgent humanAgent;

    public WaitForBehaviourAlert(HumanAgent humanAgent) {
        this.humanAgent = humanAgent;
    }

   @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null && msg.getPerformative() == ACLMessage.INFORM) {
            humanAgent.setOnAlert(true);
        }
        else {
            block();
        }
    }
}
