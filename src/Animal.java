import java.util.ArrayList;
import java.util.Objects;

public class Animal implements IMapElement {
    private MapDirection direction;
    private IWorldMap map;
    int energy;
    int startEnergy;
    private ArrayList<IPositionChangeObserver> observerList = new ArrayList<>();
    private Genotype genotype;
    private Vector2d position;

    private Animal() {
        this.direction = MapDirection.getRandomDirection();
        genotype = new Genotype(8, 32);
        position = new Vector2d(2, 2);
    }

    private Animal(IWorldMap map) {
        this();
        this.map = map;
    }

    private Animal(IWorldMap map, Vector2d initialPosition) {
        this(map);
        this.position = initialPosition;
    }

    Animal(IWorldMap map, Vector2d initialPosition, int energy) {
        this(map, initialPosition);
        this.energy = energy;
        this.startEnergy = energy;
    }

    boolean isDead() {
        return this.energy <= 0;
    }

    void changeEnergy(int value) {
        this.energy = this.energy + value;
        if (this.energy < 0) {
            this.energy = 0;
        }
    }

    void move(MoveDirection d) {
        switch (d) {
            case LEFT:
                this.direction = this.direction.previous();
                return;
            case RIGHT:
                this.direction = this.direction.next();
                return;
            case FORWARD:
                if (map.canMoveTo(position.add(Objects.requireNonNull(direction.toUnitVector())))) {
                    Vector2d old = new Vector2d(this.getPosition().x, this.getPosition().y);
                    this.position = position.add(Objects.requireNonNull(direction.toUnitVector()));
                    this.positionChanged(old, this.position, this);
                }
                return;
            case BACKWARD:
                if (map.canMoveTo(position.subtract(Objects.requireNonNull(direction.toUnitVector())))) {
                    Vector2d old = new Vector2d(this.getPosition().x, this.getPosition().y);
                    position = position.subtract(Objects.requireNonNull(direction.toUnitVector()));
                    this.positionChanged(old, this.position, this);
                }
        }
    }

    void rotate() {
        int numOfRotation = genotype.returnRandomGen();
        for (int i = 0; i < numOfRotation; i++) {
            this.move(MoveDirection.RIGHT);
        }
    }

    Animal breed(Animal mother) {
        int childEnergy = (int) (0.25 * mother.energy) + (int) (this.energy * 0.25);
        mother.changeEnergy((int) -(0.25 * mother.energy));
        this.changeEnergy((int) -(this.energy * 0.25));

        Animal child = new Animal(map, mother.getPosition(), childEnergy);
        child.genotype = new Genotype(this.genotype, mother.genotype);
        return child;
    }

    @Override
    public Vector2d getPosition() {
        return this.position;
    }

    @Override
    public void addObserver(IPositionChangeObserver observer) {
        observerList.add(observer);
    }

    void removeObserver(IPositionChangeObserver observer) {
        observerList.remove(observer);
    }

    private void positionChanged(Vector2d old, Vector2d n, Object a) {
        for (IPositionChangeObserver o : observerList) {
            o.positionChanged(old, n, a);
        }
    }

    @Override
    public String toString() {
        if(energy<=0){
            return "X";
        }
        else{
            return this.direction.toString();
        }
    }
}