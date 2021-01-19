import model.form.Environment;
import model.jade.MainContainer;
import sim.display.Console;
import view.Simulation;

public class Main {
    public static void main(String[] args) {
        new MainContainer("model/jade/main.properties");
        runUI();
    }

    public static void runUI() {
        Environment model = new Environment(System.currentTimeMillis());
        Simulation view = new Simulation(model);
        Console console = new Console(view);
        console.setVisible(true);
    }
}
