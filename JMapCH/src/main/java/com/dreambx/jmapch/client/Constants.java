package com.dreambx.jmapch.client;

import com.dreambx.jmapch.JmapCH;
import com.google.common.base.Joiner;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.FMLClientHandler;

public class Constants {
    public static String getString(String key) {
        if (FMLClientHandler.instance().getClient() == null) {
            return key;
        }
        try {
            String result = I18n.format(key, new Object[0]);
            if (result.equals(key)) {
                JmapCH.LOGGER.warn("Message key not found: " + key);
            }
            return result;
        } catch (Throwable t) {
            JmapCH.LOGGER.warn(String.format("Message key '%s' threw exception: %s", key, t.getMessage()));
            return key;
        }
    }

    public static String getString(String key, Object... params) {
        if (FMLClientHandler.instance().getClient() == null) {
            return String.format("%s (%s)", key, Joiner.on(",").join(params));
        }
        try {
            String result = I18n.format(key, params);
            if (result.equals(key)) {
                JmapCH.LOGGER.warn("Message key not found: " + key);
            }
            return result;
        } catch (Throwable t) {
            JmapCH.LOGGER.warn(String.format("Message key '%s' threw exception: %s", key, t.getMessage()));
            return key;
        }
    }

}
