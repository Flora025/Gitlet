package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author flora
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Utils.message("Usage: java gitlet.Main ARGS, where ARGS contains" +
                    "\n<COMMAND> <OPERAND1> <OPERAND2> ... ");
            System.exit(0);
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                try {
                    Repository.init();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "add":
                if (args.length < 2) {
                    System.exit(0);
                }
                Repository.add(args[1]);
                break;
            case "commit":
                // TODO: `commit [message]` command
                break;
        }
    }
}
