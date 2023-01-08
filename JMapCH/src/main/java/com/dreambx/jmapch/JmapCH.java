package com.dreambx.jmapch;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = JmapCH.MODID, name = JmapCH.NAME, version = JmapCH.VERSION, dependencies = JmapCH.DEPENDENCIES)
public class JmapCH
{
    public static final String MODID = "jmapch";
    public static final String NAME = "JourneyMap Command Helper";
    public static final String VERSION = "1.0";
    public static final String DEPENDENCIES = "after:journeymap";

    public static Logger LOGGER;

    @Mod.Instance(JmapCH.MODID)
    public static JmapCH instance;

    @SidedProxy(clientSide = "com.dreambx.jmapch.client.ClientProxy", serverSide = "com.dreambx.jmapch.server.ServerProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = LogManager.getFormatterLogger(MODID);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        //logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
        LOGGER.info("JourneyMap Command Helper init.");
    }
}
