package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  A commit consists of a log message, timestamp,
 *  a mapping of file names to blob references, a parent reference,
 *  and (for merges) a second parent reference.
 *
 *  @author flora
 */
public class Commit implements Serializable {

    /** The commit directory, a subdirectory of .gitlet. */
    public static final File COMMIT_FOLDER = join(Repository.GITLET_DIR, "commits");

    /** The message of this Commit. */
    private final String message;
    /** The timestamp of current Commit. */
    private final Date timestamp;

    /** Mapping of `filenames` to corresponding `Blob` objects.
     *  Example of mapping: {"hello.txt": "someSHA-1Hash"} */
    private HashMap<String, String> nameToBlob; //
    /** Parent of the current Commit: a sha-1 hash. */
    private final String parent; // TODO[design]: TB revised later for multiple parents
    private final String id;

    /**
     * Constructor.
     * @param msg Commit message.
     * @param parent Parent of the Commit instance.
     * @param timestamp Timestamp of the Commit instance.
     */
    public Commit(String msg, String parent, HashMap<String, String> nameToBlob, Date... timestamp) {
        // metadata
        this.message = msg;
        this.timestamp = timestamp[0];
        // references
        this.parent = parent;
        this.nameToBlob = nameToBlob;
        this.id = getId();
    }

    /**
     * Constructor. Construct by cloning another Commit
     * @param parent another Commit instance.
     */
    public Commit(Commit parent) {
        // metadata
        this.message = parent.getMessage();
        this.timestamp = parent.getTimestamp();
        // references
        this.parent = parent.getId();
        this.nameToBlob = nameToBlob;
        this.id = parent.getId();
    }




    /* Commit Functions */

    /** Save the Commit object to COMMIT_FOLDER*/
    public void saveCommit() {
        if (!COMMIT_FOLDER.exists()) {
            COMMIT_FOLDER.mkdir();
        }
        // Create a new File for this Commit
        File commitFile = join(COMMIT_FOLDER, this.getId()); // the name of the commit is its sha1 hash
        try {
            commitFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Serialize the Commit and save it to COMMIT_FOLDER
        writeObject(commitFile, this);
    }

    /** Gets the Commit object corresponding to the given sha-1 filename
     *  @param id filename as sha-1 hash referring to a Commit object
     */
    public static Commit getCommitFromId(String id) {
        if (id == null || id == "") {
            return null;
        }
        // Get the absolute file path from its sha-1 hash
        File filePath = join(COMMIT_FOLDER, id);
        if (!filePath.exists()) {
            return null;
        }
        // Return the Commit obj if it exists
        return readObject(filePath, Commit.class);
    }

    /** Update current Commit according to staged addition or removal.
     * @param area can only be Add || Rm */
    public void updateCommitMapTo(StagingArea area) {
        if (area.areaName.equals("Add") || area.areaName.equals("add")) { // if this is the add
            for (String plainName : area.nameSet()) {
                // For all staged files, update/insert mappings
                nameToBlob.put(plainName, area.get(plainName)); // update/add
            }
        } else {
            // stageRemoval
            for (String plainName : area.nameSet()) {
                nameToBlob.remove(plainName); // rm
            }
        }
    }

    /* Commit Map operations */
    public void put(String plainName, String id) {
        nameToBlob.put(plainName, id);
    }
    /** remove a key-val set from the map
     * @return id corresponding to the given key
     */
    public String remove(String plainName) {
        return nameToBlob.remove(plainName);
    }

    /** Given a plainName, return the corresponding blob id in commit map
     *  NOTE: this is a map-like operation */
    public String get(String plainName) {
        return nameToBlob.getOrDefault(plainName, "");
    }

    /** Given a filename, returns if a key with the PLAINNAME exists in the commit map. */
    public boolean containsFile(String plainName) {
        return nameToBlob.containsKey(plainName);
    }

    /* Data getters */

    /** Returns the message of the Commit. */
    public String getMessage() {
        return this.message;
    }

    /** Returns the sha-1 hash of the Commit's parent Commit. */
    public Commit getParent() {
        return getCommitFromId(this.parent);
    }

    /** Returns the sha-1 hash of the Commit object. */
    public String getId() {
        return sha1(this.message + this.timestamp.toString() + this.parent
                + this.nameToBlob.toString());
    }
    /** Returns the String data of the Commit object. */
    public String getData() {
        return this.message + "\n" + this.timestamp.toString() + "\n" + this.parent
                + "\n" + this.nameToBlob.toString();
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    /** Returns a COPY of the plainName to blob hashmap of current commit.
     * Example of mapping: {"hello.txt": "someSHA-1Hash"} */
    public HashMap<String, String> getMap() {
        // !: COPY
        return new HashMap<>(this.nameToBlob);
    }

}
