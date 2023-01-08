package journeymap.common;

import java.util.Map;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

/* loaded from: journeymap-1.12.2-5.5.4.jar:journeymap/common/CommonProxy.class */
public interface CommonProxy {
    void preInitialize(FMLPreInitializationEvent fMLPreInitializationEvent) throws Throwable;

    void initialize(FMLInitializationEvent fMLInitializationEvent) throws Throwable;

    void postInitialize(FMLPostInitializationEvent fMLPostInitializationEvent) throws Throwable;

    boolean checkModLists(Map<String, String> map, Side side);

    boolean isUpdateCheckEnabled();

    void handleWorldIdMessage(String str, EntityPlayerMP entityPlayerMP);
}