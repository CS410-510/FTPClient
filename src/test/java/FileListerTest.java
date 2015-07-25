import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

import static junit.framework.Assert.fail;

/**
 * Tests for FileLister class
 *
 * @author Sergio Garca
 */
public class FileListerTest {

    FTPClient ftp = new FTPClient();
    String server = "52.25.152.38";
    String user = "ftptestuser";
    String pass = "password";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    FileLister lister = FileLister.getInstance();

    @Before
    public void setUp() throws Exception {
        ftp.connect(server, 21);
        ftp.login(user, pass);
        ftp.enterLocalPassiveMode();
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
    }

    @After
    public void tearDown() throws Exception {
        ftp.disconnect();
        assertTrue(true);
    }


    /**
     * Helper method, makes a test file using the text passed in as an arg.
     * @param text contents for file
     * @return file with contents
     * @throws IOException if an issue opening file is encountered
     */
    private File makeTestFile(String text) throws IOException {
        File testfile = folder.newFile();
        try (PrintWriter writer = new PrintWriter(new FileWriter(testfile), true)) {
            writer.println(text);
        } catch (IOException e) {
            fail("problem w/ test: can't create local test file");
        }

        return testfile;
    }

    /**
     * Helper method, appends a new line to a currently existing file
     * @param file file to add line to
     * @param text text of new line
     */
    private void appendToFile(File file, String text) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true), true)) {
            writer.println(text);
        } catch (IOException e) {
            fail("problem w/ test: can't create local test file");
        }
    }

    /**
     * This test checks if the FileLister can print local files.
     *
     * @throws Exception
     */
    @Test
    public void testCanPrintLocalFiles() throws Exception {
        ArrayList<File> fileList = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            fileList.add(makeTestFile(String.valueOf(i)));
        }
        folder.newFolder("testdir");
        folder.newFolder("whoodir");

        String files = lister.listFilesAndDirs(folder.getRoot());

        for (File file : fileList) {
            assertTrue("can't find file", files.contains(file.getName()));
        }
        assertTrue("directories should exist", files.contains("testdir/") && files.contains("whoodir/"));
    }

    /**
     * This test checks if the FileLister can print remote files.
     */
    @Test
    public void testCanPrintRemoteFiles() {
        String files = null;
        try {
            files = lister.listFilesAndDirs(ftp.listFiles());
        } catch (IOException e) {
            fail("shouldn't fail here");
        }

        assertTrue("remote directory should exist", files.contains("for_testing"));
    }
}
