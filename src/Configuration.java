import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class Configuration {
    final int width;
    final int height;
    final int startEnergy;
    final int moveEnergy;
    final int startAnimals;
    private final double jungleRatio;
    final int simulationTimeMs;
    final int grassEnergy;
    final int grassSpawned;


    Configuration() {
        this.width = this.height = this.startEnergy = this.moveEnergy = this.startAnimals = 0;
        this.simulationTimeMs = 0;
        this.grassEnergy = 0;
        this.jungleRatio = 0;
        this.grassSpawned = 0;
    }

    static Configuration fromJson(final String parametersPath) throws FileNotFoundException {
        Gson gson = new Gson();
        try {
            return gson.fromJson(new FileReader(System.getProperty("user.dir") + "/" + parametersPath), Configuration.class);
        } catch (FileNotFoundException ex) {
            System.out.println("Configuration file not found!\n Path: " + parametersPath + "\n" + ex.toString());
            throw ex;
        }
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "width=" + width +
                ", height=" + height +
                ", startEnergy=" + startEnergy +
                ", moveEnergy=" + moveEnergy +
                ", jungleRatio=" + jungleRatio +
                ", jungleHeight=" + jungleHeight() +
                ", jungleWidth=" + jungleWidth() +
                ", grassEnergy=" + grassEnergy +
                ", startAnimals=" + startAnimals +
                '}';
    }

    int jungleWidth() {
        return (int) (this.width * this.jungleRatio);
    }

    int jungleHeight() {
        return (int) (this.height * this.jungleRatio);
    }
}
