package capers;

import java.io.File;
import java.io.IOException;

import static capers.Utils.*;

/** A repository for Capers 
 * @author f
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 * TODO: change the above structure if you do something different.
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = Utils.join(CWD, ".capers");
    // TODO Hint: look at the `join` function in Utils f

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all of the persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence() {
        // TODO f
        // init capers
        System.getProperty("user.dir");
        if (!CAPERS_FOLDER.exists() && !CAPERS_FOLDER.mkdir()) {
            exitWithError("Failed to create caper_folder");
        }
        // init story.txt
        File story = Utils.join(CAPERS_FOLDER, "story");
        try {
            if (!story.exists() && !story.createNewFile()) {
                exitWithError("Failed to create story");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        // TODO f
        File f = Utils.join(CAPERS_FOLDER, "story");
        String last = Utils.readContentsAsString(f);
        Utils.writeContents(f, last, last.isEmpty() ? "" : "\n", text);

        System.out.println(Utils.readContentsAsString(f));
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) {
        // TODO f
        // create a dog
        Dog newDog = new Dog(name, breed, age);
        // save dog info
        newDog.saveDog();
        // print info
        System.out.println(newDog.toString());
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {
        // TODO
        // get dog obj with name
        Dog curDog = Dog.fromFile(name);
        // celebrate bd
        curDog.haveBirthday();
        // save dog info
        curDog.saveDog();
    }
}
