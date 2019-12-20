import javax.swing.*;
import java.awt.*;

public class DrawPanel extends JPanel {

    private WorldMap map;
    private Simulation simulation;

    DrawPanel(WorldMap map, Simulation simulation) {
        this.map = map;
        this.simulation = simulation;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setSize(simulation.frame.getWidth(), (simulation.frame.getHeight() - 38));
        int widthScale = (this.getWidth() / map.width);
        int heightScale = (this.getHeight() / map.height);

        g.setColor(new Color(103, 83, 74));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        g.setColor(new Color(41, 171, 135));
        g.fillRect(map.getJungleLowerLeft().x * widthScale,
                map.getJungleLowerLeft().y * heightScale,
                map.jungleWidth * widthScale,
                map.jungleHeight * heightScale);

        for (Grass grass : map.getGrass()) {
            g.setColor(new Color(124, 252, 0));
            int y = map.toNoBoundedPosition(grass.getPosition()).y * heightScale;
            int x = map.toNoBoundedPosition(grass.getPosition()).x * widthScale;
            g.fillRect(x, y, widthScale, heightScale);
        }

        for (Animal animal : map.getAnimals()) {
            if (animal.energy == 0) g.setColor(new Color(0, 0, 0));
            else if (animal.energy < 0.25 * animal.startEnergy) g.setColor(new Color(128, 47, 0));
            else if (animal.energy < 0.5 * animal.startEnergy) g.setColor(new Color(191, 0, 1));
            else if (animal.energy < 0.75 * animal.startEnergy) g.setColor(new Color(182, 0, 84));
            else if (animal.energy < 1.0 * animal.startEnergy) g.setColor(new Color(163, 22, 114));
            else if (animal.energy < 2.5 * animal.startEnergy) g.setColor(new Color(163, 65, 163));
            else if (animal.energy < 5.0 * animal.startEnergy) g.setColor(new Color(148, 86, 161));
            else if (animal.energy < 7.5 * animal.startEnergy) g.setColor(new Color(155, 125, 178));
            else if (animal.energy < 10 * animal.startEnergy) g.setColor(new Color(202, 181, 229));
            else if (animal.energy < 50 * animal.startEnergy) g.setColor(new Color(255, 254, 253));
                //superStrongAnimal
            else g.setColor(new Color(255, 242, 105));

            g.fillRoundRect(
                    map.toNoBoundedPosition(animal.getPosition()).x * widthScale,
                    map.toNoBoundedPosition(animal.getPosition()).y * heightScale,
                    widthScale, heightScale, widthScale / 2, heightScale / 2);

        }
    }

}
