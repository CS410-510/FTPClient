import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

/**
 * Tests for FTPSessionSerializer
 */
@RunWith(Enclosed.class)
public class FTPSessionSerializerTest {

    @FixMethodOrder (MethodSorters.NAME_ASCENDING)
    public static class goodSerialization {
        private static FTPSession originalSession, restoredSession;
        private static final String HOST = "52.25.152.38";
        private static final int PORT = 21;
        private static final String USER = "ftptestuser";
        private static final String PASSWORD = "password";
        private static File savedSession;
        private static String newDir = "dirForFTPSerializerTest";

        @BeforeClass
        public static void setup() throws Exception {
            originalSession = new FTPSession();
            originalSession.connect(HOST, PORT);
            originalSession.login(USER, PASSWORD);
            savedSession = new File(FTPSessionSerializer.SESSION_FILENAME);
        }

        @AfterClass
        public static void teardown() throws Exception {
            originalSession.disconnect();
            savedSession.delete();
        }

        @Test
        public void test01CanSerializeSession() throws Exception {
            assertTrue(originalSession.isConnected());
            File testDir = originalSession.createLocalFile(newDir);
            testDir.mkdir();
            originalSession.setLocalDir(testDir.getPath());
            FTPSessionSerializer.serialize(originalSession);
            assertTrue(savedSession.exists());
        }

        @Test
        public void test02CanDeserializeSession() throws Exception {
            restoredSession = FTPSessionSerializer.deserialize();
            File testDir = new File(newDir);
            testDir.deleteOnExit();
            assertTrue(restoredSession.getCurrentLocalDir().equals(testDir.getAbsolutePath()));
            assertTrue(restoredSession.isConnected());
        }
    }

    @FixMethodOrder (MethodSorters.NAME_ASCENDING)
    public static class badSerialization {
        @Test (expected = IOException.class)
        public void serializationWithNoFileThrowsException() throws Exception {
            FTPSession session = FTPSessionSerializer.deserialize();
        }
    }
}
