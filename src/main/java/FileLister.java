import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Collection;

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
        }
        return instance;
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
        Collection<File> files = FileUtils.listFilesAndDirs(directory,
                                                            TrueFileFilter.INSTANCE,
                                                            TrueFileFilter.INSTANCE);

        StringBuilder sb = new StringBuilder();

        for( File file : files ) {
            sb.append(printFileDetails(file));
            sb.append(file.getName()).append((file.isDirectory() ? "/" : "")).append(nl);
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

        for( FTPFile file : files ) {
            sb.append(printFileDetails(file));
            sb.append(file.getName()).append((file.isDirectory() ? "/" : "")).append(nl);
        }

        return new String(sb);
    }

}
