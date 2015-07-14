import org.apache.commons.cli.*;

/**
 * Created by aaron on 7/13/15.
 */
public class ArgParser {
    private static CommandLineParser parser = null;
    private static Options options = null;
    private static HelpFormatter help = null;
    private static OptionGroup opsGrp = null;

    public static void initParser() {
        parser = new DefaultParser();
        options = new Options();
        help = new HelpFormatter();
        opsGrp = new OptionGroup();

        options.addOption("h", "help", false, "Print this help information.");
        options.addOption("C", "connect", true, "Begin or switch the connection context.");
        options.addOption("N", "disconnect", false, "Disconnect from the current connection context.");
        options.addOption("m", "multiple", false, "Used with -g or -p - get or put multiple files.");
        options.addOption("r", "remote", false, "Source is the remote server.");
        options.addOption("R", "recursive", false, "Where allowed, target is a directory.");
        options.addOption("c", "copy", true, "Copy a directory on the remote server, use with -rR.");
        options.addOption("d", "delete", true, "Delete files or directories.");
        options.addOption("g", "get", true, "Without -r, get a single file.");
        options.addOption("l", "list", true, "List files and directories.");
        options.addOption("p", "put", true, "Put a single file.");


    }

    public static void parse(String[] args) {
        try {
            CommandLine line = parser.parse(options, args);

            if (line.getArgList().size() <= 0 || line.hasOption('h') || line.hasOption("help")) {
                help = new HelpFormatter();
                help.printHelp("FTPClient", options, true);
            }
        }
        catch(ParseException e) {
            System.out.println("Parse error occurred. " + e.getMessage());
        }
        catch(NullPointerException e) {
            System.out.println("No parser exists.");
        }
    }
}
