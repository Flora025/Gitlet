package gitlet;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.Utils.plainFilenamesIn;

/** Represents a gitlet repository.
 *  File structure is as follows (flattened and simplified compared with .git):
 *  .gitlet
 *      |--commits/
 *      |--blobs/
 *      |--branchHeads/
 *      |    |--Master        # Master branch
 *      |    |--anotherBranch # some other branches
 *      |--HEAD
 *      |--add                # staged addition
 *      |--rm                 # staged removal
 *  Abstraction principle: Involve only communications between Objects and avoid lower map/hash/pointer operations
 *  @author flora
 */
public class Repository {

    /* Directories */
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The directory in .gitlet/ which stores the heads of branches (incl. Master and others). */
    public static final File BRANCH_DIR = join(GITLET_DIR, "branchHeads");

    /* Files */
    // Note: all pointers are represented as hashcode and stored in Files.
    /** The file in .gitlet/ which stores the Commit that HEAD is pointing to. */
    public static File HEAD = join(GITLET_DIR, "HEAD");
    /** The File in .gitlet/ which stores the Commit Master is pointing to. */
    public static File Master = join(BRANCH_DIR, "master");
    /** Staging area for addition. Data are saved as a Map object*/
    public static final File ADD_FILE = join(GITLET_DIR, "Add");
    /** Staging area for removal */
    public static final File RM_FILE = join(GITLET_DIR, "Rm");


    private static final File curBranchName = join(GITLET_DIR, "curBranch");


    /* Commands */

    private static void initDirs() throws IOException {
        GITLET_DIR.mkdir();
        BRANCH_DIR.mkdir();
        HEAD.createNewFile();
        Master.createNewFile();
        curBranchName.createNewFile();
        // init add and rm
        StagingArea Add = new StagingArea("Add", ADD_FILE);
        Add.saveStage(ADD_FILE);
        StagingArea Rm = new StagingArea("Rm", RM_FILE);
        Rm.saveStage(RM_FILE);

        writeContents(curBranchName, "master");
    }

    /** Initialize .gitlet repository in the current directory. */
    public static void init() throws IOException {
        // Exit program if gitlet vcs already exists
        if (GITLET_DIR.exists()) {
            Utils.message("A Gitlet version-control system already exists in the current directory.");
            return;
        }

        System.getProperty("user.dir");
        // 1. Create dirs and files
        initDirs();

        // 2. Create and save the first Commit
        Commit firstCommit = new Commit("initial commit", null, new TreeMap<>(), new Date(0));
        firstCommit.saveCommit();

        // 3. Initialize HEAD and master pointers
        updatePointerTo(HEAD, firstCommit); // designate HEAD -> initCommit
        updatePointerTo(Master, firstCommit); // designate Master -> initCommit
    }

    /** Given the plain name of a file,
     *  if the file has been changed, add a mapping to the staging area.
     *  @implNote The mapping is represented as {"plainName": "reference to the file's Blob hash"}.
     *
     *  @param plainName Plain name of the file. E.g. Hello.txt */
    public static void add(String plainName) {
        File curFile = join(CWD, plainName);
        if (!curFile.exists()) {
            message("File does not exist.");
            System.exit(0);
        }
        // Read in the file as a Blob
        Blob curBlob = new Blob(curFile, plainName); // the file's cur blob
        curBlob.saveBlob();

        // Compare the [current Blob] and the [Blob of the HEAD Commit] of the same plainName
        Commit curHead = getPointer(HEAD);
        Blob headBlob = curHead.get(plainName); // the file's blob in HEAD commit
        // If this id does not match the id of the same file in the HEAD (i.e. cur) commit
        //    || there is no such file in the HEAD commit,
        // -> the file is changed || newly added, update the mapping
        StagingArea Add = getStage(ADD_FILE);
        StagingArea Rm = getStage(RM_FILE);
        if (headBlob == null || !curBlob.compareTo(headBlob)) {
            // Add the new mapping to staging area for addition (Add)
            Add.put(plainName, curBlob);
        } else {
            // Else if the two matches -> No changes in the file, remove the mark if it's in Rm
            Rm.remove(plainName);
        }
    }

    /**
     * Saves a snapshot of tracked files in the current commit and staging area.
     * Save and start tracking any files that were staged for addition but were not tracked by its parent.
     * @param message Commit message.
     */
    public static Commit commit(String message) {
        if (message.equals("")) {
            message("Please enter a commit message.");
            System.exit(0);
        }
        StagingArea Add = getStage(ADD_FILE);
        StagingArea Rm = getStage(RM_FILE);

        // Failure cases: if no file has been staged
        if (Add.size() == 0 && Rm.size() == 0) {
            message("No changes added to the commit.");
            System.exit(0);
        }

        // Clone the parent Commit and update meta data
        Commit parentCommit = getPointer(HEAD);
        List<String> parents = new ArrayList<>();
        parents.add(parentCommit.getId());
        Commit curCommit = new Commit(message, parents, parentCommit.getMap(), new Date()); // receives a copy of parent's map

        /** @implNote:
         * Rm records files once `staged` and just deleted from the WD.
         * The files are no longer in the WD, but are not yet updated in the Commit mappings.
         */

        // Update current Commit according to staged addition and removal
        curCommit.updateCommitMapTo(Add);
        curCommit.updateCommitMapTo(Rm);

        curCommit.saveCommit();

        // Update HEAD and curBranchHead pointers
        updatePointerTo(HEAD, curCommit);
        updatePointerTo(join(BRANCH_DIR, readContentsAsString(curBranchName)), curCommit);

        // Clean the staging area (Add && Rm)
        Add.clean();
        Rm.clean();

        return curCommit;
    }



    /**
     * Unstage the file if it is currently staged for addition.
     * If the file is tracked in the current commit,
     *   stage it for removal and remove the file from the working directory
     * @param plainName The plain name of the file to be removed.
     */
    public static void rm(String plainName) {
        Commit head = getPointer(HEAD);
        StagingArea Add = getStage(ADD_FILE);
        StagingArea Rm = getStage(RM_FILE);

        boolean isStaged = Add.containsFile(plainName); // staged in Add or not
        boolean isTracked = head.containsFile(plainName); // tracked or not

        // Failure cases: if the file is neither `staged` nor `tracked`
        if (!isStaged && !isTracked) {
            message("No reason to remove the file.");
            System.exit(0);
        }

        if (isStaged) {
            // remove from Add
            Add.remove(plainName);
        }

        // If the file is tracked in curCommit[HEAD],
        // remove it from the working directory
        if (isTracked) {
            Blob blob = head.get(plainName);
            // Blob curBlob = new Blob(join(CWD, plainName), plainName);
            Rm.put(plainName, blob);
            if (join(CWD, plainName).exists()) {
                join(CWD, plainName).delete(); // abs path of the file to be deleted
            }
        }
    }

    /**
     *  Display information about each commit backwards
     *    along the commit tree until the initial commit.
     *  This command will follow the first parent commit links
     *    and ignore any second parents found in merge commit.
     *  c.f. `git log --first-parent`
     */
    public static void log() {
        // util
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", new Locale("en", "US"));

        Commit curCommit = getPointer(HEAD); // starting from HEAD, walking backwards
        while (curCommit != null) {
            // For each Commit, print out log info
            printCommitInfo(curCommit, sdf);
            curCommit = curCommit.getParent() == null ? null : curCommit.getParent().get(0); // first parent
        }

    }

    /** Displays information about all commits ever made. */
    public static void globalLog() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", new Locale("en", "US"));
        List<String> fileIds = plainFilenamesIn(Commit.COMMIT_FOLDER);
        assert fileIds != null;
        for (String id : fileIds) {
            // For each Commit, print out its log info
            Commit curCommit = Commit.getCommitFromId(id);
            printCommitInfo(curCommit, sdf);
        }
    }

    /**
     *  Prints out the ids of all commits that have the given commit message.
     * @param message Commit message.
     */
    public static void find(String message) {
        // Get the full list of commit ids
        List<String> fileIds = plainFilenamesIn(Commit.COMMIT_FOLDER);
        assert fileIds != null;
        // For each Commit, if its message == given message
        // Print out its id
        boolean found = false;
        for (String id : fileIds) {
            Commit curCommit = Commit.getCommitFromId(id);
            if (message.equals(curCommit.getMessage())) {
                found = true;
                message(id);
            }
        }
        if (!found) {
            message("Found no commit with that message.");
        }
    }

    /** Displays what branches currently exist, and marks the current branch with a *. */
    public static void status() {
        // FC
        if (!GITLET_DIR.exists()) {
            message("Not in an initialized Gitlet directory");
            System.exit(0);
        }
        // Print all branches, with current branch marked with a *
        message("=== Branches ===");
        List<String> branches = plainFilenamesIn(BRANCH_DIR);
        if (branches != null) {
            Commit head = getPointer(HEAD);
            for (String branchName : branches) {
                // For each branch, print out its name,
                // and marks the current branch with a *.
                message(branchName.equals(readContentsAsString(curBranchName)) ? "*" + branchName : branchName);
            }
        }
        message("");

        // Print all files staged for addition
        message("=== Staged Files ===");
        StagingArea Add = getStage(ADD_FILE);
        Set<String> addFiles = Add.getFiles();
        if (addFiles != null) {
            for (String f : addFiles) {
                message(f);
            }
        }
        message("");

        // Print all files staged for removal (i.e. removed files)
        message("=== Removed Files ===");
        StagingArea Rm = getStage(RM_FILE);
        Set<String> rmFiles = Rm.getFiles();
        if (rmFiles != null) {
            for (String f : rmFiles) {
                message(f);
            }
        }
        message("");

        message("=== Modifications Not Staged For Commit ===");
        message("");

        message("=== Untracked Files ===");
        message("");
    }

    /**
     * A general command that can fulfil three purposes.
     * Usage:
     * 1. Takes the version of the file as it exists in the head commit and puts it in the working directory
     *    java gitlet.Main checkout -- [file name]
     * 2. Takes the version of the file as it exists in the commit with the given id, and puts it in the working directory
     *    java gitlet.Main checkout [commit id] -- [file name]
     * 3. Takes all files in the commit at the head of the given branch, and puts them in the working directory
     *    java gitlet.Main checkout [branch name] */
    public static void checkoutHeadFile(String plainName, String operand) {
        if (!operand.equals("--")) {
            message("Incorrect operands.");
            System.exit(0);
        }
        // Get head Commit
        Commit head = getPointer(HEAD);
        // Get the file's blob version (content)
        Blob headBlob = head.get(plainName);
        if (headBlob != null) {
            // Put it in the WD (not staged)
            headBlob.writeContentToFile(join(CWD, plainName));
        }
        else {
            // Failure case:file does not exist
            message("File does not exist in that commit.");
            System.exit(0);
        }
    }
    public static void checkoutSpecifiedFile(String commitId, String plainName, String operand) {
        if (!operand.equals("--")) {
            message("Incorrect operands.");
            System.exit(0);
        }
        // 1. Get specified Commit
        Commit commit = Commit.getCommitFromId(commitId);
        if (commit == null) {
            // Failure case: commit does not exist
            message("No commit with that id exists.");
            System.exit(0);
        }

        // 2. Get the file's blob (content)
        Blob commitBlob = commit.get(plainName);
        if (commitBlob != null) {
            // 3. Put it in the WD (not staged)
            commitBlob.writeContentToFile(join(CWD, plainName));
        }
        else {
            // Failure case:file does not exist
            message("File does not exist in that commit.");
            System.exit(0);
        }
    }
    public static void checkoutBranch(String branchName) throws IOException {
        File branchPath = join(BRANCH_DIR, branchName);
        Commit head = getPointer(HEAD);

        // Failure Cases:
        if (!branchPath.exists()) {
            // 1. the branch does not exist
            message("No such branch exists.");
            return;
        } else if (branchName.equals(readContentsAsString(curBranchName))) {
            // 2. the checked out branch is the current branch
            message("No need to checkout the current branch.");
            System.exit(0);
        } else if (hasUntrackedFile(head, getPointer(branchPath))) {
            // 3. if a working file is untracked in the current branch and would be overwritten by the checkout
            message("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }

        // Get branch head Commit's id -> commitId
        Commit branchHead = getPointer(branchPath);
        String commitId = branchHead.getId();

        /* There are 3 kinds of situations: */
        // for ALL files in curHead and checkoutBranchHead
        Set<String> names = new HashSet<>();
        names.addAll(head.nameSet());
        names.addAll(branchHead.nameSet());

        // Iterate over files in CWD
        for (String plainName : names) {
            // 1) the file is tracked in curBranch as well as checked-out branch -> replace the Blob
            if (head.containsFile(plainName) && branchHead.containsFile(plainName)) {
                // Call checkoutSpecifiedFile(commitId, plainName) on the file
                checkoutSpecifiedFile(commitId, plainName, "--");
            }
            // 2) the file is not tracked in the checkedout branch, but only curBranch -> delete the file
            else if (head.containsFile(plainName) && !branchHead.containsFile(plainName)) {
                File f = join(CWD, plainName);
                restrictedDelete(f);
            }
            // 3) the file is tracked in the checkedout branch, but not in curBranch -> write the f to cwd
            else if (!head.containsFile(plainName) && branchHead.containsFile(plainName)) {
                File f = join(CWD, plainName);
                Blob content = branchHead.get(plainName);
                content.writeContentToFile(f);
                f.createNewFile();
            }
        }

        // Set Head to point to the Commit of this branchHead
        updatePointerTo(HEAD, branchHead);
        writeContents(curBranchName, branchName); // update current branch name

        // If branchName != the current branch (i.e. if it's checking out to another branch),
        // Clear the staging area.
        StagingArea Add = getStage(ADD_FILE);
        StagingArea Rm = getStage(RM_FILE);
        Add.clean();
        Rm.clean();
    }


    /** Creates a new branch with the given name, and points it at the current head commit.
     *  Does NOT immediately switch to the newly created branch. */
    public static void branch(String name) throws IOException {
        // Create a new branch
        File newBranch = join(BRANCH_DIR, name);
        // Failure case: branch name already exists
        if (newBranch.exists()) {
            message("A branch with that name already exists.");
        } else {
            newBranch.createNewFile();
        }

        // Point it at the cur HEAD Commit
        Commit headCommit = getPointer(HEAD);
        updatePointerTo(newBranch, headCommit);
    }

    /** Deletes the branch with the given name.
     *  (deletes the pointer and nothing else) */
    public static void rmBranch(String branchName) {
        File branchPath = join(BRANCH_DIR, branchName);
        if (!branchPath.exists()) {
            // Failure case 1: branch with the given name does not exist
            message("A branch with that name does not exist.");
        } else if (branchName.equals(readContentsAsString(curBranchName))) {
            // Failure case 2: trying to remove the current branch
            message("Cannot remove the current branch.");
        }

       branchPath.delete();
    }

    /** Checks out all the files tracked by the given commit.
     *  This command is closest to using the --hard option,
     *  as in git reset --hard [commit hash].*/
    public static void reset(String commitId) throws IOException {
        // Failure case: No commit with that id exists
        File commitFile = join(Commit.COMMIT_FOLDER, commitId);
        if (!commitFile.exists()) {
            message("No commit with that id exists.");
            System.exit(0);
        }

        Commit newHead = Commit.getCommitFromId(commitId);
        Commit head = getPointer(HEAD); // current HEAD
        if (hasUntrackedFile(head, newHead)) {
            // 3. if a working file is untracked in the current branch and would be overwritten by the checkout
            message("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }

        /* Update files in CWD to the given Commit. There are 3 kinds of situations: */

        // Iterate over files in CWD
        for (String plainName : Objects.requireNonNull(plainFilenamesIn(CWD))) {
            // 1) the file is tracked in curBranch as well as checked-out branch -> replace the Blob
            if (head.containsFile(plainName) && newHead.containsFile(plainName)) {
                // Call checkoutSpecifiedFile(commitId, plainName) on the file
                checkoutSpecifiedFile(commitId, plainName, "--");
            }
            // 2) the file is not tracked in the checkedout branch, but only curBranch -> delete the file
            else if (head.containsFile(plainName) && !newHead.containsFile(plainName)) {
                File f = join(CWD, plainName);
                restrictedDelete(f);
            }
            // 3) the file is tracked in the checkedout branch, but not in curBranch -> write the f to cwd
            else if (!head.containsFile(plainName) && newHead.containsFile(plainName)) {
                File f = join(CWD, plainName);
                Blob content = newHead.get(plainName);
                content.writeContentToFile(f);
                f.createNewFile();
            }
        }
        // Update the HEAD pointer and current branch head
        updatePointerTo(HEAD, newHead);
        updatePointerTo(join(BRANCH_DIR, readContentsAsString(curBranchName)), newHead);

        // Clean the staging area
        StagingArea Add = getStage(ADD_FILE);
        StagingArea Rm = getStage(RM_FILE);
        Add.clean();
        Rm.clean();
    }


    /**
     *  Merge a given branch into the current branch.
     */
    public static void merge(String otherBranchName) {
        File branchpath = join(BRANCH_DIR, otherBranchName);

        /* Failure checks */
        // FC1: uncommitted changes
        StagingArea Add = getStage(ADD_FILE);
        StagingArea Rm = getStage(RM_FILE);
        if (Add.size() != 0 || Rm.size() != 0) {
            message("You have uncommitted changes.");
            System.exit(0);
        }
        // FC2: If other branch does not exist, exit with error message
        if (!branchpath.exists()) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
        // FC3: merge with itself
        if (otherBranchName.equals(readContentsAsString(curBranchName))) {
            message("Cannot merge a branch with itself.");
            System.exit(0);
        }
        Commit otherHead = getPointer(branchpath);
        Commit curHead = getPointer(HEAD);
        // FC4: untracked files
        if (hasUntrackedFile(curHead, otherHead)) {
            message("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }

        /* 1. Identify the split point (LCA problem) --> Breadth-First Search */
        // Init variables for BFS
        Map<String, Integer> curMap = bfs(curHead);
        Map<String, Integer> otherMap = bfs(otherHead);

        // Iterate over curMap to find minimum depth
        int minDepth = Integer.MAX_VALUE;
        String splitId = "";
        for (String id : curMap.keySet()) {
            if (otherMap.containsKey(id)) {
                if ((curMap.get(id) <= minDepth)) {
                    splitId = id;
                    minDepth = curMap.get(id);
                }
            }
        }
        Commit split = Commit.getCommitFromId(splitId); // GET!
        checkSplit(split, curHead, otherBranchName);

        /* 2. Update files */
        // Create a map for each Commit's mapping, and a map for all mappings
        Map<String, String> allM = new TreeMap<>();    // all blobs in the 3 Commits
        Map<String, String> splitM = split.getMap();  // blobs in split Commit
        Map<String, String> curM = curHead.getMap();    // blobs in curHead
        Map<String, String> otherM = otherHead.getMap();  // blobs in otherHead
        allM.putAll(splitM);
        allM.putAll(curM);
        allM.putAll(otherM);
        // Iterate through all file plain names
        boolean conflicted = false;
        for (String fn : allM.keySet()) {
            boolean inSplit = splitM.containsKey(fn);
            boolean otherModified = modified(fn, splitM, otherM);
            boolean curModified = modified(fn, splitM, curM);
            // 现在问题：点太快就会出现bug
            // a. in SPLIT && modified in otherHead && not in curHead -> update to otherHead
            if (inSplit && !otherM.containsKey(fn) && !curModified) {
                // g. in SPLIT && unmodified in curHead && absent in otherHead -> remove (rm) file
                rm(fn);
            } else if (inSplit && !curM.containsKey(fn) && !otherModified) {
                // h. in SPLIT && unmodified in otherHead && absent in curHead -> remain removed
                continue;
            } else if (inSplit && !curModified && otherModified) {
                // update file in CWD to otherHead
                Blob newBlob = otherHead.get(fn);
                newBlob.writeContentToFile(join(CWD, fn));
                add(fn);
            } else if (inSplit && curModified && !otherModified) {
                // b. in SPLIT && modified in curHead && not in otherHead -> keep to curHead
                continue;
            } else if (inSplit && curModified && otherModified) {
                // c. in SPLIT && mod in curHead && mod in otherHead (same way) -> remain the same
                // d. in SPLIT && mod in curHead && mod in otherHead (diff ways) -> CONFLICT!
                if (curHead.get(fn) != null
                        && split.get(fn) != null
                        && !curHead.get(fn).compareTo(split.get(fn))) {
                    conflicted = true;

                    Blob curBlob = curHead.get(fn);
                    String curContent = curBlob == null ? "" : curBlob.getPlainContent();
                    Blob otherBlob = otherHead.get(fn);
                    String otherContent = otherBlob == null ? "" : otherBlob.getPlainContent();
                    // write content str into file
                    File tmp = join(CWD, fn);
                    writeContents(tmp, "<<<<<<< HEAD\n" + curContent + "=======\n" + otherContent + ">>>>>>>\n");
                    Blob newBlob = new Blob(tmp, fn); // content modification
                    newBlob.writeContentToFile(join(CWD, fn));
                    add(fn);
                }
            } else if (!inSplit && !otherModified && curModified) {
                // e. not in SPLIT && not otherHead && mod in curHead -> keep to curHead
                continue;
            } else if (!inSplit && otherModified && !curModified) {
                // f. not in SPLIT && not curHead && mod in otherHead -> update to otherHead
                Blob newBlob = otherHead.get(fn);
                newBlob.writeContentToFile(join(CWD, fn));
                add(fn);
            }
        }
        // Make a merge commit
        String msg = String.format("Merged %s into %s.", otherBranchName, readContentsAsString(curBranchName));
        // If there's anything in the staging areas
        Add = getStage(ADD_FILE); // retrieve again
        Rm = getStage(RM_FILE);
        if (Add.size() != 0 || Rm.size() != 0) {
            Commit mergeCommit = commit(msg);
            mergeCommit.addParent(otherHead);
        }
        if (conflicted) {
            System.out.println("Encountered a merge conflict.");
        }
    }




    /* HEAD and Branch management */

    /** Gets the Commit that a given pointer P is pointing to.
     *  Usage: getPointer(HEAD), getPointer(Master) */
    private static Commit getPointer(File p) {
        String id = readContentsAsString(p);
        return Commit.getCommitFromId(id);
    }

    /** Updates a pointer P to point to a specific Commit
     *  Usage: updatePointerTo(HEAD, commit) HEAD -> commit
     *         updatePointerTo(Master, commit) Master -> commit*/
    private static void updatePointerTo(File p, Commit commit) {
        // update by internally overwriting the hash (i.e. filename) of the Commit
        writeContents(p, commit.getId());
    }


    /* Helper Methods */

    /** Prints the log information with a given format. */
    private static void printCommitInfo(Commit commit, SimpleDateFormat format) {
        message("===");
        message("commit %s", commit.getId());
        List<Commit> parents = commit.getParent();
        if (parents != null && parents.size() == 2) {
            System.out.printf(
                    "Merge: %s %s\n",
                    parents.get(0).getId().substring(0, 7),
                    parents.get(1).getId().substring(0, 7));
        }
        message("Date: " + format.format(commit.getTimestamp()));
        message(commit.getMessage() + "\n");
    }

    /** Check if there are untracked files in the current branch */
    private static boolean hasUntrackedFile(Commit curHead, Commit checkoutHead) {
        // Check if this file exists in CWD but not in the Commit of current branch head

        // for all files in the current dir,
        for (String plainName : Objects.requireNonNull(plainFilenamesIn(CWD))) {
            // if the file does not exist in current commit -> has untracked file
            if (!curHead.containsFile(plainName) && checkoutHead.containsFile(plainName)) {
                return true;
            }
        }
        return false;
    }

    private static StagingArea getStage(File addFile) {
        return readObject(addFile, StagingArea.class);
    }

    /** Checks if the file in SUCCESSOR has been modified based on ANCESTOR.*/
    private static boolean modified(String plainName, Map<String, String> ancestor,
                                    Map<String, String> successor) {
        // equals == true -> not modified
        boolean res = !ancestor.getOrDefault(plainName, "").equals(successor.getOrDefault(plainName, ""));
        return res;
    }

    /** Given a starting vertex, level-traverses the Commit tree
     *  and returns a map of each visited CommitId to its depth */
    private static Map<String, Integer> bfs(Commit v) {
        Map<String, Integer> idToDepth = new TreeMap<>();    // map for curBranch

        Queue<Commit> q = new LinkedList<>();
        int depth = 0;
        q.offer(v);
        while (!q.isEmpty()) {
            Commit n = q.poll();
            idToDepth.put(n.getId(), depth);
            List<Commit> parents = n.getParent();
            if (parents == null) {
                break;
            }
            for (Commit parent : n.getParent()) {
                if (parent != null) {
                    q.offer(parent);
                }
            }
            depth++; // post level
        }
        return idToDepth;
    }

    /** Check if a split point is the current branch head || is the checked-out branch head */
    private static void checkSplit(Commit split, Commit curBranch, String givenBranchName) {
        Commit givenBranch = getPointer(join(BRANCH_DIR, givenBranchName));
        if (split.compareTo(givenBranch)) {
            message("Given branch is an ancestor of the current branch.");
            System.exit(0);
        } else if (split.compareTo(curBranch)) {
            try {
                checkoutBranch(givenBranchName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            message("Current branch fast-forwarded.");
            System.exit(0);
        }
    }
}
