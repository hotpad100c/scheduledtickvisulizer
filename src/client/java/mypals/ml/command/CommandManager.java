package mypals.ml.command;

import mypals.ml.config.ScheduledTickVisualizerConfig;

import java.lang.reflect.Field;

import static mypals.ml.ScheduledTickVisulizerClient.UpadteSettings;

public class CommandManager {
    public static void setStaticBooleanField(String fieldName, boolean flag) {
        try {
            Class<?> clazz = ScheduledTickVisualizerConfig.class;
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, flag);
            ScheduledTickVisualizerConfig.CONFIG_HANDLER.save();
            UpadteSettings();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Failed to update field '" + fieldName + "': " + e.getMessage());
        }
    }
    public static void setStaticFloatField(String fieldName, float value) {
        try {
            Class<?> clazz = ScheduledTickVisualizerConfig.class;
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, value);
            ScheduledTickVisualizerConfig.CONFIG_HANDLER.save();
            UpadteSettings();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Failed to update field '" + fieldName + "': " + e.getMessage());
        }
    }
    public static void setStaticIntField(String fieldName, int value) {
        try {
            Class<?> clazz = ScheduledTickVisualizerConfig.class;
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, value);
            ScheduledTickVisualizerConfig.CONFIG_HANDLER.save();
            UpadteSettings();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Failed to update field '" + fieldName + "': " + e.getMessage());
        }
    }
}
