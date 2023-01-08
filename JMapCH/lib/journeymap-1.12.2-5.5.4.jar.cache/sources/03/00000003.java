package journeymap.common;

import java.util.Map;
import journeymap.client.JourneymapClient;
import journeymap.common.command.CommandJTP;
import journeymap.common.version.Version;
import journeymap.server.JourneymapServer;
import journeymap.server.properties.PropertiesManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "journeymap", name = Journeymap.SHORT_MOD_NAME, version = "1.12.2-5.5.4", canBeDeactivated = true, guiFactory = "journeymap.client.ui.dialog.OptionsGuiFactory", dependencies = "required-after:Forge@[${14.23.5.2768},)", acceptedMinecraftVersions = "[1.12.2]")
/* loaded from: journeymap-1.12.2-5.5.4.jar:journeymap/common/Journeymap.class */
public class Journeymap {
    public static final String MOD_ID = "journeymap";
    public static final String SHORT_MOD_NAME = "JourneyMap";
    public static final Version JM_VERSION = Version.from("5", "5", "4", "", new Version(5, 5, 0, "dev"));
    public static final String FORGE_VERSION = "14.23.5.2768";
    public static final String MC_VERSION = "1.12.2";
    public static final String WEBSITE_URL = "http://journeymap.info/";
    public static final String PATREON_URL = "http://patreon.com/techbrew";
    public static final String DOWNLOAD_URL = "http://minecraft.curseforge.com/projects/journeymap/files/";
    public static final String VERSION_URL = "https://api.cfwidget.com/minecraft/mc-mods/journeymap";
    @Mod.Instance("journeymap")
    public static Journeymap instance;
    @SidedProxy(clientSide = "journeymap.client.JourneymapClient", serverSide = "journeymap.server.JourneymapServer")
    public static CommonProxy proxy;

    public static Logger getLogger() {
        return LogManager.getLogger("journeymap");
    }

    @NetworkCheckHandler
    public boolean checkModLists(Map<String, String> modList, Side side) {
        if (proxy == null) {
            return true;
        }
        return proxy.checkModLists(modList, side);
    }

    @Mod.EventHandler
    public void preInitialize(FMLPreInitializationEvent event) throws Throwable {
        proxy.preInitialize(event);
    }

    @Mod.EventHandler
    public void initialize(FMLInitializationEvent event) throws Throwable {
        proxy.initialize(event);
    }

    @Mod.EventHandler
    public void postInitialize(FMLPostInitializationEvent event) throws Throwable {
        proxy.postInitialize(event);
    }

    @Mod.EventHandler
    public void serverStartingEvent(FMLServerStartingEvent event) {
        PropertiesManager.getInstance();
        event.registerServerCommand(new CommandJTP());
    }

    @SideOnly(Side.SERVER)
    @Mod.EventHandler
    public void serverStartedEvent(FMLServerStartedEvent event) {
    }

    @SideOnly(Side.CLIENT)
    public static JourneymapClient getClient() {
        return (JourneymapClient) proxy;
    }

    @SideOnly(Side.SERVER)
    public static JourneymapServer getServer() {
        return (JourneymapServer) proxy;
    }
}