package com.direwolf20.buildinggadgets.common.items;

import net.minecraft.item.Item;

import com.direwolf20.buildinggadgets.BuildingGadgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemModBase extends Item {

    public ItemModBase() {}

    public ItemModBase(String name) {
        setUnlocalizedName(String.join(".", BuildingGadgets.MODID, name));
        setCreativeTab(BuildingGadgets.BUILDING_CREATIVE_TAB);
        setTextureName(String.join(":", BuildingGadgets.MODID, name));
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        // TODO(johnrowl) handle model loading.
        // ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(),
        // "inventory"));
    }
}
