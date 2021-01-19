package model.jade;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;

public class MainContainer {

    public MainContainer(String mainPropertiesFile) {

        try {
            Profile profile = new ProfileImpl(mainPropertiesFile);

            Runtime runtime = Runtime.instance();
            runtime.createMainContainer(profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void kill() {
        Runtime.instance().shutDown();
    }
}

