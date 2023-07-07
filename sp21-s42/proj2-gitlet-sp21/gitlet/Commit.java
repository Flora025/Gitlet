package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.Map;

/** Represents a gitlet commit object.
 *  A commit consists of a log message, timestamp,
 *  a mapping of file names to blob references, a parent reference,
 *  and (for merges) a second parent reference.
 *
 *  @author flora
 */
public class Commit implements Serializable {

    /** The message of this Commit. */
    private String message;
    /** The timestamp of current Commit. */
    private Date timestamp;

    /** Mapping of `filenames` to corresponding `Blob` objects.
     The Blob obj is represented as a String / a SHA-1 hash. */
    private Map<String, String> nameToBlob; //
    /** Parent of the current Commit: a sha-1 hash. */
    private String parent;

    public Commit(String msg, String parent, Date... timestamp) {
        // metadata
        this.message = msg;
        this.timestamp = timestamp[0];
        // references
        this.parent = parent;
        this.nameToBlob = new HashMap<>();
    }

    /** Returns the message of the Commit. */
    public String getMessage() {
        return this.message;
    }

    /** Returns the sha-1 hash of the Commit's parent Commit. */
    public String getParent() {
        return this.parent;
    }

    /** Returns all metadata and references used for hashing. */
    public String getData() {
        return this.message + this.timestamp.toString() + this.parent
                + this.nameToBlob.toString();
    }
}
