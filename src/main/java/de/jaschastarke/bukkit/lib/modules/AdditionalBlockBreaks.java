package de.jaschastarke.bukkit.lib.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.material.Attachable;

import de.jaschastarke.bukkit.lib.Core;
import de.jaschastarke.bukkit.lib.SimpleModule;
import de.jaschastarke.bukkit.lib.events.AttachedBlockDestroyedByPlayerEvent;
import de.jaschastarke.bukkit.lib.events.AttachedBlockDestroyedEvent;
import de.jaschastarke.bukkit.lib.events.WaterDestroyedBlockEvent;

public class AdditionalBlockBreaks extends SimpleModule<Core> implements Listener {
    private static final BlockFace[] CHECK_FACES = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
    private static final Material[] GROUNDED_MATERIALS = {
        Material.IRON_DOOR, // also
        Material.REDSTONE_WIRE,
        Material.POWERED_RAIL,
        Material.DETECTOR_RAIL,
        Material.ACTIVATOR_RAIL,
    };
    private static final Material[] NOT_WATERPROOF = {
        Material.SNOW,
        Material.SNOW_BLOCK,
        Material.TORCH,
        Material.FLOWER_POT,
        Material.BROWN_MUSHROOM,
        Material.RED_MUSHROOM,
        Material.VINE,
        Material.STONE_BUTTON,
        Material.TRIPWIRE_HOOK,
        Material.REDSTONE_WIRE,
        Material.LEVER,
    };
    private static AdditionalBlockBreaks registeredInstance = null;
    /**
     * List of grounded Items. Attached Items doesn't need to be listed.
     * 
     * Public static to let AddOns update the list, in case the lib is outdated. This isn't good practice, but nevermind.
     */
    public List<Material> groundedItems = Arrays.asList(GROUNDED_MATERIALS);
    
    /**
     * List of not waterproof items.
     * 
     * Public static to let AddOns update the list, in case the lib is outdated. This isn't good practice, but nevermind.
     */
    public List<Material> notWaterproofItems = Arrays.asList(NOT_WATERPROOF);
    
    public AdditionalBlockBreaks(final Core plugin) {
        super(plugin);
    }
    
    @Override
    public void onEnable() {
        if (registeredInstance == null) {
            registeredInstance = this;
            super.onEnable();
        }
    }
    @Override
    public void onDisable() {
        if (registeredInstance == this) { // shouldn't be neccessary, but may be called manually
            registeredInstance = null;
        }
        super.onDisable();
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        final List<Block> breakingBlockList = getAttachedBlocks(event.getBlock());
        for (Block breakingBlock : breakingBlockList) {
            AttachedBlockDestroyedEvent customEvent = new AttachedBlockDestroyedByPlayerEvent(breakingBlock, new AttachedBlockDestroyedByPlayerEvent.BlockBreakEventData(event));
            plugin.getServer().getPluginManager().callEvent(customEvent);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockFromTo(final BlockFromToEvent event) {
        if (notWaterproofItems.contains(event.getToBlock().getType())) {
            WaterDestroyedBlockEvent customEvent = new WaterDestroyedBlockEvent(event.getToBlock(), event.getBlock());
            plugin.getServer().getPluginManager().callEvent(customEvent);
        }
    }
    
    public List<Block> getAttachedBlocks(final Block attachedTo) {
        List<Block> blocks = new ArrayList<Block>(CHECK_FACES.length);
        if (groundedItems.contains(attachedTo.getRelative(BlockFace.UP).getType())) {
            blocks.add(attachedTo.getRelative(BlockFace.UP));
        }
        for (BlockFace face : CHECK_FACES) {
            if (attachedTo.getRelative(face).getState().getData() instanceof Attachable) {
                BlockFace attacedFace = ((Attachable) attachedTo.getRelative(face).getState().getData()).getAttachedFace();
                if (attacedFace.getOppositeFace() == face) {
                    blocks.add(attachedTo.getRelative(face));
                }
            }
        }
        return blocks;
    }
}
