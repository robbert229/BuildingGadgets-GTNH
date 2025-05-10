package com.direwolf20.buildinggadgets.common.items.gadgets;

import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.direwolf20.buildinggadgets.BuildingGadgetsConfig.GadgetsConfig;
import com.direwolf20.buildinggadgets.BuildingGadgetsConfig.GadgetsConfig.GadgetExchangerConfig;
import com.direwolf20.buildinggadgets.BuildingGadgetsConfig.GeneralConfig;
import com.direwolf20.buildinggadgets.client.gui.GuiUtils;
import com.direwolf20.buildinggadgets.common.tools.*;
import com.direwolf20.buildinggadgets.util.NBTTool;
import com.mojang.realmsclient.gui.ChatFormatting;
// import net.minecraft.util.MovingObjectPosition;
// import net.minecraft.util.text.TextComponentString;
// import net.minecraft.util.text.TextComponentTranslation;
// import net.minecraft.util.text.TextFormatting;
// import net.minecraft.world.World;
// import net.minecraft.world.WorldType;
// import net.minecraftforge.common.util.BlockSnapshot;
// import net.minecraftforge.fml.relauncher.Side;
// import net.minecraftforge.fml.relauncher.SideOnly;

// import javax.annotation.Nullable;
// import java.util.ArrayList;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;

public class GadgetExchanger extends GadgetGeneric {

    // private static final FakeBuilderWorld fakeWorld = new FakeBuilderWorld();

    public GadgetExchanger() {
        super("exchangertool");
        setMaxDamage(GadgetExchangerConfig.durabilityExchanger);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GeneralConfig.poweredByFE ? 0 : GadgetExchangerConfig.durabilityExchanger;
    }

    @Override
    public int getEnergyCost(ItemStack tool) {
        return GadgetExchangerConfig.energyCostExchanger;
    }

    @Override
    public int getDamageCost(ItemStack tool) {
        return GadgetExchangerConfig.damageCostExchanger;
    }

    @Override
    public void renderOverlay(RenderWorldLastEvent evt, EntityPlayer player, ItemStack heldItem) {
        // TODO(johnrowl) add renderer.
    }

    @Override
    public ModularScreen getShortcutMenuGUI(ItemStack itemStack, boolean temporarilyEnabled) {
        ModularPanel panel = ModularPanel.defaultPanel(GuiUtils.getPanelName("stubbed"));
        panel.child(
            IKey.str("STUBBED")
                .asWidget()
                .top(7)
                .left(7));
        return new ModularScreen(panel);
    }

    @Override
    public void anchorBlocks(EntityPlayer player, ItemStack stack) {
        GadgetUtils.anchorBlocks(player, stack);
    }

    @Override
    public int getItemEnchantability() {
        return 3;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, book) > 0) {
            return true;
        }

        return super.isBookEnchantable(stack, book);
    }

    private static void setToolMode(ItemStack tool, ExchangingModes mode) {
        // Store the tool's mode in NBT as a string
        NBTTagCompound tagCompound = NBTTool.getOrNewTag(tool);
        tagCompound.setString("mode", mode.getRegistryName());
    }

    public static ExchangingModes getToolMode(ItemStack tool) {
        NBTTagCompound tagCompound = NBTTool.getOrNewTag(tool);
        return ExchangingModes.byName(tagCompound.getString("mode"));
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
        super.addInformation(stack, player, list, b);

        list.add(
            EnumChatFormatting.DARK_GREEN + StatCollector.translateToLocal("tooltip.gadget.block")
                + ": "
                + GadgetUtils.getToolBlock(stack)
                    .block()
                    .getLocalizedName());

        ExchangingModes mode = getToolMode(stack);
        String modeText = (mode == ExchangingModes.Surface && getConnectedArea(stack))
            ? StatCollector.translateToLocal("tooltip.gadget.connected") + " "
            : "";
        list.add(
            EnumChatFormatting.AQUA + StatCollector.translateToLocal("tooltip.gadget.mode") + ": " + modeText + mode);

        list.add(
            EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("tooltip.gadget.range")
                + ": "
                + GadgetUtils.getToolRange(stack));

        list.add(
            EnumChatFormatting.GOLD + StatCollector.translateToLocal("tooltip.gadget.fuzzy") + ": " + getFuzzy(stack));

        addInformationRayTraceFluid(list, stack);
        addEnergyInformation(list, stack);
    }

    //
    // @Override
    // public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    // ItemStack itemstack = player.getHeldItem(hand);
    // player.setActiveHand(hand);
    // if (!world.isRemote) {
    // if (player.isSneaking()) {
    // selectBlock(itemstack, player);
    // } else {
    // exchange(player, itemstack);
    // }
    // } else if (!player.isSneaking()) {
    // ToolRenders.updateInventoryCache();
    // }
    // return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
    // }
    //
    public void setMode(ItemStack heldItem, int modeInt) {
        // Called when we specify a mode with the radial menu
        ExchangingModes mode = ExchangingModes.values()[modeInt];
        setToolMode(heldItem, mode);
    }

    public void rangeChange(EntityPlayer player, ItemStack heldItem) {
        int range = GadgetUtils.getToolRange(heldItem);
        int changeAmount = (getToolMode(heldItem) == ExchangingModes.Grid || (range % 2 == 0)) ? 1 : 2;
        if (player.isSneaking()) {
            range = (range <= 1) ? GadgetsConfig.maxRange : range - changeAmount;
        } else {
            range = (range >= GadgetsConfig.maxRange) ? 1 : range + changeAmount;
        }
        GadgetUtils.setToolRange(heldItem, range);
        player.addChatMessage(
            new ChatComponentText(
                ChatFormatting.DARK_AQUA + new ChatComponentTranslation("message.gadget.toolrange").getUnformattedText()
                    + ": "
                    + range));
    }
    //
    // private boolean exchange(EntityPlayer player, ItemStack stack) {
    // World world = player.world;
    // List<ChunkCoordinates> coords = getAnchor(stack);
    //
    // if (coords.size() == 0) { //If we don't have an anchor, build in the current spot
    // MovingObjectPosition lookingAt = VectorTools.getLookingAt(player, stack);
    // if (lookingAt == null) { //If we aren't looking at anything, exit
    // return false;
    // }
    // ChunkCoordinates startBlock = lookingAt.getBlockPos();
    // EnumFacing sideHit = lookingAt.sideHit;
    //// IBlockState setBlock = getToolBlock(stack);
    // coords = ExchangingModes.collectPlacementPos(world, player, startBlock, sideHit, stack, startBlock);
    // } else { //If we do have an anchor, erase it (Even if the build fails)
    // setAnchor(stack, new ArrayList<ChunkCoordinates>());
    // }
    // Set<ChunkCoordinates> coordinates = new HashSet<ChunkCoordinates>(coords);
    //
    // ItemStack heldItem = getGadget(player);
    // if (heldItem.isEmpty())
    // return false;
    //
    // IBlockState blockState = getToolBlock(heldItem);
    //
    // if (blockState != Blocks.AIR.getDefaultState()) { //Don't attempt a build if a block is not chosen -- Typically
    // only happens on a new tool.
    // IBlockState state = Blocks.AIR.getDefaultState(); //Initialize a new State Variable for use in the fake world
    // fakeWorld.setWorldAndState(player.world, blockState, coordinates); // Initialize the fake world's blocks
    // for (ChunkCoordinates coordinate : coords) {
    // if (fakeWorld.getWorldType() != WorldType.DEBUG_ALL_BLOCK_STATES) {
    // try {
    // state = blockState.getActualState(fakeWorld, coordinate); //Get the state of the block in the fake world (This
    // lets fences be connected, etc)
    // } catch (Exception var8) {
    // }
    // }
    // //Get the extended block state in the fake world
    // //Disabled to fix Chisel
    // //state = state.getBlock().getExtendedState(state, fakeWorld, coordinate);
    // exchangeBlock(world, player, coordinate, state);
    // }
    // GadgetUtils.clearCachedRemoteInventory();
    // }
    // return true;
    // }
    //
    // private boolean exchangeBlock(World world, EntityPlayer player, ChunkCoordinates pos, IBlockState setBlock) {
    // // This is unlikely but good to be sure
    // if( world.isOutsideBuildHeight(pos) )
    // return false;
    //
    // IBlockState currentBlock = world.getBlockState(pos);
    // ItemStack itemStack;
    // boolean useConstructionPaste = false;
    // //ItemStack itemStack = setBlock.getBlock().getPickBlock(setBlock, null, world, pos, player);
    // if (setBlock.getBlock().canSilkHarvest(world, pos, setBlock, player)) {
    // itemStack = InventoryManipulation.getSilkTouchDrop(setBlock);
    // } else {
    // itemStack = setBlock.getBlock().getPickBlock(setBlock, null, world, pos, player);
    // }
    // if (itemStack.getItem().equals(Items.AIR)) {
    // itemStack = setBlock.getBlock().getPickBlock(setBlock, null, world, pos, player);
    // }
    //
    // ItemStack tool = getGadget(player);
    // if (tool.isEmpty())
    // return false;
    //
    // NonNullList<ItemStack> drops = NonNullList.create();
    // setBlock.getBlock().getDrops(drops, world, pos, setBlock, 0);
    // int neededItems = 0;
    // for (ItemStack drop : drops) {
    // if (drop.getItem().equals(itemStack.getItem())) {
    // neededItems++;
    // }
    // }
    // if (neededItems == 0) {
    // neededItems = 1;
    // }
    // if (InventoryManipulation.countItem(itemStack, player, world) < neededItems) {
    // ItemStack constructionPaste = new ItemStack(ModItems.constructionPaste);
    // if (InventoryManipulation.countPaste(player) < neededItems) {
    // return false;
    // }
    // itemStack = constructionPaste.copy();
    // useConstructionPaste = true;
    // }
    //
    // if (!player.isAllowEdit()) {
    // return false;
    // }
    // if (!world.isBlockModifiable(player, pos)) {
    // return false;
    // }
    //
    // if (!GadgetGeneric.EmitEvent.breakBlock(world, pos, currentBlock, player)) {
    // return false;
    // }
    //
    // if (!this.canUse(tool, player))
    // return false;
    //
    // if (!GadgetExchanger.canPlaceBlockAt(world, pos, setBlock, currentBlock))
    // return false;
    //
    // BlockSnapshot blockSnapshot = BlockSnapshot.getBlockSnapshot(world, pos);
    // if (!GadgetGeneric.EmitEvent.placeBlock(player, blockSnapshot, player.getHorizontalFacing(),
    // player.getActiveHand())) {
    // return false;
    // }
    //
    // boolean useItemSuccess;
    // if (useConstructionPaste)
    // useItemSuccess = InventoryManipulation.usePaste(player, 1);
    // else
    // useItemSuccess = InventoryManipulation.useItem(itemStack, player, neededItems, world);
    //
    // if (useItemSuccess) {
    // // Only do something if the use actually happened... How was this logic overlooked? #432
    // this.applyDamage(tool, player);
    //
    // if( !player.capabilities.isCreativeMode )
    // currentBlock.getBlock().harvestBlock(world, player, pos, currentBlock, world.getTileEntity(pos), tool);
    //
    // world.spawnEntity(new BlockBuildEntity(world, pos, player, setBlock, 3, getToolActualBlock(tool),
    // useConstructionPaste));
    // return true;
    // }
    //
    // return false;
    // }
    //
    // /**
    // * This may seem a bit strange at a glance. Basically when checking if a block can be placed
    // * the game checks it against the current block. As the exchanger will not remove that
    // * block we need to do it a slightly different way. The way we resolve this issue
    // * is by removing the original block and setting it to air. We then do the
    // * check and replace the block regardless.
    // *
    // * @param setBlock the block we need to test against
    // * @param originalBlock the block we need to put back on failure
    // */
    // private static boolean canPlaceBlockAt(World world, ChunkCoordinates pos, IBlockState setBlock, IBlockState
    // originalBlock) {
    // world.setBlockState(pos, Blocks.AIR.getDefaultState());
    // boolean canPlace = setBlock.getBlock().canPlaceBlockAt(world, pos);
    // world.setBlockState(pos, originalBlock);
    //
    // return canPlace;
    // }
    //
    // public static ItemStack getGadget(EntityPlayer player) {
    // ItemStack stack = GadgetGeneric.getGadget(player);
    // if (!(stack.getItem() instanceof GadgetExchanger))
    // return ItemStack.EMPTY;
    //
    // return stack;
    // }
    //
    // @Override
    // public int getMaxItemUseDuration(ItemStack stack) {
    // return 20;
    // }
    //
    // @Override
    // @SideOnly(Side.CLIENT)
    // public boolean hasEffect(ItemStack stack) {
    // return false;
    // }

}
