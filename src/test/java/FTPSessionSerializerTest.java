import org.junit.*;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Tests for FTPSessionSerializer
 */
@RunWith(Enclosed.class)
public class FTPSessionSerializerTest {

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class goodSerialization {
        @ClassRule
        public static TemporaryFolder folder = new TemporaryFolder();

        private static FTPSession originalSession, restoredSession;
        private static final String HOST = "52.25.152.38";
        private static final int PORT = 21;
        private static final String USER = "ftptestuser";
        private static final String PASSWORD = "password";
        private static File newDir;
        private static File savedSessionFile;

        @BeforeClass
        public static void setup() throws Exception {
            originalSession = new FTPSession();
            originalSession.connect(HOST, PORT);
            originalSession.login(USER, PASSWORD);
            savedSessionFile = folder.newFile();
            newDir = folder.newFolder();
        }

        @AfterClass
        public static void teardown() throws Exception {
            originalSession.disconnect();
        }

        @Test
        public void test01CanSerializeSession() throws Exception {
            assertTrue("session is not connected", originalSession.isConnected());
            // Adding a directory to change session's local directory.
            newDir.mkdir();
            originalSession.changeLocalDirectory(newDir.getPath());
            FTPSessionSerializer.serialize(originalSession, savedSessionFile.getAbsolutePath());
            assertTrue("serialization file doesn't exist", savedSessionFile.exists());
        }

        @Test
        public void test02CanDeserializeSession() throws Exception {
            restoredSession = FTPSessionSerializer.deserialize(savedSessionFile.getAbsolutePath());
            assertTrue("restored session missing new directory",
                    restoredSession.getLocalDirectory().equals(newDir.getAbsolutePath()));
            assertTrue("restored session not connected", restoredSession.isConnected());
        }
    }

    @FixMethodOrder (MethodSorters.NAME_ASCENDING)
    public static class badSerialization {
        @Test (expected = IOException.class)
        public void deserializationWithNoFileThrowsException() throws Exception {
            FTPSession session = FTPSessionSerializer.deserialize("nonexistentfile");
        }

        @Test (expected = IOException.class)
        public void deserializationWithEmptyFileThrowsException() throws Exception {
            File badsession = new File("emptyfile");
            badsession.createNewFile();
            badsession.deleteOnExit();
            FTPSession session = FTPSessionSerializer.deserialize(badsession.getAbsolutePath());
        }

        @Test (expected = IOException.class)
        public void deserializationWithBadFileThrowsException() throws Exception {
            File badsession = new File("badfile");
            badsession.createNewFile();
            badsession.deleteOnExit();
            PrintWriter pw = new PrintWriter(badsession);
            pw.println("FTPSessionSerializer is for nerds!");
            pw.flush();
            pw.close();
            FTPSession session = FTPSessionSerializer.deserialize(badsession.getAbsolutePath());
        }

    }
}
