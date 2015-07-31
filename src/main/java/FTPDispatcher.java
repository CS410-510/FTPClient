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
                if (line.hasOption("L") || line.hasOption("local")) {
                    // handle option to list local files
                    commands.listLocalWorkingDir();
                } else {
                    // handle option to list remote files
                    commands.listRemoteWorkingDir(ftp);
                }
            }

            if (line.hasOption('g') || line.hasOption("get")) {
                // handle getting a file on remote
                commands.getRemoteFile(ftp, line.getOptionValue('g'));
            }

            if (line.hasOption('p') || line.hasOption("put")) {
                // handle putting a file on remote
                commands.putRemoteFile(ftp,line.getOptionValues('p'));
            }

            if (line.hasOption('i') || line.hasOption("dir")) {
                // create directory on the ftp server
                commands.createRemoteDirectory(ftp, line.getOptionValue('i'));
            }

            if (line.hasOption('d') || line.hasOption("delete")) {
                if (line.hasOption('R') || line.hasOption("recursive")) {
                    // TODO: handle delete remote directory
                } else {
                    // TODO: handle delete remote file
                }
            }

            commands.exit(ftp);
        }
    }
}
