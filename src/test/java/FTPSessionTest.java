import org.apache.commons.net.ftp.FTP;
import org.junit.*;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for the FTPSession class.
 *
 * @author Sergio Garcia
 */
@RunWith(Enclosed.class)
public class FTPSessionTest {

    private static final String HOST = "52.25.152.38";
    private static final int PORT = 21;
    private static final String USER = "ftptestuser";
    private static final String PASSWORD = "password";

    /**
     * This does some incremental work with what's supposed to be a good session.
     */
    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class testGoodSession {

        private static FTPSession session;
        private static File testDir;

        @Rule
        public TemporaryFolder folder = new TemporaryFolder();

        @BeforeClass
        public static void setup() {
            session = new FTPSession();
        }

        @AfterClass
        public static void teardown() throws Exception {
            session.disconnect();
        }

        /**
         * Tests connection functionality.
         *
         * @throws Exception unexpected
         */
        @Test
        public void test01CanConnect() throws Exception {
            session.connect(HOST, PORT);
            assertTrue(session.isConnected());
        }

        /**
         * Tests login functionality.
         *
         * @throws Exception unexpected
         */
        @Test
        public void test02CanLogin() throws Exception {
            assertTrue(session.login(USER, PASSWORD));
            session.enterLocalPassiveMode();
            session.setFileType(FTP.BINARY_FILE_TYPE);
        }

        /**
         * Tests changing local directory.
         */
        @Test
        public void test03CanChangeLocalDirectory() {
            assertTrue(session.changeLocalDirectory(folder.getRoot().getPath()));
        }

        /**
         * Tests creating file in appropriate local directory.
         * @throws Exception unexpected
         */
        @Test
        public void test04CanCreateFileInNewLocalDirectory() throws Exception {
            session.changeLocalDirectory(System.getProperty("user.dir"));
            testDir = new File(session.getLocalDirectory(), "goodSessionTestDir");
            testDir.mkdir();
            testDir.deleteOnExit();
            session.changeLocalDirectory(testDir.getPath());

            File testfile = new File(session.getLocalDirectory(), "goodSessionTest04");
            testfile.createNewFile();
            testfile.deleteOnExit();
            assertTrue(testfile.getAbsolutePath().contains(testDir.getAbsolutePath()));
            // Force deletion now so we can delete containing directory later.
            assertTrue(testfile.delete());
        }

        /**
         * Tests changing remote directory.
         * @throws Exception
         */
        @Test
        public void test06CanChangeRemoteDirectory() throws Exception {
            assertTrue(session.changeWorkingDirectory("for_testing"));
            assertTrue(session.printWorkingDirectory().contains("for_testing"));
        }

        /**
         * Tests restoring session after disconnect.
         *
         * @throws Exception
         */
        @Test
        public void test07CanRestoreSessionAfterDisconnect() throws Exception {
            session.disconnect();
            session.restore();
            assertTrue(session.isConnected());
            assertTrue(session.printWorkingDirectory().contains("for_testing"));
        }

        /**
         * Tests sane default for local directory if directory no longer exists.
         *
         * @throws Exception
         */
        @Test
        public void test08RestoreCanRevertToDefaultLocalIfLocalDirIsRemoved() throws Exception {
            assertTrue(testDir.delete());
            session.disconnect();
            session.restore();
            assertTrue(session.getLocalDirectory().equals(System.getProperty("user.dir")));
        }
    }

    /**
     * This does some incremental work with bad sessions.
     */
    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class testBadSession {
        private static FTPSession session;

        @BeforeClass
        public static void setup() {
            session = new FTPSession();
        }

        @AfterClass
        public static void teardown() throws Exception {
            session.disconnect();
        }

        /** Test handling of bad host. */
        @Test (expected = IOException.class)
        public void test01ConnectWithBadHostThrowsException() throws Exception {
            session.connect("banana", PORT);
        }

        /** Test handling of bad port. */
        @Test (expected = IOException.class)
        public void test02ConnectWithBadPortThrowsException() throws Exception {
            session.connect(HOST, 12345);
        }

        /** Test attempt to login without a connection. */
        @Test (expected = IOException.class)
        public void test03LoginWithoutConnectThrowsException() throws Exception {
            session.login(USER, PASSWORD);
        }

        /** Test attempt to login with bad credentials. */
        @Test
        public void test04LoginWithBadCredentialsFails() throws Exception {
            session.connect(HOST, PORT);
            assertTrue(!session.login("failtestuser", PASSWORD));
            assertTrue(!session.login(USER, "badpass"));
        }

        /** Test attempt to change remote directory to a nonexistent directory. */
        @Test
        public void test05ChangeRemoteDirWithBadDirFails() throws Exception {
            session.login(USER, PASSWORD);
            String missingDir = "eleventy_squash";
            assertTrue(!session.changeWorkingDirectory(missingDir));
            assertTrue(!session.printWorkingDirectory().contains(missingDir));
        }
    }
}