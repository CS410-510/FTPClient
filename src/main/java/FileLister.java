import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The FileLister uses a collection of Files or array of FTPFiles and uses them
 * to construct a pretty-printed String of the Files/FTPFiles contained within.
 *
 * The FileLister is set up as a singleton, use getInstance() to get a reference to it.
 * To use easily, you can use something like:
 *
 * String output = FileLister.getInstance().listFilesAndDirs([some_collection]);
 *
 * @author Sergio Garcia
 */
public class FileLister {

    private static FileLister instance;
    private static String nl;
    private static SimpleDateFormat dateFormatter;
    private static int sizePadding;

    // Keep this constructor private to avoid unnecessary instantiation.
    private FileLister() {}

    /**
     * This method returns a reference to the singleton instance of the
     * FileLister.
     *
     * @return reference to singleton instance
     */
    public static FileLister getInstance() {
        if (instance == null) {
            instance = new FileLister();
            nl = System.lineSeparator();
            dateFormatter = new SimpleDateFormat("M/d/yyyy HH:mm");
            sizePadding = 12;
        }
        return instance;
    }

    /**
     * This method takes a string and formats it as an n-length
     * string with spaces as left padding.
     *
     * @param s a string
     * @param n length of padded string
     * @return a left-padded string containing s
     */
    private String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    /**
     * This method handles converting file access permissions and last-
     * modified date to "pretty" strings.
     *
     * @param file a file
     * @return file permissions and timestamp as a string
     */
    private String printFileDetails(File file) {
        StringBuilder sb = new StringBuilder();

        // Start building permissions
        sb.append(file.isDirectory() ? "d":"-");
        sb.append(file.canRead() ? "r" : "-");
        sb.append(file.canWrite() ? "w" : "-");
        sb.append(file.canExecute() ? "x" : "-");
        sb.append("    ");

        // Add the filesize.
        sb.append(padLeft(FileUtils.byteCountToDisplaySize(file.length()), sizePadding));
        sb.append("    ");

        // Appending last-modified timestamp
        sb.append(dateFormatter.format(new Date(file.lastModified())));
        sb.append("    ");

        return new String(sb);
    }

    /**
     * This method handles converting file access permissions and last-
     * modified date to "pretty" strings.
     *
     * @param file a file
     * @return file permissions and timestamp as a string
     */
    private String printFileDetails(FTPFile file) {
        StringBuilder sb = new StringBuilder();

        // Start building permissions.
        sb.append(file.isDirectory() ? "d" : "-");
        sb.append(file.hasPermission(FTPFile.USER_ACCESS, FTPFile.READ_PERMISSION)
                ? "r" : "-");
        sb.append(file.hasPermission(FTPFile.USER_ACCESS, FTPFile.WRITE_PERMISSION)
                  ? "w" : "-");
        sb.append(file.hasPermission(FTPFile.USER_ACCESS, FTPFile.EXECUTE_PERMISSION)
                  ? "x" : "-");
        sb.append("    ");

        // Add the filesize.
        sb.append(padLeft(FileUtils.byteCountToDisplaySize(file.getSize()), sizePadding));
        sb.append("    ");

        // Appending the last-modified date to the current string.
        sb.append(dateFormatter.format(file.getTimestamp().getTime()));
        sb.append("    ");

        return new String(sb);
    }

    /**
     * This method returns the contents of a local directory in string format.
     *
     * @param directory a file representing the directory to list
     * @return the contents of the directory as a string
     */
    public String listFilesAndDirs(File directory) {

        File[] files = directory.listFiles();
        StringBuilder sb = new StringBuilder();

        if (files != null && files.length > 0) {
            Arrays.sort(files);
            for( File file : files ) {
                sb.append(printFileDetails(file));
                sb.append(file.getName()).append((file.isDirectory() ? "/" : "")).append(nl);
            }
        } else {
            sb.append("Empty directory");
        }

        return new String(sb);
    }

    /**
     * This method returns the contents of a remote directory in string format.
     *
     * @param files an array of FTPFiles
     * @return the contents of the directory as a string
     */
    public String listFilesAndDirs(FTPFile[] files) {
        StringBuilder sb = new StringBuilder();

        if (files != null && files.length > 0) {
            for (FTPFile file : files) {
                sb.append(printFileDetails(file));
                sb.append(file.getName()).append((file.isDirectory() ? "/" : "")).append(nl);
            }
        } else {
            sb.append("Empty directory");
        }

        return new String(sb);
    }

}
