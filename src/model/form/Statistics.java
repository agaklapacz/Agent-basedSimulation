package model.form;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Statistics {

    private Map<Behaviour, Integer> numberOfDead;
    private Map<Behaviour, Integer> numberOfExit;

    public Statistics() {
        numberOfDead = new HashMap<>();
        numberOfExit = new HashMap<>();
    }

    public void kill(Behaviour behaviour){
        if (numberOfDead.containsKey(behaviour))
            numberOfDead.replace(behaviour, numberOfDead.get(behaviour) + 1);
        else
            numberOfDead.put(behaviour, 1);
    }

    public void exit(Behaviour behaviour) {
        if (numberOfExit.containsKey(behaviour))
            numberOfExit.replace(behaviour, numberOfExit.get(behaviour) + 1);
        else
            numberOfExit.put(behaviour, 1);
    }

    public int getNumberOfDead() {
        return numberOfDead.values().stream().reduce(0, Integer::sum);
    }

    public Map<Behaviour, Integer> getNumberOfDeadByBehaviour() {
        return numberOfDead;
    }

    public int getNumberOfExit() {
        return numberOfExit.values().stream().reduce(0, Integer::sum);
    }

    public Map<Behaviour, Integer> getNumberOfExitByBehaviour() {
        return numberOfExit;
    }

    public String getTheFinalReport() {
        StringBuilder stringBuilder = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("#.0");

        stringBuilder.append("\n### FINAL REPORT ###\n\n");

        stringBuilder
                .append("Number of survivors: ")
                .append(this.getNumberOfExit())
                .append("/")
                .append(Constant.NumberOfPeople)
                .append(" (")
                .append(decimalFormat.format(((double) this.getNumberOfExit() / Constant.NumberOfPeople) * 100))
                .append("%)\n");

        this.getNumberOfExitByBehaviour().forEach((behaviour, integer) -> {
            stringBuilder
                    .append("| ")
                    .append(behaviour.getName())
                    .append(" : ")
                    .append(integer)
                    .append("/")
                    .append(this.getNumberOfExit())
                    .append(" (")
                    .append(decimalFormat.format(((double) integer / this.getNumberOfExit()) * 100))
                    .append("%)\n");
        });

        stringBuilder.append("\n");

        stringBuilder
                .append("Number of dead : ")
                .append(this.getNumberOfDead())
                .append("/")
                .append(Constant.NumberOfPeople)
                .append(" (")
                .append(decimalFormat.format(((double) this.getNumberOfDead() / Constant.NumberOfPeople) * 100))
                .append("%)\n");

        this.getNumberOfDeadByBehaviour().forEach((behaviour, integer) -> {
            stringBuilder
                    .append("| ")
                    .append(behaviour.getName())
                    .append(" : ")
                    .append(integer)
                    .append("/")
                    .append(this.getNumberOfDead())
                    .append(" (")
                    .append(decimalFormat.format(((double) integer / this.getNumberOfDead()) * 100))
                    .append("%)\n");
        });

        return stringBuilder.toString();
    }
}
