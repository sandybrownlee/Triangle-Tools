package triangle.optimiser;

public class SummaryStatistics {
    private static int binaryExpressionStats = 0;   //hold the number of the binary expressions
    private static int ifCommandsStat = 0;   //hold the number of the IfCommands
    private static int whileCommandStat = 0;    //hold the number of the whileCommands

    //Increase the number of the binary expressions
    public static void increaseBinaryExpressionStat(){
        binaryExpressionStats++;
    }

    //Get the number of the binary expressions
    public static int getBinaryExpressionStats(){
        return binaryExpressionStats;
    }

    //Increase the number of the if commands
    public static void increaseIfCommandStat(){
        ifCommandsStat++;
    }

    //Get the number of the if commands
    public static int getIfCommandsStat(){
        return ifCommandsStat;
    }

    //Increase the number of the while commands
    public static void increaseWhileCommandsStats(){
        whileCommandStat++;
    }

    //Get the number of the while commands
    public static int getWhileCommandStat(){
        return whileCommandStat;
    }
}
