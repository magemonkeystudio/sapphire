package com.promcteam.sapphire;

import com.promcteam.codex.CodexEngine;
import com.promcteam.codex.items.ItemType;
import com.promcteam.codex.items.ProItemManager;
import com.promcteam.codex.items.providers.IProItemProvider;
import com.promcteam.risecore.item.DarkRiseItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SapphireItemProvider implements IProItemProvider<SapphireItemProvider.SapphireItemType> {
    public static String NAMESPACE = "SAPPHIRE";

    public static void register() {
        CodexEngine.getEngine().getItemManager().registerProvider(NAMESPACE, new SapphireItemProvider());
    }

    public static void unregister() {
        CodexEngine.getEngine().getItemManager().unregisterProvider(SapphireItemProvider.class);
    }

    @Override
    public String pluginName() {
        return "Sapphire";
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public Category getCategory() {
        return Category.PRO;
    }

    @Override
    public SapphireItemType getItem(String id) {
        id = ProItemManager.stripPrefix(NAMESPACE, id);

        DarkRiseItem riseItem = Sapphire.getItemsRegistry().getItemById(id);
        if (riseItem == null) {
            return null;
        }

        return new SapphireItemType(riseItem);
    }

    @Override
    @Nullable
    public SapphireItemProvider.SapphireItemType getItem(ItemStack item) {
        DarkRiseItem riseItem = Sapphire.getItemsRegistry().getItemByStack(item);
        if (riseItem == null || riseItem.getId().startsWith("vanilla_")) {
            return null;
        }

        return new SapphireItemType(riseItem);
    }

    @Override
    public boolean isCustomItem(ItemStack item) {
        DarkRiseItem riseItem = Sapphire.getItemsRegistry().getItemByStack(item);
        return riseItem != null && !riseItem.getId().startsWith("vanilla_");
    }

    @Override
    public boolean isCustomItemOfId(ItemStack item, String id) {
        id = ProItemManager.stripPrefix(NAMESPACE, id);

        DarkRiseItem riseItem = Sapphire.getItemsRegistry().getItemByStack(item);
        if (riseItem == null || riseItem.getId().startsWith("vanilla_")) {
            return false;
        }

        return riseItem.getId().equals(id);
    }

    public static class SapphireItemType extends ItemType {
        private final DarkRiseItem riseItem;

        public SapphireItemType(DarkRiseItem riseItem) {
            this.riseItem = riseItem;
        }

        @Override
        public String getNamespace() {
            return NAMESPACE;
        }

        @Override
        public String getID() {
            return this.riseItem.getId();
        }

        @Override
        public Category getCategory() {
            return Category.PRO;
        }

        @Override
        public ItemStack create() {
            return this.riseItem.getItem();
        }

        @Override
        public boolean isInstance(ItemStack item) {
            DarkRiseItem riseItem = Sapphire.getItemsRegistry().getItemByStack(item);
            if (riseItem == null || riseItem.getId().startsWith("vanilla_")) {
                return false;
            }

            return riseItem.getId().equals(this.riseItem.getId());
        }

        public DarkRiseItem getRiseItem() {
            return riseItem;
        }
    }
}
