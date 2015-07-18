import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.io.Console;

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
