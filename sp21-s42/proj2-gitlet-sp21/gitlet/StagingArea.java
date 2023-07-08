package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import static gitlet.Utils.*;

/** Represents a staging area.
 *  Stores modified files for addition and removal.
 *
 *  @author flora
 */
public class StagingArea {

    /* Instance variables */
    public final File AREA_FILE;
    private final String areaName;
    /** Mapping of plain file name to blob id in the staging area.
     *  CANNOT be accessed from without */
    private HashMap<String, String> nameToBlob; // plainName to Blob hash id



    /** Constructor. Initializes a staging area
     * @param areaName the name of the staging area
     */
    public StagingArea(String areaName) {
        this.areaName = areaName;
        this.nameToBlob = new HashMap<>();
        this.AREA_FILE = join(Repository.GITLET_DIR, areaName);
        try {
            AREA_FILE.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(AREA_FILE, nameToBlob); // Add and Rm must be initialized in advance
    }


    /**
     * Adds a new (key, value) mapping into the staging area.
     */
    public void put(String plainName, String id) {
        this.nameToBlob.put(plainName, id);
        writeObject(AREA_FILE, nameToBlob); // overwrite the original file as an update
    }

    /** Given a key, removes a map item from the staging area. */
    public String remove(String plainName) {
        String val = nameToBlob.remove(plainName);
        writeObject(AREA_FILE, nameToBlob); // overwrite the original file as an update
        return val;
    }


    /** Given a filename, returns if a key with the PLAINNAME exists in staging area. */
    public boolean containsFile(String plainName) {
        return nameToBlob.containsKey(plainName);
    }

    public Set<String> nameSet() {
        return nameToBlob.keySet();
    }

    /** Removes all mappings in the staged area for addition. */
    public void clean() {
        nameToBlob = new HashMap<String, String>();
        writeObject(this.AREA_FILE, nameToBlob);
    }



    /* Data */

    /** Gets the id/hash of the given key in the area map */
    public String get(String plainName) {
        return nameToBlob.get(plainName);
    }

    /** Gets the filepath of the specific staging area */
    public File getFilepath() {
        return this.AREA_FILE;
    }

    /** Gets the areaName of the specific staging area */
    public String getAreaName() {
        return this.areaName;
    }

    /** Gets the size of the specific staging area map */
    public int size() {
        return this.nameToBlob.size();
    }

    /** Returns a copy of the plain name to id map */
    public HashMap<String, String> getAreaMap() {
        // COPY
        return new HashMap<>(nameToBlob);
    }
}