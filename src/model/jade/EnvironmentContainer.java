package model.jade;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class EnvironmentContainer {

    private Profile profile;
    private Runtime runtime;
    private ContainerController containerController;

    public EnvironmentContainer(String propertiesFile) {
        try {
            profile = new ProfileImpl(propertiesFile);
            runtime = Runtime.instance();
            containerController = runtime.createAgentContainer(profile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addAndStartAgent(String name, HumanAgent humanAgent) {
        try {
            containerController.acceptNewAgent(name, humanAgent).start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        try {
            containerController.kill();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
