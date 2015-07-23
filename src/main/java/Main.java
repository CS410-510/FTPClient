import org.apache.commons.net.ftp.FTPClient;

public class Main {

    static FTPClient ftp = new FTPClient();

    public static void main(String[] args) {

        ArgParser.initParser();
        FTPDispatcher dispatcher = new FTPDispatcher();
        dispatcher.dispatch(args, ftp);
    }
}
