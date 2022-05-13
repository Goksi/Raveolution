package tech.goksi.raveolution.config;


import org.simpleyaml.configuration.file.YamlFile;

import java.io.*;
import java.nio.file.Files;


public class Config {
    private YamlFile config = null;
    private File configFile;
    public Config(){
        configFile = new File("config.yml");
    }

    public void reloadConfig() throws IOException {
        configFile = new File("config.yml");
        config = new YamlFile(configFile);
        config.loadWithComments();
    }



    public void initConfig(){
        if(!configFile.exists()){
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("config.yml");
            assert is!=null;
            try{
                try (OutputStream out = Files.newOutputStream(configFile.toPath())) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = is.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }
        config = new YamlFile(configFile);
        try{
            config.loadWithComments();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public YamlFile getValues(){
        return config;
    }



    public void saveConfig(){
        if(config == null || configFile == null) return;
        try{
            getValues().save(configFile);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
