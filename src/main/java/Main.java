
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import java.io.IOException;

public class Main {

    static FTPClient ftp = new FTPClient();

    /**
     * This is basically how I had my main set up
     * when I did my local testing on my local server.  We
     * can keep it like this or change it/add on to it but
     * my feelings won't be hurt if it's changed :)  -Ryan
     */
    public static void main(String[] args) {

        //server, user, pass for AWS server
        String server = "52.25.152.38";
        String user = "ftptestuser";
        String pass = "password";

        int port = 21;

        try {
            ftp.connect(server, port);
            ftp.login(user, pass);
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            ArgParser.initParser();
            ArgParser.parse(args, ftp);

        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
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
}
