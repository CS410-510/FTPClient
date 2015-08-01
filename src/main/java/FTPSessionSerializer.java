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
    // This is used for creating a default save file in the System's current
    // directory (typically the directory where the project is executed).
    static final String SESSION_FILENAME = ".savedsession";

    /**
     * This handles the deserialization of a FTPSession and is a convenience
     * wrapper for loading from a default file. If an issue is encountered
     * during deserialization, this will return null.
     *
     * @return a restored FTPSession
     * @throws IOException if an error is encountered during reading the stream
     * @throws ClassNotFoundException if an error is encountered during deserialization
     */
    public static FTPSession deserialize() throws IOException, ClassNotFoundException {
        return deserialize(SESSION_FILENAME);
    }

    /**
     * This handles the deserialization of a FTPSession from a specific file. If
     * an issue is encountered during deserialization, this will return null.
     *
     * @param filepath absolute path of file to deserialize from
     * @return a restored FTPSession
     * @throws IOException if an error is encountered during reading the stream
     * @throws ClassNotFoundException if an error is encountered during deserialization
     */
    public static FTPSession deserialize(String filepath) throws IOException, ClassNotFoundException {
        FTPSession session;

        try (ObjectInputStream filein = new ObjectInputStream(new FileInputStream(filepath))) {
            session = (FTPSession) filein.readObject();
            session.restore();
        }

        return session;
    }

    /**
     * This handles the serialization of a FTPSession and is a convenience wrapper
     * for saving to a default file.
     *
     * @param session an FTPSession
     * @throws IOException if an error is encountered during serialization
     */
    public static void serialize(FTPSession session) throws IOException {
        serialize(session, SESSION_FILENAME);
    }

    /**
     * This handles the serialization of a FTPSession to a provided file.
     *
     * @param session an FTPSession
     * @param filepath absolute path of file to serialize to
     * @throws IOException if an error is encountered during serialization
     */
    public static void serialize(FTPSession session, String filepath) throws IOException {
        try (ObjectOutputStream fileout = new ObjectOutputStream(new FileOutputStream(filepath))) {
            fileout.writeObject(session);
        }
    }
}
