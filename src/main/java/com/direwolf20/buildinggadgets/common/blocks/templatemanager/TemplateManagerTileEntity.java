package com.direwolf20.buildinggadgets.common.blocks.templatemanager;

import java.util.Arrays;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import com.cleanroommc.modularui.api.IGuiHolder;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ItemSlot;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.direwolf20.buildinggadgets.common.items.ModItems;
import com.google.common.collect.ImmutableSet;

public class TemplateManagerTileEntity extends TileEntity implements IInventory, IGuiHolder<PosGuiData> {

    public static final int SIZE = 2;
    private ItemStack[] inventory = new ItemStack[SIZE];
    private final IItemHandler inventoryHandler = new ItemStackHandler(inventory);

    public TemplateManagerTileEntity() {
        Arrays.fill(inventory, null);
    }

    @Override
    public int getSizeInventory() {
        return SIZE;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return inventory[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (inventory[index] != null) {
            if (inventory[index].stackSize <= count) {
                ItemStack itemstack = inventory[index];
                inventory[index] = null;
                markDirty();
                return itemstack;
            } else {
                ItemStack itemstack = inventory[index].splitStack(count);
                if (inventory[index].stackSize == 0) {
                    inventory[index] = null;
                }
                markDirty();
                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        if (inventory[index] != null) {
            ItemStack itemstack = inventory[index];
            inventory[index] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory[index] = stack;
        if (stack != null && stack.getItem() != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return "TemplateManager";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
            && player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64D;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        Set<Item> allowedItemsLeft = ImmutableSet.of(ModItems.gadgetCopyPaste, ModItems.template);
        Set<Item> allowedItemsRight = ImmutableSet.of(Items.paper, ModItems.template);

        if (index == 0) {
            return allowedItemsLeft.contains(stack.getItem());
        } else if (index == 1) {
            return allowedItemsRight.contains(stack.getItem());
        }
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList tagList = compound.getTagList("items", 10);
        inventory = new ItemStack[getSizeInventory()];

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound itemTag = tagList.getCompoundTagAt(i);
            int slot = itemTag.getByte("Slot");
            if (slot >= 0 && slot < inventory.length) {
                inventory[slot] = ItemStack.loadItemStackFromNBT(itemTag);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagList tagList = new NBTTagList();

        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) i);
                inventory[i].writeToNBT(itemTag);
                tagList.appendTag(itemTag);
            }
        }

        compound.setTag("items", tagList);
    }

    public TemplateManagerContainer getContainer(EntityPlayer playerIn) {
        return new TemplateManagerContainer(playerIn.inventory, this);
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        // If we are too far away from this tile entity you cannot use it
        return !isInvalid() && playerIn.getDistanceSq(xCoord, yCoord, zCoord) <= 64D;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager) {
        var controls = Flow.column()
            .child(
                IKey.str("Template Manager")
                    .asWidget()
                    .marginTop(7))
            .child(
                Flow.row()
                    .child(
                        new ButtonWidget<>().overlay(IKey.str("Save"))
                            .height(20)
                            .width(40)
                            .marginRight(7)

                    )
                    .child(
                        new ButtonWidget<>().overlay(IKey.str("Load"))
                            .height(20)
                            .width(40)
                            .marginLeft(7)

                    )
                    .coverChildrenHeight()
                    .coverChildrenWidth()
                    .marginTop(7))
            .child(
                Flow.row()
                    .child(
                        new ItemSlot().slot(new ModularSlot(this.inventoryHandler, 0).slotGroup("gadget"))
                            .marginRight(7)

                    )
                    .child(
                        new ProgressWidget().size(20)
                            .texture(GuiTextures.PROGRESS_ARROW, 20)
                            .value(new DoubleSyncValue(() -> 0, (val) -> {})))
                    .child(
                        new ItemSlot().slot(new ModularSlot(this.inventoryHandler, 1).slotGroup("template"))
                            .marginLeft(7))
                    .coverChildrenHeight()
                    .coverChildrenWidth()
                    .marginTop(7))
            .child(
                Flow.row()
                    .child(
                        new ButtonWidget<>().overlay(IKey.str("Copy"))
                            .width(40)
                            .marginRight(7))
                    .child(
                        new ButtonWidget<>().overlay(IKey.str("Paste"))
                            .width(40)
                            .marginLeft(7))
                    .coverChildrenHeight()
                    .coverChildrenWidth()
                    .marginTop(7))
            .coverChildrenHeight();

        return ModularPanel.defaultPanel("templateemanager")
            .child(controls)
            .child(
                SlotGroupWidget.playerInventory()
                    .coverChildrenHeight())
            .heightRel(0.4f);
    }
}
