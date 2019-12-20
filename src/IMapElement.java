public interface IMapElement {
    Vector2d getPosition();

    void addObserver(IPositionChangeObserver observer);
}