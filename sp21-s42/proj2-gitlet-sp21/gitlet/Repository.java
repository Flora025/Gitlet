package gitlet;import java.io.File;import java.io.IOException;import java.text.SimpleDateFormat;import java.util.*;import static gitlet.Utils.*;/** Represents a gitlet repository. *  File structure is as follows (flattened and simplified compared with .git): *  .gitlet *      |--commits/ *      |--blobs/ *      |--branchHeads/ *      |    |--Master        # Master branch *      |    |--anotherBranch # some other branches *      |--HEAD *      |--add                # staged addition *      |--rm                 # staged removal *  Abstraction principle: Involve only communications between Objects and avoid lower map/hash/pointer operations *  @author flora */public class Repository {    /* Directories */    /** The current working directory. */    public static final File CWD = new File(System.getProperty("user.dir"));    /** The .gitlet directory. */    public static final File GITLET_DIR = join(CWD, ".gitlet");    /** The directory in .gitlet/ which stores the heads of branches (incl. Master and others). */    public static final File BRANCH_DIR = join(GITLET_DIR, "branchHeads");    /* Files */    // Note: all pointers are represented as hashcode and stored in Files.    /** The file in .gitlet/ which stores the Commit that HEAD is pointing to. */    public static File HEAD = join(GITLET_DIR, "HEAD");    /** The File in .gitlet/ which stores the Commit Master is pointing to. */    public static File Master = join(BRANCH_DIR, "Master");    /** Staging area for addition. Data are saved as a Map object*/    public static StagingArea Add;    /** Staging area for removal */    public static StagingArea Rm;    /* Commands */    private static void initDirs() throws IOException {        GITLET_DIR.mkdir();        BRANCH_DIR.mkdir();        HEAD.createNewFile();        Master.createNewFile();        Add = new StagingArea("Add");        Rm = new StagingArea("Rm");    }    /** Initialize .gitlet repository in the current directory. */    public static void init() throws IOException {        // Exit program if gitlet vcs already exists        if (GITLET_DIR.exists()) {            Utils.message("A Gitlet version-control system already exists in the current directory.");            return;        }        System.getProperty("user.dir");        // 1. Create dirs and files        initDirs();        // 2. Create and save the first Commit        Commit firstCommit = new Commit("initial commit", null, new HashMap<>(), new Date(0));        firstCommit.saveCommit();        // 3. Initialize HEAD and master pointers        updatePointerTo(HEAD, firstCommit); // designate HEAD -> initCommit        updatePointerTo(Master, firstCommit); // designate Master -> initCommit    }    /** Given the plain name of a file,     *  if the file has been changed, add a mapping to the staging area.     *  @implNote The mapping is represented as {"plainName": "reference to the file's Blob hash"}.     *     *  @param plainName Plain name of the file. E.g. Hello.txt */    public static void add(String plainName) {        File curFile = join(CWD, plainName);        if (!curFile.exists()) {            message("File does not exist.");            System.exit(0);        }        // Read in the file as a Blob        Blob curBlob = new Blob(curFile, plainName);        curBlob.saveBlob();        // Compare the [current Blob] and the [Blob of the HEAD Commit] of the same plainName        Commit curHead = getPointer(HEAD);        String headBlobId = curHead.get(plainName); // the file's blob id in HEAD commit        String curBlobId = curBlob.getId(); // the file's cur blob id        // If this id does not match the id of the same file in the HEAD (i.e. cur) commit        //    || there is no such file in the HEAD commit,        // -> the file is changed || newly added, update the mapping        if (!curBlobId.equals(headBlobId)) {            // Add the new mapping to staging area for addition (Add)            Add.put(plainName, curBlobId);        }        // Else if the two matches -> No changes in the file, and thus nothing happens    }    /**     * Saves a snapshot of tracked files in the current commit and staging area.     * Save and start tracking any files that were staged for addition but were not tracked by its parent.     * @param message Commit message.     */    public static void commit(String message) {        // Failure cases: if no file has been staged        if (Add.size() == 0) {            message("No changes added to the commit.");            System.exit(0);        }        // Clone the parent Commit and update meta data        Commit parentCommit = getPointer(HEAD);        Commit curCommit = new Commit(message, parentCommit.getId(), parentCommit.getMap(), new Date()); // receives a copy of parent's map        /** @implNote:         * Rm records files once `staged` and just deleted from the WD.         * The files are no longer in the WD, but are not yet updated in the Commit mappings.*/        // Update current Commit according to staged addition and removal        curCommit.updateCommitMapTo(Add);        curCommit.updateCommitMapTo(Rm);        curCommit.saveCommit();        // Update HEAD and MASTER pointers        updatePointerTo(HEAD, curCommit);        updatePointerTo(Master, curCommit);        // Clean the staging area (Add && Rm)        Add.clean();        Rm.clean();    }    /**     * Unstage the file if it is currently staged for addition.     * If the file is tracked in the current commit,     *   stage it for removal and remove the file from the working directory     * @param plainName The plain name of the file to be removed.     */    public static void rm(String plainName) {        Commit head = getPointer(HEAD);        boolean isStaged = Add.containsFile(plainName); // staged in Add or not        boolean isTracked = head.containsFile(plainName); // tracked or not        // Failure cases: if the file is neither `staged` nor `tracked`        if (!isStaged && (!isTracked)) {            message("No reason to remove the file.");            System.exit(0);        }        // If the file exists in stagedAddition,        // remove it from add and add to stagedRemoval        if (isStaged) {            // remove from Add            String id = Add.remove(plainName);            // put into Rm            Rm.put(plainName, id);        }        // If the file is tracked in curCommit[HEAD],        // remove it from the working directory        if (isTracked) {            File delFile = join(CWD, plainName); // abs path of the file to be deleted            restrictedDelete(delFile);        }    }    /**     *  Display information about each commit backwards     *    along the commit tree until the initial commit.     *  This command will follow the first parent commit links     *    and ignore any second parents found in merge commit.     *  c.f. `git log --first-parent`     */    public static void log() {        // util        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", new Locale("en", "US"));        Commit curCommit = getPointer(HEAD); // starting from HEAD, walking backwards        while (curCommit != null) {            // For each Commit, print out log info            printCommitInfo(curCommit, sdf);            curCommit = curCommit.getParent();        }    }    /** Displays information about all commits ever made. */    public static void globalLog() {        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", new Locale("en", "US"));        List<String> fileIds = plainFilenamesIn(Commit.COMMIT_FOLDER);        assert fileIds != null;        for (String id : fileIds) {            // For each Commit, print out its log info            Commit curCommit = Commit.getCommitFromId(id);            printCommitInfo(curCommit, sdf);        }    }    /**     *  Prints out the ids of all commits that have the given commit message.     * @param message Commit message.     */    public static void find(String message) {        // Get the full list of commit ids        List<String> fileIds = plainFilenamesIn(Commit.COMMIT_FOLDER);        assert fileIds != null;        // For each Commit, if its message == given message        // Print out its id        boolean found = false;        for (String id : fileIds) {            Commit curCommit = Commit.getCommitFromId(id);            if (message.equals(curCommit.getMessage())) {                found = true;                message(id);            }        }        if (!found) {            message("Found no commit with that message.");        }    }    /** Displays what branches currently exist, and marks the current branch with a *. */    public static void status() {        // Print all branches, with current branch marked with a *        message("=== Branches ===");        List<String> branches = plainFilenamesIn(BRANCH_DIR);        assert branches != null;        Commit head = getPointer(HEAD);        for (String branchName : branches) {            // For each branch, print out its name,            // and marks the current branch with a *.            File curBranch = join(BRANCH_DIR, branchName);            Commit curBranchHead = getPointer(curBranch);            message(curBranchHead.equals(head) ? "*" + branchName : branchName);        }        message("");        // Print all files staged for addition        message("=== Staged Files ===");        Set<String> addFiles = Add.getFiles();        for (String f : addFiles) {            message(f);        }        message("");        // Print all files staged for removal (i.e. removed files)        message("=== Removed Files ===");        Set<String> rmFiles = Rm.getFiles();        for (String f : rmFiles) {            message(f);        }        message("");        message("=== Modifications Not Staged For Commit ===");        message("");        message("=== Untracked Files ===");        message("");    }    /* HEAD and Master management */    /** get String info of HEAD commit, tmp used for testing */    private static String getHeadInfo() {        Commit head = getPointer(HEAD);        return head.getData();    }    /** Gets the Commit that a given pointer P is pointing to.     *  Usage: getPointer(HEAD), getPointer(Master) */    private static Commit getPointer(File p) {        String id = readContentsAsString(p);        return Commit.getCommitFromId(id);    }    /** Updates a pointer P to point to a specific Commit     *  Usage: updatePointerTo(HEAD, commit) HEAD -> commit     *         updatePointerTo(Master, commit) Master -> commit*/    private static void updatePointerTo(File p, Commit commit) {        // update by internally overwriting the hash (i.e. filename) of the Commit        writeContents(p, commit.getId());    }    /* Helper Methods */    /** Prints the log information with a given format. */    private static void printCommitInfo(Commit commit, SimpleDateFormat format) {        message("===");        message("commit %s", commit.getId());        // TODO: placeholder for <merge>        // Merge: [first seven digits of the CW branch] [first seven digits of merged-in branch]        // message("Merge: " + format.format(commit.getTimestamp()));        message("Date: " + format.format(commit.getTimestamp()));        message(commit.getMessage() + "\n");    }}