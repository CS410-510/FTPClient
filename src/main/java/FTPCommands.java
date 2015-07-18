import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;

/**
 * We can put all the commands that will be available in this
 * class that way it's easy to keep track of and update.
 *
 * @author Ryan
 */
public class FTPCommands {

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
}
