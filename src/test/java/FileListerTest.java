import org.apache.commons.net.ftp.FTPFile;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Tests for FileLister class
 *
 * @author Sergio Garcia
 */
public class FileListerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    FileLister lister = FileLister.getInstance();


    /**
     * Helper method, makes a test file using the text passed in as an arg.
     * @param text contents for file
     * @return file with contents
     * @throws IOException if an issue opening file is encountered
     */
    private File makeTestFile(String text) throws IOException {
        File testfile = folder.newFile();
        try (PrintWriter writer = new PrintWriter(new FileWriter(testfile), true)) {
            writer.println(text);
        } catch (IOException e) {
            fail("problem w/ test: can't create local test file");
        }

        return testfile;
    }

    /**
     * This test checks if the FileLister can print local files, including
     * empty directories.
     *
     * @throws Exception
     */
    @Test
    public void testCanPrintLocalFiles() throws Exception {
        String files = lister.listFilesAndDirs(folder.getRoot());

        assertEquals("files should be empty", "Empty directory", files);

        ArrayList<File> fileList = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            fileList.add(makeTestFile(String.valueOf(i)));
        }
        folder.newFolder("testdir");
        folder.newFolder("whoodir");

        files = lister.listFilesAndDirs(folder.getRoot());

        for (File file : fileList) {
            assertTrue("can't find file", files.contains(file.getName()));
        }
        assertTrue("directories should exist", files.contains("testdir/") && files.contains("whoodir/"));
    }

    /**
     * This creates a mock FTPFile with a name containing num.
     *
     * @param num number for filename
     * @param isDir true if returning a directory
     * @return a FTPFile directory if isDir is true, a FTPFile otherwise.
     */
    private FTPFile getMockFTPFile(int num, boolean isDir) {
        FTPFile remoteFile = mock(FTPFile.class);
        when (remoteFile.getName()).thenReturn("remote_" + (isDir ? "dir_" : "file_") + num);
        when (remoteFile.hasPermission(anyInt(), anyInt())).thenReturn(true);
        when (remoteFile.getSize()).thenReturn(1000000L * num);
        when (remoteFile.isDirectory()).thenReturn(isDir);
        when (remoteFile.getTimestamp()).thenReturn(Calendar.getInstance());
        return remoteFile;
    }

    /**
     * This sets up a mock FTPFile directory with nFiles files and nDirs
     * directories.
     *
     * @param nFiles number of files
     * @param nDirs number of directories
     * @return new array of mock FTPFile
     */
    private FTPFile[] setUpRemoteTestDir(int nFiles, int nDirs) {
        ArrayList<FTPFile> fileList = new ArrayList<>();
        for (int i = 0; i < nFiles; ++i) {
            fileList.add(getMockFTPFile(i, false));
        }

        for (int i = 0; i < nDirs; ++i) {
            fileList.add(getMockFTPFile(i, true));
        }

        return fileList.toArray(new FTPFile[fileList.size()]);
    }

    /**
     * This test checks if the FileLister can print remote files.
     */
    @Test
    public void testCanPrintRemoteFiles() throws Exception {

        int nFiles = 4;
        int nDirs = 3;

        FTPFile[] mockFiles = setUpRemoteTestDir(nFiles, nDirs);
        String files = lister.listFilesAndDirs(mockFiles);

        for (int i = 0; i < nFiles; ++i) {
            assertTrue("remote file should exist", files.contains("remote_file_" + i));
        }

        for (int j = 0; j < nDirs; ++j)  {
            assertTrue("remote directory should exist", files.contains("remote_dir_" + j + "/"));
        }

        // Verifying that our mock objects were actually used within the file lister.
        for (FTPFile f : mockFiles) {
            verify(f).getSize();
            verify(f).getName();
            verify(f, atLeastOnce()).isDirectory();
            verify(f).getTimestamp();
            verify(f, atLeastOnce()).hasPermission(anyInt(), anyInt());
        }
    }
}
