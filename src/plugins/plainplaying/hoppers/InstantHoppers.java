package plugins.plainplaying.hoppers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class InstantHoppers extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this,this);
        getLogger().info("InstantHoppers is enabled!");
    }
    @Override
    public void onDisable() {
        getLogger().info("InstantHoppers is disabled!");
    }

    @Override
    public void onLoad() {
        getLogger().info("InstantHoppers loaded successfully!");
    }
    @EventHandler
    public void onHopperPickup(InventoryPickupItemEvent e) {
        if (e.getInventory().getType().equals(InventoryType.HOPPER)) {
            moveToNextHopper((Hopper)e.getInventory().getHolder());
        }
    }
    @EventHandler
    public void onHopperMoved(InventoryMoveItemEvent e) {
        if (e.getDestination().getType().equals(InventoryType.HOPPER)) {
            moveToNextHopper((Hopper)e.getSource().getHolder());
        }
    }
    @EventHandler
    public void onManualHopperPlaced(InventoryClickEvent e){
        if (e.getInventory().getType().equals(InventoryType.HOPPER)) {
            if (e.getCurrentItem() != null && !e.getCurrentItem().getType().equals(Material.AIR)) {
                moveToNextHopper((Hopper)e.getInventory().getHolder());
            }
        }
    }
    private void moveToNextHopper(Hopper hopper){
        Inventory hopperItems = hopper.getInventory();
        if (hopper.getBlock().getData() == 0x0) {
            Material blockBelow = hopper.getLocation().subtract(0, 1, 0).getBlock().getType();
            if (blockBelow.equals(Material.HOPPER)) {
                mergeInventories(hopperItems,((Hopper) hopper.getLocation().subtract(0, 1, 0).getBlock().getState()).getInventory());
            }else if (blockBelow.equals(Material.CHEST)){
                mergeInventories(hopperItems,((Chest) hopper.getLocation().subtract(0, 1, 0).getBlock().getState()).getInventory());
            }else if (blockBelow.equals(Material.TRAPPED_CHEST)){
                mergeInventories(hopperItems,((org.bukkit.block.) hopper.getLocation().subtract(0, 1, 0).getBlock().getState()).getInventory());
            }
        }
    }
    private void mergeInventories(Inventory mergeFrom, Inventory mergeTo){
        for (ItemStack merged : mergeTo){
            for (ItemStack merging : mergeFrom){
                if (merged != null){
                    if (merging != null){
                        if (merged.isSimilar(merging) && compareIfNotNull(merged.getItemMeta().getDisplayName(),merging.getItemMeta().getDisplayName()) && compareIfNotNull(merged.getItemMeta().getLore(),merged.getItemMeta().getLore())){
                            getLogger().info(merged.getAmount()+", "+merging.getAmount());
                            if (merged.getAmount() + merging.getAmount() > 64){
                                int oldAmount = merged.getAmount();
                                merged.setAmount(64);
                                merging.setAmount(oldAmount + merging.getAmount()-64);
                            }else{
                                merged.setAmount(merged.getAmount() + merging.getAmount());
                                merging.setAmount(0);
                            }
                        }
                    }
                }else{
                    if (merging != null) {
                        mergeTo.setItem(mergeTo.firstEmpty(), merging);
                        merging.setAmount(0);
                    }
                }
            }
        }
    }
    private <T> boolean compareIfNotNull(T first, T second){
        if (first == null || second == null){
            return true;
        }
        return first.equals(second);
    }
}