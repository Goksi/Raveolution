package tech.goksi.raveolution.utils;

import tech.goksi.raveolution.Bot;

import java.util.List;
import java.util.Objects;

public class ConfigUtils {

    public static String getString(String path){
        return Bot.getInstance().getConf().getValues().getString(path);
    }

    public static String getString(String path, String regex, String replacement){
        return Objects.requireNonNull(Bot.getInstance().getConf().getValues().getString(path)).replaceAll(regex, replacement);
    }

    public static boolean getBoolean(String path){
        return Bot.getInstance().getConf().getValues().getBoolean(path);
    }

    public static void set(String path, Object value){
        Bot.getInstance().getConf().getValues().set(path, value);
    }

    public static List<String> getStringList(String path){
        return Bot.getInstance().getConf().getValues().getStringList(path);
    }

}
