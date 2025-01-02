package mypals.ml.LogsManager;

import org.apache.commons.logging.Log;

public class LogManager {
    public String fileName = "unnamed";
    public int ticks = 10;


    public LogManager(String fileName, int ticks) {
        this.fileName = fileName + ".txt";
        this.ticks = ticks;
    }


}
