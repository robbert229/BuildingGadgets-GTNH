package com.direwolf20.buildinggadgets.common.items.pastes;

import java.util.List;
import java.util.function.IntSupplier;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.tools.InventoryManipulation;
import com.direwolf20.buildinggadgets.util.NBTTool;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ConstructionPasteContainer extends GenericPasteContainer {

    private final String name;
    private final IntSupplier maxCapacity;

    private IIcon iconEmpty;
    private IIcon iconQuarter;
    private IIcon iconHalf;
    private IIcon icon3Quarter;
    private IIcon iconFull;

    public ConstructionPasteContainer(String suffix, IntSupplier maxCapacity) {
        super("constructionpastecontainer" + suffix);

        this.name = "constructionpastecontainer" + suffix;
        this.maxCapacity = maxCapacity;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        this.iconEmpty = register.registerIcon(BuildingGadgets.MODID + ":" + this.name + "");
        this.iconQuarter = register.registerIcon(BuildingGadgets.MODID + ":" + this.name + "-quarter");
        this.iconHalf = register.registerIcon(BuildingGadgets.MODID + ":" + this.name + "-half");
        this.icon3Quarter = register.registerIcon(BuildingGadgets.MODID + ":" + this.name + "-3quarter");
        this.iconFull = register.registerIcon(BuildingGadgets.MODID + ":" + this.name + "-full");
    }

    @Override
    public IIcon getIconFromDamage(int i) {
        int amount = this.getMaxDamage() - i;

        if (amount == this.getMaxCapacity()) {
            return this.iconFull;
        } else if (amount >= 0.75 * this.getMaxCapacity()) {
            return this.icon3Quarter;
        } else if (amount >= 0.50 * this.getMaxCapacity()) {
            return this.iconHalf;
        } else if (amount >= 0.25 * this.getMaxCapacity()) {
            return this.iconQuarter;
        } else {
            return this.iconEmpty;
        }
    }

    @Override
    public void setPasteCount(ItemStack stack, int amount) {
        NBTTool.getOrNewTag(stack)
            .setInteger("amount", amount);

        this.setMaxDamage(this.getMaxCapacity());
        this.setDamage(stack, this.getMaxDamage() - amount);
    }

    @Override
    public int getPasteCount(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return 0;
        }

        return stack.getTagCompound()
            .getInteger("amount");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack heldItem, World worldIn, EntityPlayer player) {
        InventoryPlayer inv = player.inventory;
        if (!worldIn.isRemote) {
            for (int i = 0; i < 36; ++i) {
                ItemStack itemStack = inv.getStackInSlot(i);
                if (itemStack != null && itemStack.getItem() instanceof ConstructionPaste) {
                    InventoryManipulation.addPasteToContainer(player, itemStack);
                }
            }
        }
        return heldItem;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
        list.add(getAmountDisplayLocalized() + ": " + getPasteAmount(stack));
    }

    @Override
    public int getMaxCapacity() {
        return maxCapacity.getAsInt();
    }
}
