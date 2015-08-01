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

        @Test
        public void test01CanConnect() throws Exception {
            session.connect(HOST, PORT);
            assertTrue(session.isConnected());
        }

        @Test
        public void test02CanLogin() throws Exception {
            assertTrue(session.login(USER, PASSWORD));
        }

        @Test
        public void test03CanChangeLocalDirectory() {
            assertTrue(session.setLocalDir(folder.getRoot().getPath()));
        }

        @Test
        public void test04CanCreateFileInNewLocalDirectory() throws Exception {
            session.setLocalDir(System.getProperty("user.dir"));
            testDir = session.createLocalFile("goodSessionTestDir");
            testDir.mkdir();
            testDir.deleteOnExit();
            session.setLocalDir(testDir.getPath());

            File testfile = session.createLocalFile("goodSessionTest04");
            testfile.createNewFile();
            testfile.deleteOnExit();
            assertTrue(testfile.getAbsolutePath().contains(testDir.getAbsolutePath()));
            session.setLocalDir(System.getProperty("user.dir"));
        }

        @Test
        public void test06CanChangeRemoteDirectory() throws Exception {
            assertTrue(session.setRemoteDir("for_testing"));
            assertTrue(session.printWorkingDirectory().contains("for_testing"));
        }

        @Test
        public void test07CanRestoreAfterDisconnect() throws Exception {
            session.disconnect();
            session.restore();
            assertTrue(session.isConnected());
            assertTrue(session.printWorkingDirectory().contains("for_testing"));
        }

        @Test
        public void test08RestoreCanRevertToDefaultIfLocalDirIsRemoved() throws Exception {
            testDir.delete();
            session.disconnect();
            session.restore();
            assertTrue(session.getCurrentLocalDir().equals(System.getProperty("user.dir")));

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

        @Test (expected = IOException.class)
        public void test01ConnectWithBadHostThrowsException() throws Exception {
            session.connect("banana", PORT);
        }

        @Test (expected = IOException.class)
        public void test02ConnectWithBadPortThrowsException() throws Exception {
            session.connect(HOST, 12345);
        }

        @Test (expected = IOException.class)
        public void test03LoginWithoutConnectThrowsException() throws Exception {
            session.login(USER, PASSWORD);
        }

        @Test
        public void test04LoginWithBadCredentialsFails() throws Exception {
            session.connect(HOST, PORT);
            assertTrue(!session.login("failtestuser", PASSWORD));
            assertTrue(!session.login(USER, "badpass"));
        }

        @Test
        public void test05ChangeRemoteDirWithBadDirFails() throws Exception {
            session.login(USER, PASSWORD);
            String missingDir = "eleventy_squash";
            assertTrue(!session.setRemoteDir(missingDir));
            assertTrue(!session.printWorkingDirectory().contains(missingDir));
        }
    }
}