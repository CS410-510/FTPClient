import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.util.Collection;

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
    public void connect(FTPClient ftp, String server) {

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
    public void listFilesFolders(FTPClient ftp) {

        // list files
        FTPFile[] files = new FTPFile[0];
        try {
            files = ftp.listFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //iterate through to get files
        System.out.println();
        //"Pretty Printing"  subject to change
        System.out.println("Type\t\t" + "Name\t\t\t" + "Size");
        for (FTPFile file : files) {
            String details = file.getName();
            if (file.isDirectory()) {
                details = "Folder:\t\t[" + details + "]";
            } else {
                details = "File:\t\t" + details;
            }
            // Added size, thought it was a nice touch other than just the name and type
            // can also easily add a Date to it
            details += "\t\t" + file.getSize();
            System.out.println(details);
            System.out.println();
        }
    }

    /**
     * Get a file from the remote server and place it in the current local working directory.
     * The location where the retrieved file ends up can be changed if needed. Maybe we could
     * provide the option to specify a location.
     */
    public void getRemoteFile(FTPClient ftp, String filepath) {

        File file = new File(filepath);

        try (OutputStream dest = new FileOutputStream(file)) {
            ftp.retrieveFile(file.getName(), dest);
            System.out.println(file.getName() + " has been placed in your current working directory");
        } catch (IOException e) {
            System.out.println("Error retrieving file");
            e.printStackTrace();
        }
    }
    /**
     * Get a multiple files from the remote server and place it in the current local working directory.
     * This function is overloaded.
     */
    public void getRemoteFile(FTPClient ftp, String... files) {
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
    public void putRemoteFile(FTPClient ftp, String filepath) {
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
    public void putRemoteFile(FTPClient ftp, String... filepaths) {
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
    public void createRemoteDirectory(FTPClient ftp, String path) {
        try {
            ftp.makeDirectory(path);
        } catch (IOException e) {
            System.out.println();
            e.printStackTrace();
        }
        System.out.println("'" + path + "' was created");
    }

    /**
     * Perform logoff and disconnect functions
     */
    public void exit(FTPClient ftp) {

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
