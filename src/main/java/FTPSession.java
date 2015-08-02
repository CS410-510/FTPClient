import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * This is essentially a wrapper for FTPClient that allows storage of some state
 * related to the current FTP session. Some FTPClient methods are overridden to
 * do some work with arguments here, but all overriding methods call their
 * corresponding superclass methods.
 *
 * @author Sergio Garcia
 */
public class FTPSession extends FTPClient implements Serializable {
    private String host;
    private int port;
    private String username;
    private String password;
    private String currentLocalDir = null;
    private String currentRemoteDir = null;
    // Adding for serialization
    private static final long serialVersionUID = -8523696657891305249L;

    public FTPSession() {}

    /**
     * This method handles restoring a session's state if restoring from serialization.
     *
     * @throws IOException if the session can't connect to its server
     */
    public void restore() throws IOException {
        super.connect(host, port);

        // If we can't login anymore, throw an exception.
        if (!login(username, password)) {
            throw new IOException("Restoring session failed, bad login");
        }
        // If the local directory no longer exists, change to the local system's
        // current directory.
        if (currentLocalDir == null || !changeLocalDirectory(currentLocalDir)) {
            currentLocalDir = System.getProperty("user.dir");
        }

        // If the remote directory no longer exists, change to the server's
        // current directory.
        if (currentRemoteDir == null || !changeWorkingDirectory(currentRemoteDir)) {
            currentRemoteDir = super.printWorkingDirectory();
        }
    }

    /**
     * This is a wrapper for FTPClient's connect that lets FTPSession hold onto
     * the hostname and port.
     *
     * @param hostname for connecting to FTP server
     * @param port for connecting to FTP server
     * @throws IOException
     */
    @Override
    public void connect(String hostname, int port) throws IOException {
        super.connect(hostname, port);
        this.host = hostname;
        this.port = port;
    }

    /**
     * This is a wrapper for FTPClient's login that lets FTPSession hold onto
     * the username and password.
     *
     * @param username for logging into FTP server
     * @param password for loggint into FTP server
     * @return true we logged in, false otherwise
     * @throws IOException if there is a communication issue with the server
     */
    @Override
    public boolean login(String username, String password) throws IOException {
        boolean result = false;

        if (super.login(username, password)) {
            this.username = username;
            this.password = password;

            // If we don't already have local dirs, set them to defaults.
            if (currentLocalDir == null)
                currentLocalDir = System.getProperty("user.dir");
            if (currentRemoteDir == null)
                currentRemoteDir = super.printWorkingDirectory();
            result = true;
        }

        return result;
    }

    /**
     * Getter for current local directory.
     * @return current local directory
     */
    public String getLocalDirectory() {
        return currentLocalDir;
    }

    /**
     * This sets the FTPSession's local working directory to the directory
     * supplied.
     *
     * @param path filepath to use
     * @return true if changed successfully, false otherwise.
     */
    public boolean changeLocalDirectory(String path) {
        boolean result = false;

        File newDir = new File(path);
        if (newDir.exists() && newDir.isDirectory()) {
            currentLocalDir = newDir.getAbsolutePath();
            result = true;
        }

        return result;
    }

    /**
     * This sets the server's remote directory to the provided directory.
     *
     * @param path filepath of remote directory
     * @return true if changed successfully, false otherwise
     * @throws IOException
     */
    @Override
    public boolean changeWorkingDirectory(String path) throws IOException {
        boolean result = false;

        if (super.changeWorkingDirectory(path)) {
            currentRemoteDir = path;
            result = true;
        }

        return result;
    }

}
