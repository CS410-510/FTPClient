import org.apache.commons.net.ftp.FTPClient;

public class Main {

    static FTPClient ftp = new FTPClient();

    /**
     * This is basically how I had my main set up
     * when I did my local testing on my local server.  We
     * can keep it like this or change it/add on to it but
     * my feelings won't be hurt if it's changed :)  -Ryan
     */
    public static void main(String[] args) {

        ArgParser.initParser();
        FTPDispatcher dispatcher = new FTPDispatcher();
        dispatcher.dispatch(args, ftp);

    }
}
