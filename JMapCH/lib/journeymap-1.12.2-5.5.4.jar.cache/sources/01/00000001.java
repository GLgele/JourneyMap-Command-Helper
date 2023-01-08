package modinfo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import journeymap.common.Journeymap;
import modinfo.mp.v1.Client;
import modinfo.mp.v1.Message;
import modinfo.mp.v1.Payload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.Locale;
import net.minecraft.server.integrated.IntegratedServer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/* loaded from: journeymap-1.12.2-5.5.4.jar:modinfo/ModInfo.class */
public class ModInfo {
    public static final String VERSION = "0.2";
    public static final Logger LOGGER = LogManager.getLogger("modinfo");
    private final Minecraft minecraft = Minecraft.func_71410_x();
    private final String trackingId;
    private final String modId;
    private final String modName;
    private final String modVersion;
    private Locale reportingLocale;
    private Config config;
    private Client client;

    public ModInfo(String trackingId, String reportingLanguageCode, String modId, String modName, String modVersion, boolean singleUse) {
        this.trackingId = trackingId;
        this.modId = modId;
        this.modName = modName;
        this.modVersion = modVersion;
        try {
            this.reportingLocale = getLocale(reportingLanguageCode);
            this.config = Config.getInstance(this.modId);
            this.client = createClient();
            if (singleUse) {
                singleUse();
            } else if (this.config.isEnabled().booleanValue()) {
                if (Config.generateStatusString(modId, false).equals(this.config.getStatus())) {
                    optIn();
                } else {
                    this.config.confirmStatus();
                }
            } else {
                optOut();
            }
        } catch (Throwable t) {
            LOGGER.log(Level.ERROR, "Unable to configure ModInfo", t);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static UUID createUUID(String... parts) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            for (String part : parts) {
                md.update(part.getBytes());
            }
            byte[] md5Bytes = md.digest();
            md5Bytes[6] = (byte) (md5Bytes[6] & 15);
            md5Bytes[6] = (byte) (md5Bytes[6] | 48);
            md5Bytes[8] = (byte) (md5Bytes[8] & 63);
            md5Bytes[8] = (byte) (md5Bytes[8] | 128);
            long msb = 0;
            long lsb = 0;
            for (int i = 0; i < 8; i++) {
                msb = (msb << 8) | (md5Bytes[i] & 255);
            }
            for (int i2 = 8; i2 < 16; i2++) {
                lsb = (lsb << 8) | (md5Bytes[i2] & 255);
            }
            return new UUID(msb, lsb);
        } catch (NoSuchAlgorithmException e) {
            throw new InternalError("MD5 not supported");
        }
    }

    public final boolean isEnabled() {
        return this.client != null;
    }

    public void reportAppView() {
        try {
            if (isEnabled()) {
                Payload payload = new Payload(Payload.Type.AppView);
                payload.add(appViewParams());
                payload.add(minecraftParams());
                this.client.send(payload);
            }
        } catch (Throwable t) {
            LOGGER.log(Level.ERROR, t.getMessage(), t);
        }
    }

    public void reportException(Throwable e) {
        try {
            if (isEnabled()) {
                String category = "Exception: " + e.toString();
                int actionMaxBytes = Payload.Parameter.EventAction.getMaxBytes();
                int labelMaxBytes = Payload.Parameter.EventLabel.getMaxBytes();
                int maxBytes = actionMaxBytes + labelMaxBytes;
                StackTraceElement[] stackTrace = e.getStackTrace();
                ArrayList<Integer> byteLengths = new ArrayList<>(stackTrace.length);
                int total = 0;
                for (int i = 0; i < stackTrace.length; i++) {
                    int byteLength = Payload.encode(stackTrace[i].toString() + " / ").getBytes().length;
                    if (total + byteLength > maxBytes) {
                        break;
                    }
                    total += byteLength;
                    byteLengths.add(i, Integer.valueOf(byteLength));
                }
                int index = 0;
                StringBuilder action = new StringBuilder(actionMaxBytes / 11);
                int actionTotal = 0;
                while (index < byteLengths.size()) {
                    int byteLength2 = byteLengths.get(index).intValue();
                    if (actionTotal + byteLength2 > actionMaxBytes) {
                        break;
                    }
                    actionTotal += byteLength2;
                    action.append(stackTrace[index].toString() + " / ");
                    index++;
                }
                StringBuilder label = new StringBuilder(labelMaxBytes / 11);
                int labelTotal = 0;
                while (index < byteLengths.size()) {
                    int byteLength3 = byteLengths.get(index).intValue();
                    if (labelTotal + byteLength3 > labelMaxBytes) {
                        break;
                    }
                    labelTotal += byteLength3;
                    label.append(stackTrace[index].toString() + " / ");
                    index++;
                }
                reportEvent(category, action.toString(), label.toString());
            }
        } catch (Throwable t) {
            LOGGER.log(Level.ERROR, t.getMessage(), t);
        }
    }

    public void reportEvent(String category, String action, String label) {
        try {
            if (isEnabled()) {
                Payload payload = new Payload(Payload.Type.Event);
                payload.add(appViewParams());
                payload.put(Payload.Parameter.EventCategory, category);
                payload.put(Payload.Parameter.EventAction, action);
                payload.put(Payload.Parameter.EventLabel, label);
                this.client.send(payload);
            }
        } catch (Throwable t) {
            LOGGER.log(Level.ERROR, t.getMessage(), t);
        }
    }

    public void keepAlive() {
        try {
            if (isEnabled()) {
                Payload payload = new Payload(Payload.Type.Event);
                payload.put(Payload.Parameter.EventCategory, "ModInfo");
                payload.put(Payload.Parameter.EventAction, "KeepAlive");
                payload.put(Payload.Parameter.NonInteractionHit, Payload.VERSION);
                this.client.send(payload);
            }
        } catch (Throwable t) {
            LOGGER.log(Level.ERROR, t.getMessage(), t);
        }
    }

    private Locale getLocale(String languageCode) {
        List<String> langs = Arrays.asList("en_US");
        if (!"en_US".equals(languageCode)) {
            langs.add(languageCode);
        }
        Locale locale = new Locale();
        locale.func_135022_a(this.minecraft.func_110442_L(), langs);
        return locale;
    }

    private String I18n(String translationKey, Object... parms) {
        return this.reportingLocale.func_135023_a(translationKey, parms);
    }

    private Client createClient() {
        String salt = this.config.getSalt();
        String username = this.minecraft.func_110432_I().func_111285_a();
        UUID clientId = createUUID(salt, username, this.modId);
        return new Client(this.trackingId, clientId, this.config, Minecraft.func_71410_x().func_135016_M().func_135041_c().func_135034_a());
    }

    private Map<Payload.Parameter, String> minecraftParams() {
        Map<Payload.Parameter, String> map = new HashMap<>();
        Language language = this.minecraft.func_135016_M().func_135041_c();
        map.put(Payload.Parameter.UserLanguage, language.func_135034_a());
        DisplayMode displayMode = Display.getDesktopDisplayMode();
        map.put(Payload.Parameter.ScreenResolution, displayMode.getWidth() + "x" + displayMode.getHeight());
        StringBuilder desc = new StringBuilder(Journeymap.MC_VERSION);
        if (this.minecraft.field_71441_e != null) {
            IntegratedServer server = this.minecraft.func_71401_C();
            boolean multiplayer = server == null || server.func_71344_c();
            desc.append(", ").append(multiplayer ? I18n("menu.multiplayer", new Object[0]) : I18n("menu.singleplayer", new Object[0]));
        }
        map.put(Payload.Parameter.ContentDescription, desc.toString());
        return map;
    }

    private Map<Payload.Parameter, String> appViewParams() {
        Map<Payload.Parameter, String> map = new HashMap<>();
        map.put(Payload.Parameter.ApplicationName, this.modName);
        map.put(Payload.Parameter.ApplicationVersion, this.modVersion);
        return map;
    }

    private void optIn() {
        Payload payload = new Payload(Payload.Type.Event);
        payload.put(Payload.Parameter.EventCategory, "ModInfo");
        payload.put(Payload.Parameter.EventAction, "Opt In");
        createClient().send(payload, new Message.Callback() { // from class: modinfo.ModInfo.1
            @Override // modinfo.mp.v1.Message.Callback
            public void onResult(Object result) {
                if (Boolean.TRUE.equals(result) && ModInfo.this.config.isEnabled().booleanValue()) {
                    ModInfo.this.config.confirmStatus();
                    ModInfo.LOGGER.info("ModInfo for " + ModInfo.this.config.getModId() + " has been re-enabled. Thank you!");
                }
            }
        });
    }

    public void singleUse() {
        if (Config.isConfirmedDisabled(this.config)) {
            return;
        }
        reportAppView();
        this.config.disable();
    }

    private void optOut() {
        if (Config.isConfirmedDisabled(this.config)) {
            LOGGER.info("ModInfo for " + this.modId + " is disabled");
        } else if (!this.config.isEnabled().booleanValue()) {
            Payload payload = new Payload(Payload.Type.Event);
            payload.put(Payload.Parameter.EventCategory, "ModInfo");
            payload.put(Payload.Parameter.EventAction, "Opt Out");
            createClient().send(payload, new Message.Callback() { // from class: modinfo.ModInfo.2
                @Override // modinfo.mp.v1.Message.Callback
                public void onResult(Object result) {
                    if (Boolean.TRUE.equals(result) && !ModInfo.this.config.isEnabled().booleanValue()) {
                        ModInfo.this.config.confirmStatus();
                        ModInfo.LOGGER.info("ModInfo for " + ModInfo.this.config.getModId() + " has been disabled");
                    }
                }
            });
        }
    }
}