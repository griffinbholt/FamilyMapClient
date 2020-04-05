package server.service.helper;

import java.util.List;
import java.util.Random;

abstract class GenerateDataGadget {
    private final Random randomNumGenerator = new Random();

    Object getRandomElementFromList(int numElements, List<?> elements) {
        int randomIndex = getRandomNumber(numElements);
        return elements.get(randomIndex);
    }

    int getRandomNumber(int upperBound) {
        return randomNumGenerator.nextInt(upperBound);
    }
}
