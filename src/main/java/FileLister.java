/**
 * Created by serge on 7/24/15.
 */
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.util.Collection;

/**
 * Set up as a singleton, use getInstance() to get a reference to it.
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

        StringBuilder sb = new StringBuilder(directory.getParent() + nl + ".." + nl);
        String name;
        for( File file : files ) {
            name = file.getName();
            sb.append((file.isDirectory() && name.equals(directory.getName())
                      ? "." : (name + (file.isDirectory() ? "/" : ""))) + nl);
        }

        return new String(sb);
    }

    /**
     * This method returns the contents of a remote directory in string format.
     * @param files an array of FTPFiles
     * @return the contents of the directory as a string
     */
    public String listFilesAndDirs(FTPFile[] files) {
        StringBuilder sb = new StringBuilder();
        String name;

        for( FTPFile file : files ) {
            name = file.getName();
            sb.append(name + (file.isDirectory() ? "/" : "") + nl);
        }

        return new String(sb);
    }

}
