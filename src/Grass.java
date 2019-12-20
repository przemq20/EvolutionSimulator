public class Grass implements IMapElement {

    private Vector2d position;

    public Vector2d getPosition() {
        return this.position;
    }

    Grass(Vector2d position) {
        this.position = position;
    }

    @Override
    public void addObserver(IPositionChangeObserver observer) {
    }

    @Override
    public String toString() {
        return "*";
    }
}