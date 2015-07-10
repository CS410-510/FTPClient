
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    static FTPClient ftp = new FTPClient();
    static FTPCommands commands = new FTPCommands();

    /**
     * This is basically how I had my main set up
     * when I did my local testing on my local server.  We
     * can keep it like this or change it/add on to it but
     * my feelings won't be hurt if it's changed :)  -Ryan
     */
    public static void main(String[] args) {

        String server, user, pass;
        int port = 21;

        Scanner reader = new Scanner(System.in);
        System.out.print("Enter server name: ");
        server = reader.nextLine();

        System.out.print("Username: ");
        user = reader.nextLine();

        System.out.print("Password: ");
        pass = reader.nextLine();

        try {
            ftp.connect(server, port);
            ftp.login(user, pass);
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            System.out.println("********************* TEAM FOOD FTP CLIENT *********************");
            System.out.println("1.  List Files and Folders");
            System.out.println("2.  add more as we go...");
            System.out.println("****************************************************************");
            System.out.println();

            if(reader.nextInt() == 1) {
                commands.listFilesFolders(ftp);
            } else {
                System.out.println("Peace");
            }

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
