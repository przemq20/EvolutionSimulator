import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

public class WorldMap implements IWorldMap, IPositionChangeObserver {

    int width;
    int height;
    private final Vector2d lowerLeft;
    private final Vector2d upperRight;
    private Vector2d lowerLeftJungle;
    private Vector2d upperRightJungle;
    final int jungleWidth;
    final int jungleHeight;
    private int startEnergy;
    private int plantEnergy;
    private int moveEnergy;
    private int energyToBreed;
    private int grassToSpawn;

    private Map<Vector2d, Grass> grass = new HashMap<>();
    private Map<Vector2d, LinkedList<Animal>> animals = new HashMap<>();
    private LinkedList<Animal> animalsList;
    private LinkedList<Grass> grassList;
    LinkedList<Grass> toRemoveAfterEating = new LinkedList<>();

    WorldMap(int width, int height, int jungleWidth, int jungleHeight, int plantEnergy, int moveEnergy, int startEnergy, int grassToSpawn) {
        this.startEnergy = startEnergy;
        this.grassList = new LinkedList<>();
        this.animalsList = new LinkedList<>();
        this.energyToBreed = startEnergy / 2;
        this.plantEnergy = plantEnergy;
        this.moveEnergy = moveEnergy;
        this.grassToSpawn = grassToSpawn;
        this.width = width;
        this.height = height;
        this.lowerLeft = new Vector2d(0, 0);
        this.upperRight = new Vector2d(width - 1, height - 1);
        this.jungleWidth = jungleWidth;
        this.jungleHeight = jungleHeight;
        this.lowerLeftJungle = new Vector2d(0, 0);
        this.upperRightJungle = new Vector2d(width - 1, height - 1);

        for (int i = 0; i < (width - jungleWidth); i++) {
            if (i % 2 == 0) {
                lowerLeftJungle.x++;
            } else {
                upperRightJungle.x--;
            }
        }

        for (int i = 0; i < (height - jungleHeight); i++) {
            if (i % 2 == 0) {
                lowerLeftJungle.y++;
            } else {
                upperRightJungle.y--;
            }
        }
    }

    //do what is necessary
    void newDay() {
        removeDeadAnimals();
        moveRandomAllAnimals();
        eat();
        loseEnergyAfterDay();
        breed();
        spawnGrass(grassToSpawn);
    }

    private void removeDeadAnimals() {
        LinkedList<Animal> animals = getAnimals();
        for (int i = 0; i < animals.size(); i++) {
            Animal animal = animalsList.get(i);
            if (animal.isDead()) {
                removeAnimal(animal, animal.getPosition());
                animal.removeObserver(this);
                animalsList.remove(animal);
            }
        }
    }

    private void moveRandomAllAnimals() {
        LinkedList<Animal> animals = getAnimals();
        for (int i = 0; i < animals.size(); i++) {
            animalsList.get(i).rotate();
            animalsList.get(i).move(MoveDirection.FORWARD);
        }
    }

    private void eat() {
        LinkedList<Grass> toRemoveAfterEat = new LinkedList<>();
        for (Grass food : grass.values()) {
            LinkedList<Animal> animalsOnGrassField = animals.get(food.getPosition());
            if (animalsOnGrassField != null && animalsOnGrassField.size() > 0) {
                for (Animal a : animalsOnGrassField) {
                    a.changeEnergy(plantEnergy / animalsOnGrassField.size());
                    //if we want animals to have a certain maximum amount of energy
//                    if(a.energy>10*startEnergy) {
//                        a.energy=10*startEnergy;
//                    }
                    toRemoveAfterEat.add(food);
                }
            }
        }
        for (Grass food : toRemoveAfterEat) {
            grass.remove(food.getPosition());
            grassList.remove(food);
        }
    }

    private void loseEnergyAfterDay() {
        for (LinkedList<Animal> animalList : animals.values())
            if (animalList != null && animalList.size() > 0)
                for (Animal a : animalList)
                    a.changeEnergy((-1) * moveEnergy);
    }

    private void breed() {
        for (LinkedList<Animal> animalList : animals.values()) {
            if (animalList != null) {
                if (animalList.size() == 2) {
                    if (animalList.get(0).energy >= energyToBreed)
                        if (animalList.get(1).energy >= energyToBreed) {
                            Animal child = animalList.get(1).breed(animalList.get(0));
                            place(child);
                        }
                }
            }
        }
    }

    private void spawnGrass(int grassToSpawn) {
        int grassToSpawnInJungle = grassToSpawn / 2;
        int grassToSpawnBesidesJungle = grassToSpawn / 2;
        //if grassToSpawn is odd then more grass will spawn in jungle
        if (grassToSpawn % 2 == 1) {
            grassToSpawnInJungle = (grassToSpawn / 2) + 1;
        }
        //if grassToSpawn equals 1 then it can spawn everywhere
        if (grassToSpawn == 1) {
            int countOperations = 0;
            while (countOperations < width * height * 10) {
                int randomWidth = new Random().nextInt(width);
                int randomHeight = new Random().nextInt(height);
                Vector2d newGrass = new Vector2d(randomWidth, randomHeight);
                if (grass.get(newGrass) == null && canPlace(newGrass)) {
                    place(new Grass(newGrass));
                    break;
                }
                countOperations++;
            }
        } else {

            int jungleSize = jungleWidth * jungleHeight;
            int countOperations = 0;
            //spawn grass on random position in jungle if whole place in jungle is not filled with grass
            for (int i = 0; i < grassToSpawnInJungle; i++) {
                if (getGrassCountInJungle() < jungleSize) {
                    while (countOperations < jungleSize * 100) {
                        int randomWidth = new Random().nextInt(jungleWidth);
                        int randomHeight = new Random().nextInt(jungleHeight);
                        Vector2d newGrass = new Vector2d(randomWidth + lowerLeftJungle.x, randomHeight + lowerLeftJungle.y);
                        if (grass.get(newGrass) == null && canPlace(newGrass)) {
                            place(new Grass(newGrass));
                            break;
                        }
                        countOperations++;
                    }
                }
                countOperations = 0;
            }
            //spawn grass on random position besides jungle if whole place besides jungle is not filled with grass
            for (int i = 0; i < grassToSpawnBesidesJungle; i++) {
                if (getGrassCountBesidesJungle() < height * width - jungleSize) {
                    while (countOperations < (height * width - jungleSize) * 100) {
                        int randomWidth = new Random().nextInt(width);
                        int randomHeight = new Random().nextInt(height);
                        Vector2d newGrass = new Vector2d(randomWidth + lowerLeft.x, randomHeight + lowerLeft.y);
                        if (grass.get(newGrass) == null && canPlace(newGrass) && !(newGrass.follows(lowerLeftJungle) && newGrass.precedes(upperRightJungle))) {
                            place(new Grass(newGrass));
                            break;
                        }
                        countOperations++;
                    }
                }
                countOperations = 0;
            }
        }
    }

    LinkedList<Animal> getAnimals() {
        return animalsList;
    }

    int getAnimalsCount() {
        return animalsList.size();
    }

    int getGrassCount() {
        return grassList.size();
    }

    private int getGrassCountInJungle() {
        int count = 0;
        for (Grass grass1 : grassList) {
            if ((grass1.getPosition().follows(lowerLeftJungle) && grass1.getPosition().precedes(upperRightJungle))) {
                count++;
            }
        }
        return count;
    }

    private int getGrassCountBesidesJungle() {
        int count = 0;
        for (Grass grass1 : grassList) {
            if ((!grass1.getPosition().follows(lowerLeftJungle) || !grass1.getPosition().precedes(upperRightJungle))) {
                count++;
            }
        }
        return count;
    }

    LinkedList<Grass> getGrass() {
        return grassList;
    }

    Vector2d getJungleLowerLeft() {
        return lowerLeftJungle;
    }

    Vector2d toNoBoundedPosition(Vector2d position) {
        int x;
        int y;

        if (position.x < lowerLeft.x) {
            x = (width - Math.abs(position.x % width)) % width;
        } else {
            x = Math.abs(position.x % width);
        }
        if (position.y < lowerLeft.y) {
            y = (height - Math.abs(position.y % height)) % height;
        } else {
            y = Math.abs(position.y % height);
        }
        return new Vector2d(x, y);
    }

    @Override
    public boolean place(IMapElement iMapElement) {
        Vector2d position = toNoBoundedPosition(iMapElement.getPosition());

        if (canPlace(position)) {
            if (iMapElement instanceof Grass) {
                if (grass.get(position) == null)
                    grass.put(position, (Grass) iMapElement);
                grassList.add((Grass) iMapElement);
            }
            if (iMapElement instanceof Animal) {
                addAnimal((Animal) iMapElement, position);
                animalsList.add((Animal) iMapElement);
                iMapElement.addObserver(this);
            }
        }
        return true;
    }

    private void removeAnimal(Animal a, Vector2d position2) {
        Vector2d position = toNoBoundedPosition(position2);
        LinkedList<Animal> l = animals.get(position);
        if (l == null)
            throw new IllegalArgumentException("Animal" + a.getPosition() + " -> " + position + " already not exist in the map");
        else if (l.size() == 0)
            throw new IllegalArgumentException("Animal" + a.getPosition() + " already not exist in the map empty list");
        else {
            l.remove(a);
            if (l.size() == 0) {
                animals.remove(position);
            }
        }
    }

    private void addAnimal(Animal animal, Vector2d vector2d) {
        if (animal == null) return;
        Vector2d position = toNoBoundedPosition(vector2d);
        LinkedList<Animal> animalList = animals.get(position);
        if (animalList == null) {
            LinkedList<Animal> listAnimal = new LinkedList<>();
            listAnimal.add(animal);
            animals.put(position, listAnimal);
        } else {
            animalList.add(animal);
        }
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        return objectAt(position) != null;
    }

    @Override
    public Object objectAt(Vector2d position2) {
        Vector2d position = toNoBoundedPosition(position2);
        LinkedList<Animal> list = animals.get(position);
        if (list == null) return grass.get(position);
        else if (list.size() == 0) return grass.get(position);
        else return list.getFirst();
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        Vector2d noBoundedPosition = toNoBoundedPosition(position);
        if (animals.get(noBoundedPosition) == null) return true;
        return animals.get(noBoundedPosition).size() < 2;
    }

    private boolean canPlace(Vector2d position) {
        Vector2d noBoundedPosition = toNoBoundedPosition(position);
        if (animals.get(noBoundedPosition) == null) return true;
        return animals.get(noBoundedPosition).size() < 3;
    }

    void addAnimalToMap() {
        int countOperations = 0;
        while (countOperations < width * height * 100) {
            int randomPositionX = new Random().nextInt(width);
            int randomPositionY = new Random().nextInt(height);
            Vector2d position = new Vector2d(randomPositionX + lowerLeftJungle.x, randomPositionY + lowerLeftJungle.y);
            if (canPlace(position)) {
                place(new Animal(this, position, startEnergy));
                return;
            }
            countOperations++;
        }
    }

    public boolean positionChanged(Vector2d oldPosition2, Vector2d newPosition2, Object a) {
        Vector2d oldPosition = toNoBoundedPosition(oldPosition2);
        Vector2d newPosition = toNoBoundedPosition(newPosition2);
        if (canMoveTo(newPosition)) {
            removeAnimal((Animal) a, oldPosition);
            addAnimal((Animal) a, newPosition);
            return true;
        }
        return false;
    }

}
