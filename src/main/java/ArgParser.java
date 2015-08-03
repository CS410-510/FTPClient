import org.apache.commons.cli.*;

/**
 * Created by Aaron Nash on 7/13/15.
 */
public class ArgParser {
    private static CommandLineParser parser = null;
    private static Options options = null;
    private static OptionGroup opsGrp = null;

    public static void initParser() {
        parser = new DefaultParser();
        options = new Options();
        opsGrp = new OptionGroup();

        Option connect = Option.builder("C").longOpt("connect").required(false).hasArg().optionalArg(false)
                .argName("SERVER")
                .desc("Begin or switch the connection context.").build();
        Option delete = Option.builder("d").longOpt("delete").required(false).hasArg().optionalArg(false)
                .argName("PATH")
                .desc("Delete a file, use with -R for a directory.").build();
        Option get = Option.builder("g").longOpt("get").required(false).hasArg().optionalArg(false)
                .argName("PATHS")
                .desc("Get file(s).").build();
        Option list = Option.builder("l").longOpt("list").required(false).hasArg().optionalArg(false)
                .argName("PATH")
                .desc("List files and directories.").build();
        Option dir = Option.builder("i").longOpt("dir").required(false).hasArg().optionalArg(false)
                .argName("PATH")
                .desc("Create a directory.").build();
        Option copy = Option.builder("c").longOpt("copy").required(false).numberOfArgs(2).optionalArg(false)
                .argName("PATHS")
                .desc("Copy a directory on the server.").build();
        // Using hasArgs instead of hasArg to specify option can have unlimited args.
        Option put = Option.builder("p").longOpt("put").required(false).hasArgs().optionalArg(false)
                .argName("PATHS")
                .desc("Put file(s).").build();

        opsGrp.setRequired(false);
        opsGrp.addOption(delete);
        opsGrp.addOption(get);
        opsGrp.addOption(list);
        opsGrp.addOption(put);
        opsGrp.addOption(dir);
        opsGrp.addOption(copy);

        options.addOptionGroup(opsGrp);
        options.addOption(connect);
        options.addOption("h", "help", false, "Print this help information.");
        options.addOption("N", "disconnect", false, "Disconnect from the current connection context.");
        options.addOption("L", "local", false, "Source is the local machine.");
        options.addOption("R", "recursive", false, "Where allowed, target is a directory.");

    }

    public static CommandLine parse(String[] args) {
        CommandLine line = null;

        try {
            line = parser.parse(options, args);
        }
        catch(ParseException e) {
            System.out.println("Parse error occurred. " + e.getMessage());
        }
        catch(NullPointerException e) {
            System.out.println("No parser exists.");
        }

        return line;
    }

    public static Options options() {
        return options;
    }
}
