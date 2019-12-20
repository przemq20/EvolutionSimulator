import java.io.FileNotFoundException;

public class World {
    static final String parametersPath = "parameters.json";

    public static void main(String[] args) throws FileNotFoundException {
        try {
            System.out.println(System.getProperty("user.dir") + "/" + parametersPath);
            final Configuration config = Configuration.fromJson(World.parametersPath);
            //System.out.println(config.toString());

            WorldMap map = new WorldMap(
                    config.width, config.height, config.jungleWidth(), config.jungleHeight(), config.grassEnergy, config.moveEnergy, config.startEnergy,
                    config.grassSpawned
            );

            Simulation simulation = new Simulation(
                    map,
                    config.simulationTimeMs,
                    config.startAnimals
            );
            simulation.startSimulation();

        } catch (FileNotFoundException ex) {
            System.out.println("Application cannot be launched!");
        } catch (IllegalArgumentException ex) {
            System.out.println("Illegal configuration! Check parameters.json file.\n" + ex.toString());
        }
    }
}