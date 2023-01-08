package com.dreambx.jmapch;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = JmapCH.MODID, name = JmapCH.NAME, version = JmapCH.VERSION, dependencies = JmapCH.DEPENDENCIES)
public class JmapCH
{
    public static final String MODID = "jmapch";
    public static final String NAME = "JourneyMap Command Helper";
    public static final String VERSION = "1.0";
    public static final String DEPENDENCIES = "after:journeymap";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // some example code
        logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }
}
