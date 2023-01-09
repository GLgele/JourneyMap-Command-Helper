package com.dreambx.jmapch.client.plugin;

import com.dreambx.jmapch.JmapCH;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.DisplayType;
import journeymap.client.api.event.ClientEvent;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;

import static journeymap.client.api.event.ClientEvent.Type.*;

@ParametersAreNonnullByDefault
@journeymap.client.api.ClientPlugin
public class CmdJMapPlugin implements IClientPlugin {
    private IClientAPI jmAPI = null;
    private ForgeEventListener forgeEventListener;

    @Override
    public void initialize(final IClientAPI jmAPI)
    {
        // Set ClientProxy.SampleWaypointFactory with an implementation that uses the JourneyMap IClientAPI under the covers.
        this.jmAPI = jmAPI;

        // Register listener for forge events
        forgeEventListener = new ForgeEventListener(jmAPI);
        MinecraftForge.EVENT_BUS.register(forgeEventListener);

        // Subscribe to desired ClientEvent types from JourneyMap
        this.jmAPI.subscribe(getModId(), EnumSet.of(DEATH_WAYPOINT, MAPPING_STARTED, MAPPING_STOPPED));

        JmapCH.LOGGER.info("Initialized " + getClass().getName());
    }
    @Override
    public String getModId()
    {
        return JmapCH.MODID;
    }
    @Override
    public void onEvent(ClientEvent event)
    {
        try
        {
            switch (event.type)
            {
                case MAPPING_STARTED:
                    onMappingStarted(event);
                    break;

                case MAPPING_STOPPED:
                    onMappingStopped(event);
                    break;

                /*case DEATH_WAYPOINT:
                    onDeathpoint((DeathWaypointEvent) event);
                    break;*/
            }
        }
        catch (Throwable t)
        {
            JmapCH.LOGGER.error(t.getMessage(), t);
        }
    }
    void onMappingStarted(ClientEvent event)
    {
        if (jmAPI.playerAccepts(JmapCH.MODID, DisplayType.Waypoint))
        {
            //SampleWaypointFactory.removeBedWaypoint(jmAPI);
        }
    }
    void onMappingStopped(ClientEvent event)
    {
        // Clear everything
        jmAPI.removeAll(JmapCH.MODID);
    }
    public IClientAPI getJmAPI()
    {
        return jmAPI;
    }
}
