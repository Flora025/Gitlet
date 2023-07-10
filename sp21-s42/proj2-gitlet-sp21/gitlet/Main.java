package gitlet;

import java.io.IOException;

import static gitlet.Utils.*;
import static gitlet.Repository.*;

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
                    init();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            /* Usage: java gitlet.Main add [file name] */
            case "add":
                if (args.length < 2) {
                    System.exit(0);
                }
                add(args[1]);
                break;
            /* Usage: java gitlet.Main commit [message] */
            case "commit":
                if (args.length < 2) {
                    message("Please enter a commit message");
                    System.exit(0);
                }
                commit(args[1]);
                break;
            /* Usage: java gitlet.Main rm [file name] */
            case "rm":
                if (args.length < 2) {
                    System.exit(0);
                }
                rm(args[1]);
                break;
            /* Usage: java gitlet.Main log */
            case "log":
                log();
                break;
            /* Usage: java gitlet.Main log */
            case "global-log":
                globalLog();
                break;
            /* Usage: java gitlet.Main find [commit message] */
            case "find":
                if (args.length < 2) {
                    System.exit(0);
                }
                find(args[1]);
                break;
            /* Usage: java gitlet.Main status */
            case "status":
                status();
                break;
            /* Usage:
             * 1. java gitlet.Main checkout -- [file name]
             * 2. java gitlet.Main checkout [commit id] -- [file name]
             * 3. java gitlet.Main checkout [branch name] */
            case "checkout":
                int n = args.length;
                if (n < 2) {
                    System.exit(0);
                }
                if (n == 2) {
                    try {
                        checkoutBranch(args[1]); // args[0] == "checkout"
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (n == 3) {
                    checkoutHeadFile(args[2]);
                } else if (n == 4) {
                    checkoutSpecifiedFile(args[1], args[3]);
                }
                break;
            case "branch":
                break;
            case "rm-branch":
                break;
            case "reset":
                break;
            case "merge":
                break;
        }
    }
}
