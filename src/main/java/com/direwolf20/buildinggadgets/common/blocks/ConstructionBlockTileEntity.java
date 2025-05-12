package com.direwolf20.buildinggadgets.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import com.direwolf20.buildinggadgets.util.datatypes.BlockState;

public class ConstructionBlockTileEntity extends TileEntity {
    private Block block;
    private int blockMeta;

    private Block actualBlock;
    private int actualBlockMeta;

    public boolean setBlockState(Block block, int meta) {
        this.block = block;
        this.blockMeta = meta;

        markDirtyClient();

        return true;
    }

    public boolean setBlockState(BlockState blockState) {
        return this.setBlockState(blockState.block(), blockState.metadata());
    }

    public boolean setActualBlockState(Block block, int meta) {
        this.actualBlock = block;
        this.actualBlockMeta = meta;

        markDirtyClient();

        return true;
    }

    public boolean setActualBlockState(BlockState blockState) {
        return this.setActualBlockState(blockState.block(), blockState.metadata());
    }

    public BlockState getBlockState() {
        return new BlockState(this.getBlock(), this.getBlockMeta());
    }

    public BlockState getActualBlockState() {
        return new BlockState(this.getActualBlock(), this.getActualBlockMeta());
    }

    public Block getBlock() {
        if (block == null || block == Blocks.air) {
            return null;
        }
        return block;
    }

    public int getBlockMeta() {
        return blockMeta;
    }

    public Block getActualBlock() {
        if (actualBlock == null || actualBlock == Blocks.air) {
            return null;
        }
        return actualBlock;
    }

    public int getActualBlockMeta() {
        return actualBlockMeta;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.block = Block.getBlockById(compound.getInteger("blockId"));
        this.blockMeta = compound.getInteger("blockMeta");
        this.actualBlock = Block.getBlockById(compound.getInteger("actualBlockId"));
        this.actualBlockMeta = compound.getInteger("actualBlockMeta");
        markDirtyClient();
    }

    private void markDirtyClient() {
        markDirty();
        if (this.worldObj != null) {
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        writeToNBT(nbtTag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        Block oldBlock = getBlock();
        int oldMeta = getBlockMeta();
        NBTTagCompound tagCompound = packet.func_148857_g();
        super.onDataPacket(net, packet);
        readFromNBT(tagCompound);

        //
        if (this.worldObj.isRemote) {
            if (getBlock() != oldBlock || getBlockMeta() != oldMeta) {
                this.worldObj.markBlockRangeForRenderUpdate(
                    this.xCoord,
                    this.yCoord,
                    this.zCoord,
                    this.xCoord,
                    this.yCoord,
                    this.zCoord);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (block != null) {
            compound.setInteger("blockId", Block.getIdFromBlock(block));
            compound.setInteger("blockMeta", blockMeta);
        }
        if (actualBlock != null) {
            compound.setInteger("actualBlockId", Block.getIdFromBlock(actualBlock));
            compound.setInteger("actualBlockMeta", actualBlockMeta);
        }
    }
}
