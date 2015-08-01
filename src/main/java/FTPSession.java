import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * This is essentially a wrapper for FTPClient that allows storage of some state
 * related to the current FTP session.
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

    public FTPSession() {}

    /**
     *
     * @throws IOException
     */
    public void restore() throws IOException {
        super.connect(host, port);

        // If we can't login anymore, throw an exception.
        if (!login(username, password)) {
            throw new IOException("Restoring session failed, bad login");
        }
        // If the local directory no longer exists, change to the local system's
        // current directory.
        if (currentLocalDir == null || !setLocalDir(currentLocalDir)) {
            currentLocalDir = System.getProperty("user.dir");
        }

        // If the remote directory no longer exists, change to the server's
        // current directory.
        if (currentRemoteDir == null || !setRemoteDir(currentRemoteDir)) {
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
    public void connect(String hostname, int port) throws IOException {
        super.connect(hostname, port);
        this.host = hostname;
        this.port = port;
    }

    /**
     * This is a wrapper for FTPClient's login that lets FTPSession hold onto
     * the username and password. It also handles setting up the proper mode
     * and filetype.
     *
     * @param username for logging into FTP server
     * @param password for loggint into FTP server
     * @return true we logged in, false otherwise
     * @throws IOException if there is a communication issue with the server
     */
    public boolean login(String username, String password) throws IOException {
        boolean result = false;

        if (super.login(username, password)) {
            this.username = username;
            this.password = password;
            super.enterLocalPassiveMode();
            super.setFileType(FTP.BINARY_FILE_TYPE);

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
    public String getCurrentLocalDir() {
        return currentLocalDir;
    }

    /**
     * This sets the FTPSession's local working directory to the directory
     * supplied.
     *
     * @param path filepath to use
     * @return true if changed successfully, false otherwise.
     */
    public boolean setLocalDir(String path) {
        boolean result = false;

        File newDir = new File(path);
        if (newDir.exists() && newDir.isDirectory()) {
            currentLocalDir = newDir.getAbsolutePath();
            result = true;
        }

        return result;
    }

    /**
     * This creates a new File using the current local directory as the new
     * File's parent. Use this if your intent is to create a file or directory
     * in the FTPSession's current working local directory.
     *
     * @param filepath relative filepath of file to create
     * @return a new File with the current local directory as its parent directory
     */
    public File createLocalFile(String filepath) {
        return new File(currentLocalDir, filepath);
    }

    /**
     * This sets the server's remote directory to the provided directory.
     *
     * @param path filepath of remote directory
     * @return true if changed successfully, false otherwise
     * @throws IOException
     */
    public boolean setRemoteDir(String path) throws IOException {
        boolean result = false;

        if (super.changeWorkingDirectory(path)) {
            currentRemoteDir = path;
            result = true;
        }

        return result;
    }

}
