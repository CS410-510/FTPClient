import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
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
        }
        return instance;
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
            sb.append(file.getName()).append((file.isDirectory() ? "/" : "")).append(nl);
        }

        return new String(sb);
    }

}
