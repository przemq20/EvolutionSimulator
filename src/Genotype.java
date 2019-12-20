import java.util.Arrays;
import java.util.Random;

public class Genotype {

    private int[] genotype;
    private int size;
    private int numOfGenes;

    Genotype(int numOfGenes, int size) {
        genotype = new int[size];
        this.size = size;
        this.numOfGenes = numOfGenes;
        fillRandom();
    }

    Genotype(Genotype fatherGenotype, Genotype motherGenotype) {
        this(fatherGenotype.getNumOfGenes(), fatherGenotype.getSize());
        int position1 = new Random().nextInt(size - 2) + 1;
        int position2;

        do {
            position2 = new Random().nextInt(size - 2) + 1;
        } while (position1 == position2);

        if (position1 > position2) {
            int replace = position1;
            position1 = position2;
            position2 = replace;
        }

        for (int i = 0; i < position1; i++) {
            genotype[i] = motherGenotype.getGenotype()[i];
        }
        for (int i = position1; i < position2; i++) {
            genotype[i] = fatherGenotype.getGenotype()[i];
        }
        for (int i = position2; i < size; i++) {
            genotype[i] = motherGenotype.getGenotype()[i];
        }

        checkGenotype(genotype);
    }

    private void fillRandom() {
        for (int i = 0; i < 8; i++) {
            genotype[i] = i;
        }
        for (int i = 8; i < size; i++) {
            int random = new Random().nextInt(numOfGenes);
            genotype[i] = random;
        }
        Arrays.sort(genotype);
    }

    private int getNumOfGenes() {
        return this.numOfGenes;
    }

    private int getSize() {
        return this.size;
    }

    private int[] getGenotype() {
        return genotype;
    }


    int returnRandomGen() {
        int random = new Random().nextInt(size);
        return genotype[random];
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < size; i++) {
            result = result + " " + genotype[i];
        }
        return result;
    }

    private void checkGenotype(int[] genotype) {
        for (int i = 0; i < 8; i++) {
            boolean exists = false;
            for (int j = 0; j < size; j++) {
                if (genotype[j] == i) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                updateGene(genotype, i);
            }
        }
    }

    private void updateGene(int[] genotype, int gene) {
        int where = new Random().nextInt(size);
        while (geneCount(genotype, where) <= 1) {
            where = new Random().nextInt(size);
        }
        genotype[where] = gene;
        Arrays.sort(genotype);
    }

    private int geneCount(int[] genotype, int gene) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (genotype[i] == gene) {
                count++;
            }
        }
        return count;
    }
}