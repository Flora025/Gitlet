package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Utils.*;

/** Represents a gitlet Blob object.
 *  A blob is a snapshot of a file's content at the moment of addition.
 *
 *  @author flora
 */
public class Blob implements Serializable {
    /** The blob directory, a subdirectory of .gitlet. */
    public static final File BLOB_FOLDER = join(Repository.GITLET_DIR, "blobs");


    /* Instance variables */

    /** Name of the Blob (SHA-1 hashed) */
    private final String id;
    /** The plain String of the original file's content */
    private final String plainContent;
    private final String plainName;

    /**
     *  Constructor.
     *  Given a filepath, reads in the File and converts it into a Blob.
     *  Does NOT save it here.
     * @param filePath The absolute path of the file.
     * @param plainName Plain name of the file (e.g. hello.txt)
     */
    public Blob(File filePath, String plainName) {
        if (!BLOB_FOLDER.exists()) {
            BLOB_FOLDER.mkdir();
        }
        this.plainContent = readContentsAsString(filePath);
        this.plainName = plainName;
        this.id = sha1(plainName + plainContent); // MUST: hash in its name
    }

    public void saveBlob() {
        if (!BLOB_FOLDER.exists()) {
            BLOB_FOLDER.mkdir();
        }
        // Create a new File for this Blob
        File blobFile = join(BLOB_FOLDER, this.getId()); // the name of the commit is its sha1 hash
        try {
            blobFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Serialize the Blob and save it to BLOB_FOLDER
        writeObject(blobFile, this);
    }

    /** Converts a blob back into a file, and write to the given new file path */
    public void writeContentToFile(File newFilepath) {
        if (!newFilepath.exists()) {
            try {
                newFilepath.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // Reads the blob's plainContent into the new File
        writeContents(newFilepath, this.getPlainContent()); // overwrite
    }

    /** Returns true if two Commits object refer to the same commit */
    public boolean compareTo(Blob anotherBlob) {
        return anotherBlob.getId().contentEquals(this.id);
    }



    /** Gets the Commit object corresponding to the given sha-1 filename
     *  @param id filename as sha-1 hash referring to a Commit object
     */
    public static Blob getBlobFromId(String id) {
        if (id == null || id.equals("")) {
            return null;
        }
        // Get the absolute file path from its sha-1 hash
        File filePath = join(BLOB_FOLDER, id);
        if (!filePath.exists()) {
            return null;
        }
        // Return the Blob obj if it exists
        return readObject(filePath, Blob.class);
    }




    /* Data getters */

    /** Returns the sha-1 hash (id) of the Blob object. */
    public String getId() {
        return id; // same as hash
    }

    public String getPlainName() {
        return plainName; // same as hash
    }

    public String getPlainContent() {
        return plainContent;
    }

}
