package server.service.helper;

import shared.model.Gender;
import shared.model.ServerPerson;
import shared.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A gadget that manages the logic for generating family members for a user
 * @author griffinbholt
 */
final class GenerateAncestorsGadget extends GenerateDataGadget {
    private final List<String> surnames;
    private final List<String> femaleNames;
    private final List<String> maleNames;

    private final int numSurnames;
    private final int numFemaleNames;
    private final int numMaleNames;

    private Map<String, ServerPerson> generatedAncestors;

    GenerateAncestorsGadget(List<String> surnames, List<String> femaleNames, List<String> maleNames) {
        this.surnames = surnames;
        this.numSurnames = surnames.size();

        this.femaleNames = femaleNames;
        this.numFemaleNames = femaleNames.size();

        this.maleNames = maleNames;
        this.numMaleNames = maleNames.size();
    }

    Collection<ServerPerson> generateAncestors(User user, int numGenerations) {
        assert null != user;

        ServerPerson userPerson = user.getPersonObj();
        assert null != userPerson;

        String username = userPerson.getAssociatedUsername();
        String lastName = userPerson.getLastName();

        this.generatedAncestors = new HashMap<>();

        generateParents(username, lastName, numGenerations, userPerson);

        return generatedAncestors.values();
    }

    private void generateParents(String username, String lastName, int numGens, ServerPerson person) {
        // Base Case - No more generations to generate
        if (0 == numGens) {
            return;
        }

        // Recursive Case
        ServerPerson mother = generateFemale(username, numGens - 1);
        String motherID = null;
        if (null != mother) {
            motherID = mother.getPersonID();
            person.setMotherID(motherID);
        }

        ServerPerson father = generateMale(username, lastName, numGens - 1);
        String fatherID = null;
        if (null != father) {
            fatherID = father.getPersonID();
            person.setFatherID(fatherID);
        }

        father.setSpouseID(motherID);
        mother.setSpouseID(fatherID);
    }

    private ServerPerson generateFemale(String username, int numGens) {
        String maidenName = getRandomSurname();
        return generatePerson(username, maidenName, Gender.FEMALE, numGens);
    }

    private ServerPerson generateMale(String username, String lastName, int numGens) {
        return generatePerson(username, lastName, Gender.MALE, numGens);
    }

    private ServerPerson generatePerson(String username, String lastName, Gender gender, int numGens) {
        String firstName = getRandomFirstName(gender);
        ServerPerson person = new ServerPerson(username, firstName, lastName, gender);
        generateParents(username, lastName, numGens, person);

        generatedAncestors.put(person.getPersonID(), person);

        return person;
    }

    private String getRandomFirstName(Gender gender) {
        String firstName = null;

        switch (gender) {
            case MALE:
                firstName = getRandomMaleName();
                break;
            case FEMALE:
                firstName = getRandomFemaleName();
                break;

        }

        return firstName;
    }

    private String getRandomFemaleName() {
        return (String) getRandomElementFromList(numFemaleNames, femaleNames);
    }

    private String getRandomMaleName() {
        return (String) getRandomElementFromList(numMaleNames, maleNames);
    }

    private String getRandomSurname() {
        return (String) getRandomElementFromList(numSurnames, surnames);
    }

    // Getter
    Map<String, ServerPerson> getGeneratedAncestors() {
        return generatedAncestors;
    }
}
