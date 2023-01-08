package com.dreambx.jmapch.common;

import com.dreambx.jmapch.common.command.CmdBase;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class Registery {
    public static void initRegistries() {}

    public static void commandRegistries()
    {
        ClientCommandHandler.instance.registerCommand(new CmdBase());
    }
}
