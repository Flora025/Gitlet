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
    private String message;
    /** The timestamp of current Commit. */
    private Date timestamp;

    /** Mapping of `filenames` to corresponding `Blob` objects.
     *  Example of mapping: {"hello.txt": "someSHA-1Hash"} */
    private HashMap<String, String> nameToBlob; //
    /** Parent of the current Commit: a sha-1 hash. */
    private String parent;

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
    }


    /* Commit Functions */

    /** Save the Commit object to COMMIT_FOLDER*/
    public void saveCommit() {
        if (!COMMIT_FOLDER.exists()) {
            COMMIT_FOLDER.mkdir();
        }
        // Create a new File for this Commit
        File commitFile = join(COMMIT_FOLDER, this.getHash()); // the name of the commit is its sha1 hash
        try {
            commitFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Serialize the Commit and save it to COMMIT_FOLDER
        writeObject(commitFile, this);
    }

    /** Gets the Commit object corresponding to the given sha-1 filename
     *  @param shaName filename as sha-1 hash referring to a Commit object
     */
    public static Commit getCommitFromSha(String shaName) {
        // Get the absolute file path from its sha-1 hash
        File filePath = join(COMMIT_FOLDER, shaName);
        if (!filePath.exists()) {
            return null;
        }
        // Return the Commit obj if it exists
        return readObject(filePath, Commit.class);
    }


    /* Data getters */

    /** Returns the message of the Commit. */
    public String getMessage() {
        return this.message;
    }

    /** Returns the sha-1 hash of the Commit's parent Commit. */
    public String getParent() {
        return this.parent;
    }

    /** Returns the sha-1 hash of the Commit object. */
    public String getHash() {
        return sha1(this.message + this.timestamp.toString() + this.parent
                + this.nameToBlob.toString());
    }

    /** Returns the plainName to blob hashmap of current commit.
     * Example of mapping: {"hello.txt": "someSHA-1Hash"}*/
    public HashMap<String, String> getMap() {
        return this.nameToBlob;
    }

}
