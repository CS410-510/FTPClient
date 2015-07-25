import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.Assert.*;

/**
 * Tests for FTPCommands class
 *
 * @author Ryan
 * @author Sergio
 */
public class FTPCommandsTest {

    FTPClient ftp = new FTPClient();
    String server = "52.25.152.38";
    String user = "ftptestuser";
    String pass = "password";
    FTPCommands commands = new FTPCommands();


    /**
     * Added this rule to let us use temporary files during tests that are
     * automatically cleaned up after test completion.
     */
    @Rule   // Needs to be public
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * arbitrary like Main.java is set up.  We can adjust later -Ryan
     *
     * @throws Exception
     */
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
     * Assert the client is connected to the server after connecting
     *
     * @throws Exception
     */
    @Test
    public void testIsConnected() throws Exception {
        assertTrue("Client is not connected", ftp.isConnected());
    }

    /**
     * Just show what is a folder and file listed in the Remote directory
     *
     * @throws Exception
     */
    @Test
    public void testListFilesFolders() throws Exception {
        FTPFile[] files;
        files = ftp.listFiles();

        //one folder and two files in test directory
        for (FTPFile file : files) {
            if(file.isDirectory()) {
                System.out.println("Folders Found");
            } else if(file.isFile()) {
                System.out.println("File Found");
            } else {
                fail("Something else found");
            }
        }
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
     * Helper method, this finds a file whose name matches the arg
     * on the server.
     *
     * @param filename name of file to find
     * @return true if found, false otherwise
     * @throws IOException if there's an issue on the server side
     */
    private boolean findFileOnRemote(String filename) throws IOException {
        FTPFile[] files = ftp.listFiles(filename);

        for (FTPFile f : files) {
            if (f.getName().equals(filename)) {
                return true;
            }
        }
        return false;
    }


    /**
     * This test tries to put a new file on the server using a filepath.
     * This creates a temp file, uploads it to the server, checks if it
     * exists on the server, and then removes it.
     *
     * @throws Exception
     */
    @Test
    public void testPutAnotherFile() throws Exception {
        File testfile = makeTestFile("hey! banana!");

        FTPCommands commands = new FTPCommands();
        commands.putRemoteFile(ftp, testfile.getPath());
        assertTrue("file not found", findFileOnRemote(testfile.getName()));
        assertTrue("file not deleted", ftp.deleteFile(testfile.getName()));
    }

    /**
     * This test checks if an existing file on the server can be overwritten.
     * This creates a temp file, uploads it to the server, modifies it locally,
     * uploads it to the server again, and then grabs the server's copy and
     * compares if the server's copy matches the updated local file.
     *
     * @throws Exception
     */
    @Test
    public void testPutOverwritesFileOnServer() throws Exception {
        File testfile = makeTestFile("hey! apple!");
        commands.putRemoteFile(ftp, testfile.getPath());

        appendToFile(testfile, "hey! banana!");
        commands.putRemoteFile(ftp, testfile.getPath());

        File found = folder.newFile();
        ftp.retrieveFile(testfile.getName(), new FileOutputStream(found));
        assertTrue("files don't match!", FileUtils.contentEquals(testfile, found));
    }

    /**
     * This test checks if putRemoteFile can upload multiple files to the
     * server.
     *
     * @throws Exception passed up from helper methods
     */
    @Test
    public void testPutMultipleFilesOnServer() throws Exception {
        ArrayList<File> files = new ArrayList<>();
        for (int i = 1; i <= 5; ++i) {
            files.add(makeTestFile("testfile #" + String.valueOf(i)));
        }

        // Take the files collection, map each file to its path, collect it
        // as a new list, convert the new list to a String array, and pass the array
        // as an arg to putRemoteFile. Mimicking the use of multiple-arg options,
        // don't worry if this looks bananas.
        commands.putRemoteFile(ftp, files.stream()
                                         .map(f -> f.getPath())
                                         .collect(Collectors.toList())
                                         .toArray(new String[files.size()]));

        for (File file : files) {
            assertTrue("file not found", findFileOnRemote(file.getName()));
            assertTrue("file not deleted", ftp.deleteFile(file.getName()));
        }
    }
}
