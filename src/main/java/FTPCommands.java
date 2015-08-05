import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;

/**
 * We can put all the commands that will be available in this
 * class that way it's easy to keep track of and update.
 *
 * @author Ryan
 */
public class FTPCommands {

    /**
     * Default constructor
     */
    public FTPCommands() {}

    /**
     * Handles connection to the specified FTP server. Will check
     * server to see if user is still logged in. If not, will
     * request access credentials
     */
    public void connect(FTPSession ftp, String server) {

        int port = 21;
        Console console = System.console();
        String username = console.readLine("Enter username: ");
        String password = new String(console.readPassword("Enter password: "));

        try {
            ftp.connect(server, port);
            ftp.login(username, password);
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
        }
        catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            //e.printStackTrace();
            exit(ftp);
        }
    }

    /**
     * Lists files and folders using the Apache method
     * listFiles and checks to see if there's a folder in there
     * somewhere too.
     */
    public void listRemoteWorkingDir(FTPSession ftp) {

        // list files
        FTPFile[] files = new FTPFile[0];
        try {
            files = ftp.listFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Using File Lister to prettify directory contents.
        System.out.println(FileLister.getInstance().listFilesAndDirs(files));
    }

    /**
     * List the contents of the current local working directory to standard out.
     */
    public void listLocalWorkingDir(FTPSession ftp) {
        // Get the current local working directory from the system.
        File currentDir = new File(ftp.getLocalDirectory());
        // Use the File Lister to prettify the directory contents.
        System.out.println(FileLister.getInstance().listFilesAndDirs(currentDir));
    }

    /**
     * Get a file from the remote server and place it in the current local working directory.
     *
     * @param ftp Assuming a connection
     * @param filepath Input from cmd line
     */
    public void getRemoteFile(FTPSession ftp, String filepath) {

        // Using a File instance to parse the remote file's name easily.
        File remoteFile = new File(filepath);
        // Construct the new location for the local file using the
        // FTPSession's current directory as the parent.
        File localFile = new File(ftp.getLocalDirectory(), remoteFile.getName());

        try (OutputStream dest = new FileOutputStream(localFile)) {
            ftp.retrieveFile(filepath, dest);
            System.out.println(localFile.getName() + " has been placed in your current working directory");
        } catch (IOException e) {
            System.out.println("Error retrieving file");
            e.printStackTrace();
        }
    }

    /**
     * Overloaded for FTPSession conversion
     *
     * @param ftp Assuming a connection
     * @param files Input from cmd line
     *
     * */
    public void getRemoteFile(FTPSession ftp, String... files) {
        for (String path : files) {
            getRemoteFile(ftp, path);
        }
    }

    /**
     * Put a file from working directory to the remote server
     *
     * @param ftp connection assumed
     * @param filepath argument passed in from command line
     */
    public void putRemoteFile(FTPSession ftp, String filepath) {
        // Switched to using File for work instead of filename string.
        // Avoids failure when string provided is a full path.
        File file = new File(filepath);

        // Switched to try-with-resources, autocloses when done w/ stream
        try (InputStream local = new FileInputStream(file)) {
            // TODO: Silent overwrite if file already exists on server. Maybe change later?
            ftp.storeFile(file.getName(), local);
            System.out.println(file.getName() + " upload complete");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wrapper for putRemoteFile that allows for putting multiple files
     * given a String array of filepaths.
     *
     * @param ftp connection assumed
     * @param filepaths argument array passed in from command line
     */
    public void putRemoteFile(FTPSession ftp, String... filepaths) {
        for (String path : filepaths) {
            putRemoteFile(ftp, path);
        }
    }

    /**
     * Create the specified directory on the FTP server. Some ftp servers
     * do not understand complex paths, so this command will not create all
     * the nonexistent sub-directories on the way to the target directory. This
     * command should be used to create all non-existing sub-directories one by
     * one before attempting to reference the target directory with a relative
     * path.
     *
     * @param ftp connection assumed
     * @param path argument passed from command line
     */
    public void createRemoteDirectory(FTPSession ftp, String path) {
        try {
            ftp.makeDirectory(path);
        } catch (IOException e) {
            System.out.println();
            e.printStackTrace();
        }
        System.out.println("'" + path + "' was created");
    }

    /**
     * Delete specified file from the remote FTP server.
     *
     *
     */
    public void deleteRemoteFile(FTPSession ftp, String path) {
        boolean success = false;
        try {
            success = ftp.deleteFile(path);
        } catch (IOException e) {
            System.out.println();
            e.printStackTrace();
        }

        if (success)
            System.out.println("Remote file '" + path + "' was removed");
        else
            System.out.println("Error deleting '" + path + "' on remote server");

    }

    /**
     * Perform logoff and disconnect functions
     */
    public void exit(FTPSession ftp) {

        try {
            if (ftp.isConnected()) {
                ftp.logout();
                ftp.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
