import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

/**
 * This class handles the serialization and deserialization of an FTPSession.
 *
 * @author Sergio Garcia
 */
public class FTPSessionSerializer {
    static final String SESSION_FILENAME = ".savedsession";

    /**
     * This handles the deserialization of a FTPSession. If an issue is encountered
     * during deserialization, this will return null.
     *
     * @return a restored FTPSession
     * @throws IOException if an error is encountered during reading the stream
     * @throws ClassNotFoundException if an error is encountered during deserialization
     */
    public static FTPSession deserialize() throws IOException, ClassNotFoundException {
        FTPSession session;

        try (ObjectInputStream filein = new ObjectInputStream(new FileInputStream(SESSION_FILENAME))) {
            session = (FTPSession) filein.readObject();
            session.restore();
        }

        return session;
    }

    /**
     * This handles the serialization of a FTPSession.
     *
     * @param session an FTPSession
     * @throws IOException if an error is encountered during serialization
     */
    public static void serialize(FTPSession session) throws IOException {
        try (ObjectOutputStream fileout = new ObjectOutputStream(new FileOutputStream(SESSION_FILENAME))) {
            fileout.writeObject(session);
        }
    }
}
