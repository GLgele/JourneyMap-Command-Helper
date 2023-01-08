package modinfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;
import journeymap.client.io.FileHandler;
import org.apache.logging.log4j.Level;

/* loaded from: journeymap-1.12.2-5.5.4.jar:modinfo/Config.class */
public class Config implements Serializable {
    private static final String[] HEADERS = {"// ModInfo v%s - Configuration file for %s", "// ModInfo is a simple utility which helps the Mod developer support their mod.", "// For more information: https://github.com/MCModInfo/modinfo/blob/master/README.md"};
    private static final String PARENT_DIR = "config";
    private static final String FILE_PATTERN = "%s_ModInfo.cfg";
    private static final String ENABLED_STATUS_PATTERN = "Enabled (%s)";
    private static final String DISABLED_STATUS_PATTERN = "Disabled (%s)";
    private String modId;
    private Boolean enable;
    private String salt;
    private String status;
    private Boolean verbose;

    private Config() {
    }

    public static synchronized Config getInstance(String modId) {
        Config config = null;
        File configFile = getFile(modId);
        if (configFile.exists()) {
            try {
                Gson gson = new Gson();
                config = (Config) gson.fromJson(new FileReader(configFile), Config.class);
            } catch (Exception e) {
                ModInfo.LOGGER.log(Level.ERROR, "Can't read file " + configFile, e.getMessage());
                if (configFile.exists()) {
                    configFile.delete();
                }
            }
        }
        if (config == null) {
            config = new Config();
        }
        config.validate(modId);
        return config;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isConfirmedDisabled(Config config) {
        return !config.enable.booleanValue() && generateStatusString(config).equals(config.status);
    }

    static String generateStatusString(Config config) {
        return generateStatusString(config.modId, config.enable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String generateStatusString(String modId, Boolean enable) {
        UUID uuid = ModInfo.createUUID(modId, enable.toString());
        String pattern = enable.booleanValue() ? ENABLED_STATUS_PATTERN : DISABLED_STATUS_PATTERN;
        return String.format(pattern, uuid.toString());
    }

    private static File getFile(String modId) {
        File dir = new File(FileHandler.getMinecraftDirectory(), PARENT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, String.format(FILE_PATTERN, modId.replaceAll("%", "_")));
    }

    private void validate(String modId) {
        boolean dirty = false;
        if (!modId.equals(this.modId)) {
            this.modId = modId;
            dirty = true;
        }
        if (this.enable == null) {
            this.enable = Boolean.TRUE;
            dirty = true;
        }
        if (this.salt == null) {
            this.salt = Long.toHexString(System.currentTimeMillis());
            dirty = true;
        }
        if (this.verbose == null) {
            this.verbose = Boolean.FALSE;
            dirty = true;
        }
        if (dirty) {
            save();
        }
    }

    public void save() {
        String[] strArr;
        File configFile = getFile(this.modId);
        try {
            String lineEnding = System.getProperty("line.separator");
            StringBuilder sb = new StringBuilder();
            for (String line : HEADERS) {
                sb.append(line).append(lineEnding);
            }
            String header = String.format(sb.toString(), ModInfo.VERSION, this.modId);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(this);
            FileWriter fw = new FileWriter(configFile);
            fw.write(header);
            fw.write(json);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            ModInfo.LOGGER.log(Level.ERROR, "Can't save file " + configFile, e);
        }
    }

    public String getSalt() {
        return this.salt;
    }

    public String getModId() {
        return this.modId;
    }

    public Boolean isEnabled() {
        return this.enable;
    }

    public Boolean isVerbose() {
        return this.verbose;
    }

    public String getStatus() {
        return this.status;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void disable() {
        this.enable = false;
        confirmStatus();
    }

    public void confirmStatus() {
        String newStatus = generateStatusString(this);
        if (!newStatus.equals(this.status)) {
            this.status = newStatus;
            save();
        }
    }
}