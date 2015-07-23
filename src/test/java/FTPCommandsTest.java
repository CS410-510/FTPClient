import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * Tests for FTPCommands class
 *
 * @author Ryan
 */
public class FTPCommandsTest {

    FTPClient ftp = new FTPClient();
    String server = "52.25.152.38";
    String user = "ftptestuser";
    String pass = "password";

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
     * Assert the file put on the server is really there
     *
     * @throws Exception
     */
    @Test
    public void testPutFile() throws Exception {
        FTPFile[] files;
        files = ftp.listFiles();
        for (FTPFile file: files) {
            if(file.getName().equals("putGoodTest.txt")) {
                assertTrue("File exists", file.isFile());
            } else {
                assertTrue("Does not exist", file.isFile());
            }
        }
    }
}
