package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author flora
 */
public class Repository {

    // Directories
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The commit directory, a subdirectory of .gitlet. */
    public static final File COMMIT_DIR = join(GITLET_DIR, "commits");
    /** The blob directory, a subdirectory of .gitlet. */
    public static final File BLOB_DIR = join(GITLET_DIR, "blobs");

    // Pointers.
    // Note: all pointers are stored as hashcode and stored in Files.
    /** The file in .gitlet/ which stores the Commit that HEAD is pointing to. */
    private static File HEAD = join(GITLET_DIR, "HEAD");
    /** The File in .gitlet/ which stores the Commit Master is pointing to. */
    private static File Master = join(GITLET_DIR, "Master");


    /** Initialize .gitlet repository in the current directory. */
    public static void init() {
        // Exit program if gitlet vcs already exists
        if (GITLET_DIR.exists()) {
            Utils.message("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

        // Automatically make the first commit with commit info
        // FIXME: clean debug statements
        System.getProperty("user.dir");
        if (GITLET_DIR.mkdir()) {
            // Create dirs and files
            COMMIT_DIR.mkdir();
            BLOB_DIR.mkdir();
            try {
                HEAD.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                Master.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Create a first Commit
            String commitMsg= "initial commit";
            Date UnixEpoch = new Date(0);
            Commit firstCommit = new Commit(commitMsg, null, UnixEpoch);

            // Serialize the Commit and save it to COMMIT_DIR
            String shaName = sha1(firstCommit.getData());
            File initCommitF = join(COMMIT_DIR, shaName); // the name of the commit is its sha1 hash
            try {
                initCommitF.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writeObject(initCommitF, firstCommit); // serialize and write in

            // Initialize HEAD and master pointers
            writeObject(HEAD, shaName); // designate HEAD -> initCommit
            writeObject(Master, shaName); // designate Master -> initCommit
        } else {
            Utils.message("Fail to create GITLET_DIR!"); // Debug
        };
    }
}
