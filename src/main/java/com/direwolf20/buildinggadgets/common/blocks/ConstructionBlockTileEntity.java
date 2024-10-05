package com.direwolf20.buildinggadgets.common.blocks;

import com.direwolf20.buildinggadgets.common.tools.BlockState;
import com.direwolf20.buildinggadgets.common.tools.NBTTool;
import com.direwolf20.buildinggadgets.common.tools.WorldUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public class ConstructionBlockTileEntity extends TileEntity {
    private BlockState blockState;
    private BlockState actualBlockState;

    public boolean setBlockState(BlockState state) {
        blockState = state;
        markDirtyClient();
        return true;
    }


    public boolean setActualBlockState(BlockState state) {
        actualBlockState = state;
        markDirtyClient();
        return true;
    }

    @Nullable
    public BlockState getBlockState() {
        if (blockState == null || blockState.isAir()) {
            return null;
        }

        return blockState;
    }

    @Nullable
    public BlockState getActualBlockState() {
        if (actualBlockState == null || actualBlockState.isAir()) {
            return null;
        }

        return actualBlockState;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        blockState = NBTTool.blockFromCompound(compound.getCompoundTag("blockState"));
        actualBlockState = NBTTool.blockFromCompound(compound.getCompoundTag("actualBlockState"));
        markDirtyClient();
    }

    private void markDirtyClient() {
        markDirty();

        var world = getWorldObj();
        if (world != null) {
            var state = WorldUtils.getBlockState(world, xCoord, yCoord, zCoord);
            world.markBlockForUpdate(xCoord, yCoord, zCoord);

            // TODO(johnrowl) maybe overload this?
            world.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, state.getBlock());
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        writeToNBT(nbtTag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        BlockState oldMimicBlock = getBlockState();
        NBTTagCompound tagCompound = packet.func_148857_g();

        super.onDataPacket(net, packet);
        readFromNBT(tagCompound);

        if (getWorldObj().isRemote) {
            if (getBlockState() != oldMimicBlock) {
                getWorldObj().markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (blockState != null) {
            var blockStateTag = NBTTool.blockToCompound(blockState);
            compound.setTag("blockState", blockStateTag);

            if (actualBlockState != null) {
                NBTTagCompound actualBlockStateTag = NBTTool.blockToCompound(actualBlockState);
                compound.setTag("actualBlockState", actualBlockStateTag);
            }
        }
    }
}
