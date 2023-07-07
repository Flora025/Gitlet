package gitlet;

import org.junit.Assert;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static gitlet.Utils.*;

public class UnitTest {
    public static final File CWD = Repository.CWD;

    /** Test Repository.init() */
    @Test
    public void testInit() {
        try {
            Repository.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File CWD = new File(System.getProperty("user.dir"));
        File GITLET_DIR = join(CWD, ".gitlet");

        Assert.assertTrue(GITLET_DIR.exists());
    }

    /** Test Repository.add() */
    @Test
    public void testAdd() {

        // Create a new file in cwd
        File randomFile = join(CWD, "hello.txt");
        try {
            randomFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Use the add(file) command: add hell0.txt
        Repository.add("hello.txt");

        // Validations: ok
        // 1. Check if the blob_folder saves the blob
        File fp = join(Blob.BLOB_FOLDER, sha1("hello.txt")); // note: there's no content so just hash the name
        Assert.assertTrue(fp.exists());
        // 2. Check if the nameToHash map has been saved into the staging area [Add]
        HashMap<String, String> expected = new HashMap<>();
        String text = "hello.txt";
        expected.put(text, sha1(text));
        Assert.assertEquals(expected, readObject(Repository.Add, HashMap.class));
        // 3. Add content and check 2 again
        String content = "goodbye";
        writeContents(randomFile, content); // add content
        Repository.add("hello.txt"); // use <add> command
        expected.put(text, sha1(text + content));
        Assert.assertEquals(expected, readObject(Repository.Add, HashMap.class));
    }

    // TODO add tests for commit and rm
}
