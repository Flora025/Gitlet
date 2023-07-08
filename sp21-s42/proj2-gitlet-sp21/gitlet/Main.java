package gitlet;

import java.io.IOException;

import static gitlet.Utils.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author flora
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            message("Usage: java gitlet.Main ARGS, where ARGS contains" +
                    "\n<COMMAND> <OPERAND1> <OPERAND2> ... ");
            System.exit(0);
        }

        String firstArg = args[0];
        switch(firstArg) {
            /* Usage: java gitlet.Main init */
            case "init":
                try {
                    Repository.init();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            /* Usage: java gitlet.Main add [file name] */
            case "add":
                if (args.length < 2) {
                    System.exit(0);
                }
                Repository.add(args[1]);
                break;
            /* Usage: java gitlet.Main commit [message] */
            case "commit":
                if (args.length < 2) {
                    message("Please enter a commit message");
                    System.exit(0);
                }
                Repository.commit(args[1]);
                break;
            /* Usage: java gitlet.Main rm [file name] */
            case "rm":
                if (args.length < 2) {
                    System.exit(0);
                }
                Repository.rm(args[1]);
                break;
            /* Usage: java gitlet.Main log */
            case "log":
                Repository.log();
                break;
            /* Usage: java gitlet.Main log */
            case "global-log":
                Repository.globalLog();
                break;
            /* Usage: java gitlet.Main find [commit message] */
            case "find":
                if (args.length < 2) {
                    System.exit(0);
                }
                Repository.find(args[1]);
                break;
            /* Usage: java gitlet.Main status */
            case "status":
                // TODO
                break;
            /* Usage:
             * 1. java gitlet.Main checkout -- [file name]
             * 2. java gitlet.Main checkout [commit id] -- [file name]
             * 3. java gitlet.Main checkout [branch name] */
            case "checkout":
                // TODO
                break;
        }
    }
}
