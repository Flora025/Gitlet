package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  Emits all gitlet commands and manages HEAD and Master pointers.
 *
 *  @author flora
 */
public class Repository {

    /* Directories */
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /* Files */
    // Note: all pointers are represented as hashcode and stored in Files.
    /** The file in .gitlet/ which stores the Commit that HEAD is pointing to. */
    private static File HEAD = join(GITLET_DIR, "HEAD");
    /** The File in .gitlet/ which stores the Commit Master is pointing to. */
    private static File Master = join(GITLET_DIR, "Master");
    /** Staging area for addition. Data are saved as a Map object*/
    public static File Add = join(GITLET_DIR, "add");
    /** Staging area for removal */
    public static File Rm = join(GITLET_DIR, "rm");


    /* Commands */

    /** Initialize .gitlet repository in the current directory. */
    public static void init() throws IOException {
        // Exit program if gitlet vcs already exists
        if (GITLET_DIR.exists()) {
            Utils.message("A Gitlet version-control system already exists in the current directory.");
            return;
        }

        System.getProperty("user.dir");
        // 1. Create dirs and files
        // FIXME[refactor]
        GITLET_DIR.mkdir();
        HEAD.createNewFile();
        Master.createNewFile();
        Add.createNewFile();
        writeObject(Add, new HashMap<>()); // Add and Rm must be initialized in advance
        Rm.createNewFile();
        writeObject(Rm, new HashMap<>());

        // 2. Create and save the first Commit
        Commit firstCommit = new Commit("initial commit", null, new Date(0));
        firstCommit.saveCommit();

        // 3. Initialize HEAD and master pointers
        updateHeadTo(firstCommit); // designate HEAD -> initCommit
        updateMasterTo(firstCommit); // designate Master -> initCommit
    }

    /** Given the plain name of a file,
     *  if the file has been changed, add a mapping to the staging area.
     *  @implNote The mapping is represented as {"plainName": "reference to the file's Blob hash"}.
     *
     *  @param plainName Plain name of the file. E.g. Hello.txt */
    public static void add(String plainName) {
        // 根据plainName在cwd找到file =>
        File curFile = join(CWD, plainName);
        if (!curFile.exists()) {
            message("File does not exist.");
            System.exit(0);
        }
        // Read in the file as a Blob and get its sha-1 hash
        Blob curBlob = new Blob(curFile, plainName);
        curBlob.saveBlob();
        String shaName = curBlob.getHash();

        // If this shaName does not match the shaName of the same file in the HEAD (i.e. cur) commit
        //    || there is no such file in the HEAD commit,
        // -> the file is changed || newly added, update the mapping
        Commit curHead = Repository.getHead();
        Map<String, String> nameToBlob = curHead.getMap();
        if (!shaName.equals(nameToBlob.getOrDefault(plainName, ""))) {
            // Add the new mapping to staging area for addition (Add)
            // FIXME[bug]
            HashMap<String, String> stagedAddition = readObject(Add, HashMap.class);
            if (null == stagedAddition) {
                // if the map has not yet been initialized, create a new map
                stagedAddition = new HashMap<>();
            };
            stagedAddition.put(plainName, shaName);
            writeObject(Add, stagedAddition); // overwrite
        }
        // Else if the two matches -> No changes in the file, and thus nothing happens
    }


    /* HEAD and Master management */

    /** Gets the Commit that current HEAD is pointing to. */
    public static Commit getHead() {
        String shaName = readContentsAsString(HEAD);
        return Commit.getCommitFromSha(shaName);
    }

    /** Gets the Commit that current HEAD is pointing to. */
    public static Commit getMaster() {
        String shaName = readContentsAsString(Master);
        return Commit.getCommitFromSha(shaName);
    }

    // FIXME[design]: receives Commit or Hash?
    /** Updates HEAD to point to a specific Commit */
    private static void updateHeadTo(Commit commit) {
        // update by internally overwriting the hash (i.e. filename) of the Commit
        writeContents(HEAD, commit.getHash());
    }

    // FIXME[design]: receives Commit or Hash?
    /** Updates Master to point to a specific Commit */
    private static void updateMasterTo(Commit commit) {
        // update by internally overwriting the hash (i.e. filename) of the Commit
        writeContents(Master, commit.getHash());
    }

}
