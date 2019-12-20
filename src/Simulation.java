import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

public class Simulation implements ActionListener {

    private WorldMap map;
    private int startNumOfAnimals;
    private int days = 0;
    JFrame frame;
    private DrawPanel drawpanel;
    private Timer timer;
    final Configuration config = Configuration.fromJson(World.parametersPath);

    Simulation(WorldMap map, int timeOfOneDay, int startNumOfAnimals) throws FileNotFoundException {

        this.map = map;
        this.startNumOfAnimals = startNumOfAnimals;
        timer = new Timer(timeOfOneDay, this);

        frame = new JFrame("Evolution Simulator -" + " Day: " + days + ", Animals: " + map.getAnimalsCount() + ", Grass: " + map.getGrassCount() + " ");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize.width / 2, screenSize.height / 2);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        drawpanel = new DrawPanel(map, this);
        this.drawpanel.setSize(new Dimension(1, 1));
        frame.add(drawpanel);
    }

    void startSimulation() {
        for (int i = 0; i < startNumOfAnimals; i++) {
            map.addAnimalToMap();
            timer.start();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        map.newDay();
        days++;
        drawpanel.repaint();
        frame.setTitle("Evolution Simulator -" + " Day: " + days + ", Animals: " + map.getAnimalsCount() + ", Grass: " + map.getGrassCount() + " ");
    }
}
