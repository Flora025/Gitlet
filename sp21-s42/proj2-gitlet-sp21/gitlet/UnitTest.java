package gitlet;

import org.junit.Assert;
import org.junit.Test;
import java.io.File;
import java.io.IOException;

import gitlet.Utils;

public class UnitTest {
    /** Test Repository.init() */
    @Test
    public void testInit() {
        try {
            Repository.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File CWD = new File(System.getProperty("user.dir"));
        File GITLET_DIR = Utils.join(CWD, ".gitlet");

        Assert.assertTrue(GITLET_DIR.exists());
    }

    /** Test Repository.add() */
    @Test
    public void testAdd() {
        // placeholder
    }
}
