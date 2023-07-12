package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

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
    private TreeMap<String, String> nameToBlob; //
    /** Parent of the current Commit: a sha-1 hash. */
    private final List<String> parents;
    private final String id;

    /**
     * Constructor.
     * @param msg Commit message.
     * @param parents Parent of the Commit instance.
     * @param timestamp Timestamp of the Commit instance.
     */
    public Commit(String msg, List<String> parents, TreeMap<String, String> nameToBlob, Date... timestamp) {
        // metadata
        this.message = msg;
        this.timestamp = timestamp[0];
        // references
        this.parents = parents;
        this.nameToBlob = nameToBlob;
        this.id = sha1(this.message + this.timestamp.toString() + this.parents
                + this.nameToBlob.toString());
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
     *            Allow abbreviated id.
     */
    public static Commit getCommitFromId(String id) {
        if (id == null || id.equals("")) {
            return null;
        }
        // Search id in file (might be abbreviated id)
        int n = id.length();
        String foundId = id;
        for (String name : Objects.requireNonNull(plainFilenamesIn(COMMIT_FOLDER))) {
            if (name.substring(0, n).equals(id)) {
                foundId = name;
            }
        }
        // Get the absolute file path from its sha-1 hash
        File filePath = join(COMMIT_FOLDER, foundId);
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
                this.put(plainName, area.get(plainName)); // this == curCommit, update/add new entry
            }
        } else {
            // stageRemoval
            for (String plainName : area.nameSet()) {
                this.remove(plainName); // rm
            }
        }
    }

    /* Commit Map operations */
    /** Insert a key-value set into the nameToBlob map of the commit */
    private void put(String plainName, Blob blob) {
        String id = blob.getId();
        nameToBlob.put(plainName, id); // what's ACTUALLY put into the map is the ID
        File filepath = join(COMMIT_FOLDER, this.id);
        writeObject(filepath, this);
    }
    /** remove a key-val set from the map
     * @return Blob of the id corresponding to the given key
     */
    public Blob remove(String plainName) {
        String blobId = nameToBlob.remove(plainName);
        File filepath = join(COMMIT_FOLDER, this.id);
        writeObject(filepath, this); // overwrite the original file as an update
        return Blob.getBlobFromId(blobId);
    }

    /** Adds a parent to the current parent list.
     *  Used for merging commits. */
    public void addParent(Commit parent) {
        this.parents.add(parent.getId());
        this.saveCommit();// update commit file
    }

    /** Given a plainName, return the corresponding blob id in commit map
     *  NOTE: this is a map-like operation */
    public Blob get(String plainName) {
        // If the blob does not exist, return an empty string.
        String id = nameToBlob.getOrDefault(plainName, null);
        return Blob.getBlobFromId(id);
    }

    /** Given a filename, returns if a key with the PLAINNAME exists in the commit map. */
    public boolean containsFile(String plainName) {
        return nameToBlob.containsKey(plainName);
    }

    /** Returns a set of filenames in the current commit */
    public Set<String> nameSet() {
        return nameToBlob.keySet();
    }

    /** Returns true if two Commits object refer to the same commit */
    public boolean compareTo(Commit anotherCommit) {
        return anotherCommit.getId().contentEquals(this.id);
    }

    /* Data getters */

    /** Returns the message of the Commit. */
    public String getMessage() {
        return this.message;
    }

    /** Returns the sha-1 hash of the Commit's parent Commit. */
    public List<Commit> getParent() {
        if (parents == null) {
            return null;
        }
        List<Commit> res = new ArrayList<>();
        for (String pId : parents) {
            res.add(getCommitFromId(pId));
        }
        return res;
    }

    /** Returns the sha-1 hash of the Commit object. */
    public String getId() {
        return this.id;
    }
    /** Returns the String data of the Commit object. */
    public String getData() {
        return this.message + "\n" + this.timestamp.toString() + "\n" + this.parents.toString()
                + "\n" + this.nameToBlob.toString();
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    /** Returns a COPY of the plainName to blob hashmap of current commit.
     * Example of mapping: {"hello.txt": "someSHA-1Hash"} */
    public TreeMap<String, String> getMap() {
        // !: COPY
        return new TreeMap<>(this.nameToBlob);
    }

}
