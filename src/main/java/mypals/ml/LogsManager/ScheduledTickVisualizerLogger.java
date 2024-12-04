package mypals.ml.LogsManager;

import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ScheduledTickVisualizerLogger {
    public static void writeLogFile(String fileName, String content) {
        try {
            File gameDir = FabricLoader.getInstance().getGameDir().toFile();

            File logDir = new File(gameDir, "scheduledtickvisualizerLogs");
            if (!logDir.exists()) {
                if (logDir.mkdirs()) {
                    System.out.println("Created directory: " + logDir.getAbsolutePath());
                }
            }

            File file = new File(logDir, fileName);

            if (!file.exists()) {
                if (file.createNewFile()) {
                    System.out.println("Created file: " + file.getAbsolutePath());
                }
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
            writer.write(content);
            writer.newLine();
            writer.close();

            System.out.println("File written successfully: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
