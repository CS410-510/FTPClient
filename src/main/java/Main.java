
public class Main {

    static FTPSession ftp = new FTPSession();

    public static void main(String[] args) {

        ArgParser.parse(args);
        FTPDispatcher dispatcher = new FTPDispatcher();
        dispatcher.dispatch(args, ftp);
    }
}
