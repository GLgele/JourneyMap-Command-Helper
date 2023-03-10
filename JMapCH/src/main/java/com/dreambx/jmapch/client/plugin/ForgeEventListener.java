package com.dreambx.jmapch.client.plugin;

import com.dreambx.jmapch.JmapCH;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.DisplayType;
import journeymap.client.api.display.PolygonOverlay;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;

/**
 * Listens to Forge events, creates bed waypoints and slime chunk overlays via the ClientAPI.
 */
class ForgeEventListener
{
    IClientAPI jmAPI;
    HashMap<ChunkPos, PolygonOverlay> slimeChunkOverlays;

    /**
     * Constructor.
     *
     * @param jmAPI API implementation
     */
    ForgeEventListener(IClientAPI jmAPI)
    {
        this.jmAPI = jmAPI;
        this.slimeChunkOverlays = new HashMap<ChunkPos, PolygonOverlay>();
    }

    /**
     * Listen for Forge PlayerSleepInBedEvents, create a waypoint for the bed.
     * This is just a quick example, and doesn't take into account whether the player successfully slept.
     */
    @SubscribeEvent
    public void onPlayerSlept(PlayerSleepInBedEvent event)
    {
        try
        {
            if (event.getEntity().getEntityWorld().isRemote)
            {
                if (jmAPI.playerAccepts(JmapCH.MODID, DisplayType.Waypoint))
                {
                    SampleWaypointFactory.createBedWaypoint(jmAPI, event.getPos(), event.getEntity().dimension);
                }
            }
        }
        catch (Throwable t)
        {
            JmapCH.LOGGER.error(t.getMessage(), t);
        }
    }

    /**
     * Listen for Forge chunk load, show polygon overlay if it is a slime chunk.
     */
    @SubscribeEvent
    public void onChunkLoadEvent(ChunkEvent.Load event)
    {
        try
        {
            if (event.getWorld().isRemote)
            {
                if (jmAPI.playerAccepts(JmapCH.MODID, DisplayType.Polygon))
                {
                    Chunk chunk = event.getChunk();
                    if (isSlimeChunk(chunk))
                    {
                        ChunkPos chunkCoords = chunk.getPos();
                        if (!slimeChunkOverlays.containsKey(chunkCoords))
                        {
                            int dimension = event.getWorld().provider.getDimension();
                            PolygonOverlay overlay = SamplePolygonOverlayFactory.create(chunkCoords, dimension);
                            slimeChunkOverlays.put(chunkCoords, overlay);
                            jmAPI.show(overlay);
                        }
                    }
                }
            }
        }
        catch (Throwable t)
        {
            JmapCH.LOGGER.error(t.getMessage(), t);
        }
    }

    /**
     * Listen for Forge chunk unload, remove polygon overlay if it is a slime chunk.
     */
    @SubscribeEvent
    public void onChunkUnloadEvent(ChunkEvent.Unload event)
    {
        if (event.getWorld().isRemote)
        {
            if (jmAPI.playerAccepts(JmapCH.MODID, DisplayType.Polygon))
            {
                ChunkPos chunkCoords = event.getChunk().getPos();
                if (!slimeChunkOverlays.containsKey(chunkCoords))
                {
                    PolygonOverlay overlay = slimeChunkOverlays.remove(chunkCoords);
                    if (overlay != null)
                    {
                        jmAPI.remove(overlay);
                    }
                }
            }
        }
    }

    /**
     * Magic formula for slime chunk discovery.
     *
     * @param chunk the chunk
     * @return true if it's a slime chunk
     */
    private boolean isSlimeChunk(Chunk chunk)
    {
        return chunk.getRandomWithSeed(987234911L).nextInt(10) == 0;
    }
}
