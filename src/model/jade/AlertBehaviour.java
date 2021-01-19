package model.jade;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;

public class AlertBehaviour extends OneShotBehaviour {
    private HumanAgent sender;        //nmadawca
    private AID receiver;            //odbiorca

    public AlertBehaviour(HumanAgent humainAgent, AID aid) {
        this.sender = humainAgent;
        this.receiver = aid;
    }

    @Override
    public void action() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        msg.addReceiver(receiver);
        sender.send(msg);
    }
}
