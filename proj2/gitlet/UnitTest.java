//package gitlet;
//
//import org.junit.Assert;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.TreeMap;
//
//import static gitlet.Utils.*;
//
//public class UnitTest {
//    public static final File CWD = Repository.CWD;
//    public static final File GITLET_DIR = join(CWD, ".gitlet");
//
//    /** Run to delete test directories and files */
//    @Test
//    public void clean() {
//        cleanRepo(CWD);
//    }
//
//    /** Test Repository.init() */
//    @Test
//    public void testInit() {
//        clean();
//        try {
//            Repository.init();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        File CWD = new File(System.getProperty("user.dir"));
//        File GITLET_DIR = join(CWD, ".gitlet");
//
//        Assert.assertTrue(GITLET_DIR.exists());
//    }
//
//    /** Test Repository.add() */
//    @Test
//    public void testAdd() throws IOException {
//        clean();
//        Repository.init();
//        // Create a new file in cwd
//        File randomFile = join(CWD, "hello.txt");
//        if (!randomFile.exists()) {
//            try {
//                randomFile.createNewFile();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        // Use the add(file) command: add hell0.txt
//        Repository.add("hello.txt");
//
//        // Validations: ok
//        // 1. Check if the blob_folder saves the blob
//        File fp = join(Blob.BLOB_FOLDER, sha1("hello.txt")); // note: there's no content so just hash the name
//        Assert.assertTrue(fp.exists());
//        // 2. Check if the nameToHash map has been saved into the staging area [Add]
//        TreeMap<String, String> expected = new TreeMap<>();
//        String text = "hello.txt";
//        expected.put(text, sha1(text));
//        Assert.assertEquals(expected, readObject(Repository.ADD_FILE, StagingArea.class).getAreaMap());
//        // 3. Add content and check 2 again
//        String content = "goodbye";
//        writeContents(randomFile, content); // add content
//        Repository.add("hello.txt"); // use <add> command
//        expected.put(text, sha1(text + content));
//        Assert.assertEquals(expected, readObject(Repository.ADD_FILE, StagingArea.class).getAreaMap());
//    }
//
//    /** Test Repository.commit() */
//    @Test
//    public void testCommit() throws IOException {
//        if (GITLET_DIR.exists()) {cleanRepo(CWD);}
//        Repository.init();
//        // test failure cases: no file to commit
//        // Steps: init repo (done) && commit
//
//        Repository.commit("commit nothing"); // [ok] out: No changes added to the commit.
//    }
//
//    /** Test add() and commit() */
//    @Test
//    public void testAddandCommit() throws IOException {
//        if (GITLET_DIR.exists()) {cleanRepo(CWD);}
//        Repository.init();
//
//        // steps: 1. add higedan.txt 2. add yorushika.txt 3. commit "add favorite bands"
//        // create file "higedan.txt"
//        File hgd = join(CWD, "higedan.txt");
//        hgd.createNewFile();
//        // create another file "yorushika.txt"
//        File yrsk = join(CWD, "yorushika.txt");
//        yrsk.createNewFile();
//
//        // call add() func
//        Repository.add("higedan.txt");
//        Repository.add("yorushika.txt");
//        // By now there should be two blobs in BLOB_FOLDER, two items in the StagedAddition Map
//        Assert.assertEquals(2, readObject(Repository.ADD_FILE, StagingArea.class).size());
//
//        // call commit() func
//        Repository.commit("add favorite bands");
//        // message("[0] commit success"); // [ok]
//    }
//
//    /** Test Repository.rm() */
//    @Test
//    public void testRm() throws IOException {
//        testAddandCommit();
//        File hgd = join(CWD, "higedan.txt"); // already created
//        File yrsk = join(CWD, "yorushika.txt"); // already created
//        // modify file contents
//        writeContents(hgd, "Coffee to Syrup");
//        writeContents(yrsk, "itte");
//
//        Repository.add("yorushika.txt");
//        Repository.add("higedan.txt");
//        Repository.commit("add fav songs"); // 【1】commit
//        writeContents(yrsk, readContents(yrsk), "Setting Sun");
//        Repository.add("yorushika.txt"); // now yrsk is in StagedAddition
//        Repository.rm("yorushika.txt"); // remove yrsk
//        // Expected behavior: yrsk removed from CWD, stg and the new Commit map
//        // System.out.println(Repository.getHeadInfo());
//        // Repository.commit("delete one"); // 【2】 commit FIXME
//        // System.out.println(Repository.getHeadInfo());
//
//
//        // finally, check Staged Area (should be clean)
//        Assert.assertEquals(0, readObject(Repository.ADD_FILE, StagingArea.class).size()); // added but not yet committed
//        Assert.assertEquals(1, readObject(Repository.RM_FILE, StagingArea.class).size()); // added to rm area but not yet committed
//
//    }
//
//    /** Test Repository.log() */
//    @Test
//    public void testLog() {
//        try {
//            testRm();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        Repository.log(); // [ok]
//        clean();
//    }
//
//    /** Test Repository.globalLog() */
//    @Test
//    public void testGlobalLog() {
//        try {
//            testRm();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        Repository.globalLog(); // [ok]
//        clean();
//    }
//
//    /** Test Repository.find() */
//    @Test
//    public void testFind() {
//        try {
//            testRm();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        Repository.find("add fav songs"); // [ok]
//        Repository.find("this msg never exists"); // [ok]
//        clean();
//    }
//
//    /** Test Repository.status() */
//    @Test
//    public void testStatus() {
//        try {
//            testRm();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        Repository.status();
//        clean();
//    }
//
//    /** Test Repository.checkout(xxx) */
//    @Test
//    public void testCheckout() {
//        // TODO
//        clean();
//    }
//
//    /** Test Repository.branch(xxx) */
//    @Test
//    public void testBranch() throws IOException {
//        // TODO
//        // add & commits
//
//        Repository.branch("newBranch");
//        clean();
//    }
//
//    /** Test Repository.branch(xxx) */
//    @Test
//    public void testRmBranch() throws IOException {
//        // TODO
//        testBranch();
//
//        Repository.rmBranch("newBranch");
//        clean();
//    }
//
//}
