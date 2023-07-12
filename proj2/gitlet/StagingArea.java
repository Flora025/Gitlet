package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.TreeMap;
import java.util.List;
import java.util.Set;

import static gitlet.Utils.*;

/** Represents a staging area.
 *  Stores modified files for addition and removal.
 *
 *  @author flora
 */
public class StagingArea implements Serializable {

    /* Instance variables */
    public final File AREA_FILE;
    public final String areaName;
    /** Mapping of plain file name to blob id in the staging area.
     *  CANNOT be accessed from without */
    private TreeMap<String, String> nameToBlob; // plainName to Blob hash id



    /** Constructor. Initializes a staging area
     * @param areaName the name of the staging area
     */
    public StagingArea(String areaName, File AREA_FILE) {
        this.areaName = areaName;
        this.nameToBlob = new TreeMap<>();
        this.AREA_FILE = AREA_FILE;
    }

    public void saveStage(File AREA_FILE) {
        if (!AREA_FILE.exists()) {
            try {
                AREA_FILE.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        writeObject(AREA_FILE, this); // Add and Rm must be initialized in advance
    }

    /** Gets the Blob corresponding to the id/hash of the given key in the area map */
    public Blob get(String plainName) {
        return Blob.getBlobFromId(nameToBlob.get(plainName));
    }

    /**
     * Adds a new (key, value) mapping into the staging area.
     */
    public void put(String plainName, Blob blob) {
        String blobId = blob.getId();
        this.nameToBlob.put(plainName, blobId);
        writeObject(AREA_FILE, this); // overwrite the original file as an update
    }

    /** Given a key, removes a map item from the staging area.
     *  returns a blob item mapped to the plainName. */
    public Blob remove(String plainName) {
        String blobId = this.nameToBlob.remove(plainName);
        writeObject(AREA_FILE, this); // overwrite the original file as an update
        return Blob.getBlobFromId(blobId);
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
        this.nameToBlob = new TreeMap<String, String>();
        writeObject(this.AREA_FILE, this);
    }



    /* Data */


    /** Gets the filepath of the specific staging area */
    public File getFilepath() {
        return this.AREA_FILE;
    }

    /** Gets the size of the specific staging area map */
    public int size() {
        return this.nameToBlob.size();
    }

    /** Returns a copy of the plain name to id map */
    public TreeMap<String, String> getAreaMap() {
        // COPY
        return new TreeMap<>(nameToBlob);
    }

    /** Returns a set of all file names currently in the staging area
     *  (equivalent to map.keySet() */
    public Set<String> getFiles() {
        return nameToBlob.keySet();
    }

}
