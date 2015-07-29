import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.net.ftp.FTPClient;

/**
 * Created by aaron on 7/18/15.
 */
public class FTPDispatcher {
    FTPCommands commands = new FTPCommands();

    /**
     * Default constructor
     */
    public FTPDispatcher() {}

    public void dispatch(String[] args, FTPClient ftp) {

        CommandLine line = ArgParser.parse(args);

        if (args.length <= 0 || line == null || line.hasOption('h') || line.hasOption("help")) {
            // determine whether or not to print usage/help

            HelpFormatter help = new HelpFormatter();
            help.printHelp("FTPClient", ArgParser.options());
        } else {
            // otherwise handle all other options

            if (line.hasOption('C') || line.hasOption("connect")) {
                // handle server connection option

                commands.connect(ftp, line.getOptionValue('C'));
            }

            if (line.hasOption("l") || line.hasOption("list")) {
                // handle option to list files

                commands.listFilesFolders(ftp);
            }

            if (line.hasOption('g') || line.hasOption("get")) {
 
                commands.getRemoteFile(ftp, line.getOptionValues('g'));
            }

            if (line.hasOption('p') || line.hasOption("put")) {
                // handle putting a file on remote
                commands.putRemoteFile(ftp,line.getOptionValues('p'));
            }

            commands.exit(ftp);
        }
    }
}
