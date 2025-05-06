package com.direwolf20.buildinggadgets.common.entities;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import com.direwolf20.buildinggadgets.common.blocks.ConstructionBlockTileEntity;
import com.direwolf20.buildinggadgets.common.blocks.ModBlocks;
import com.direwolf20.buildinggadgets.util.ChunkCoordinateUtils;
import com.direwolf20.buildinggadgets.util.NBTTool;
import com.direwolf20.buildinggadgets.util.WorldUtils;
import com.direwolf20.buildinggadgets.util.datatypes.BlockState;

public class BlockBuildEntity extends Entity {

    private static final int TOOL_MODE_INDEX = 20; // Any unused index
    private static final int SET_BLOCK_METADATA_INDEX = 21;
    private static final int SET_BLOCK_ID_INDEX = 22;
    private static final int USE_PASTE_INDEX = 23;
    private static final int FIXED_X = 24;
    private static final int FIXED_Y = 25;
    private static final int FIXED_Z = 26;

    private int despawning = -1;
    public int maxLife = 20;
    private int mode;
    private BlockState setBlock;
    private BlockState originalSetBlock;
    private BlockState actualSetBlock;
    private ChunkCoordinates setPos;
    private EntityLivingBase spawnedBy;
    private boolean useConstructionPaste;

    private World world;

    public BlockBuildEntity(World worldIn) {
        super(worldIn);
        setSize(0.1F, 0.1F);
        world = worldIn;
    }

    public BlockBuildEntity(World worldIn, ChunkCoordinates spawnPos, EntityLivingBase player, BlockState spawnBlock,
        int toolMode, BlockState actualSpawnBlock, boolean constrPaste) {
        super(worldIn);

        setSize(0.1F, 0.1F);
        setPosition(spawnPos.posX, spawnPos.posY, spawnPos.posZ);
        BlockState currentBlock = BlockState.getBlockState(worldIn, spawnPos);
        TileEntity te = worldIn.getTileEntity(spawnPos.posX, spawnPos.posY, spawnPos.posZ);

        setPos = spawnPos;

        if (te instanceof ConstructionBlockTileEntity cbte) {
            if (cbte.getBlock() == null || cbte.getBlock() == Blocks.air) {
                setBlock = spawnBlock;
            } else {
                setBlock = new BlockState(cbte.getBlock(), cbte.getBlockMeta());
            }
        } else {
            setBlock = spawnBlock;
        }

        originalSetBlock = spawnBlock;
        setSetBlock(setBlock);
        if (toolMode == 3) {
            if (currentBlock != null) {
                if (te instanceof ConstructionBlockTileEntity cbte) {
                    if (cbte.getBlock() == null || cbte.getBlock() == Blocks.air) {
                        setBlock = currentBlock;
                    } else {
                        setBlock = new BlockState(cbte.getBlock(), cbte.getBlockMetadata());
                    }
                } else {
                    setBlock = currentBlock;
                }
                setSetBlock(setBlock);
            } else {
                setBlock = null;
                setSetBlock(setBlock);
            }
        }
        world = worldIn;
        mode = toolMode;
        setToolMode(toolMode);
        spawnedBy = player;
        actualSetBlock = actualSpawnBlock;

        // Don't let leaves decay
        // if (setBlock.getBlock() instanceof BlockLeaves) {
        // setBlock = setBlock.withProperty(BlockLeaves.DECAYABLE, false);
        // }

        world.setBlock(spawnPos.posX, spawnPos.posY, spawnPos.posZ, ModBlocks.effectBlock);
        // world.setBlockState(spawnPos, ModBlocks.effectBlock.getDefaultState());

        if (setBlock.getBlock() instanceof BlockLeaves) {
            // Set the metadata to prevent leaf decay
            int metadata = world.getBlockMetadata(spawnPos.posX, spawnPos.posY, spawnPos.posZ);
            world.setBlockMetadataWithNotify(spawnPos.posX, spawnPos.posY, spawnPos.posZ, metadata & (~8), 2); // `~8`
                                                                                                               // removes
                                                                                                               // the
                                                                                                               // DECAYABLE
                                                                                                               // bit

        }

        setUsingConstructionPaste(constrPaste);
    }

    public int getToolMode() {
        return this.dataWatcher.getWatchableObjectInt(TOOL_MODE_INDEX);
    }

    public void setToolMode(int mode) {
        this.dataWatcher.updateObject(TOOL_MODE_INDEX, mode);
    }

    @Nullable
    public BlockState getSetBlock() {
        var blockId = this.dataWatcher.getWatchableObjectInt(SET_BLOCK_ID_INDEX);

        if (blockId == 0) {
            return null;
        }

        var metadata = this.dataWatcher.getWatchableObjectInt(SET_BLOCK_METADATA_INDEX);
        return new BlockState(Block.getBlockById(blockId), metadata);
    }

    public void setSetBlock(@Nullable BlockState state) {
        int metadata = 0;
        if (state != null) {
            metadata = state.getMetadata();
        }

        this.dataWatcher.updateObject(SET_BLOCK_ID_INDEX, Block.getIdFromBlock(state.getBlock()));
        this.dataWatcher.updateObject(SET_BLOCK_METADATA_INDEX, metadata);
    }

    public void setUsingConstructionPaste(Boolean paste) {
        this.dataWatcher.updateObject(USE_PASTE_INDEX, paste ? 1 : 0);
    }

    public boolean getUsingConstructionPaste() {
        return this.dataWatcher.getWatchableObjectInt(USE_PASTE_INDEX) == 1;
    }

    @Override
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return true;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0; // After tr
    }

    public int getTicksExisted() {
        return ticksExisted;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (ticksExisted > maxLife) {
            setDespawning();
        }

        if (!isDespawning()) {

        } else {
            despawnTick();
        }
    }

    public boolean isDespawning() {
        return despawning != -1;
    }

    private void setDespawning() {
        if (despawning == -1) {
            despawning = 0;
            if (setPos != null && setBlock != null && (getToolMode() == 1)) {
                if (getUsingConstructionPaste()) {
                    world.setBlock(setPos.posX, setPos.posY, setPos.posZ, ModBlocks.constructionBlock);

                    TileEntity te = world.getTileEntity(setPos.posX, setPos.posY, setPos.posZ);
                    if (te instanceof ConstructionBlockTileEntity) {
                        ((ConstructionBlockTileEntity) te).setBlockState(setBlock.getBlock(), setBlock.getMetadata());
                        ((ConstructionBlockTileEntity) te)
                            .setActualBlockState(actualSetBlock.getBlock(), actualSetBlock.getMetadata());
                    }

                    world.spawnEntityInWorld(new ConstructionBlockEntity(world, setPos, false));
                } else {
                    world.setBlockMetadataWithNotify(setPos.posX, setPos.posY, setPos.posZ, setBlock.getMetadata(), 2);
                    var neighborBlock = WorldUtils.getBlock(world, ChunkCoordinateUtils.up(setPos));

                    BlockState.getBlockState(world, setPos)
                        .getBlock()
                        .onNeighborBlockChange(world, setPos.posX, setPos.posY, setPos.posZ, neighborBlock);
                }
            } else if (setPos != null && setBlock != null && getToolMode() == 2) {
                world.setBlock(setPos.posX, setPos.posY, setPos.posZ, Blocks.air);
            } else if (setPos != null && setBlock != null && getToolMode() == 3) {
                world.spawnEntityInWorld(
                    new BlockBuildEntity(
                        world,
                        setPos,
                        spawnedBy,
                        originalSetBlock,
                        1,
                        actualSetBlock,
                        getUsingConstructionPaste()));
            }
        }
    }

    private void despawnTick() {
        despawning++;
        if (despawning > 1) {
            setDead();
        }
    }

    @Override
    public void setDead() {
        this.isDead = true;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("despawning", despawning);
        compound.setInteger("ticksExisted", ticksExisted);
        compound.setTag("setPos", NBTTool.createPosTag(setPos));
        NBTTagCompound blockStateTag = new NBTTagCompound();
        NBTTool.writeBlockToCompound(blockStateTag, setBlock);
        compound.setTag("setBlock", blockStateTag);
        NBTTagCompound actualBlockStateTag = new NBTTagCompound();
        NBTTool.writeBlockToCompound(actualBlockStateTag, actualSetBlock);
        compound.setTag("actualSetBlock", actualBlockStateTag);
        NBTTool.writeBlockToCompound(blockStateTag, originalSetBlock);
        compound.setTag("originalBlock", blockStateTag);
        compound.setInteger("mode", mode);
        compound.setBoolean("paste", useConstructionPaste);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        despawning = compound.getInteger("despawning");
        ticksExisted = compound.getInteger("ticksExisted");
        setPos = NBTTool.getPosFromTag(compound.getCompoundTag("setPos"));
        setBlock = NBTTool.blockFromCompound(compound.getCompoundTag("setBlock"));
        originalSetBlock = NBTTool.blockFromCompound(compound.getCompoundTag("originalBlock"));
        actualSetBlock = NBTTool.blockFromCompound(compound.getCompoundTag("actualSetBlock"));
        mode = compound.getInteger("mode");
        useConstructionPaste = compound.getBoolean("paste");
    }

    @Override
    protected void entityInit() {
        this.dataWatcher.addObject(SET_BLOCK_METADATA_INDEX, 0);
        this.dataWatcher.addObject(SET_BLOCK_ID_INDEX, 0);
        this.dataWatcher.addObject(USE_PASTE_INDEX, useConstructionPaste ? 1 : 0);
        this.dataWatcher.addObject(TOOL_MODE_INDEX, 1);
        this.dataWatcher.addObject(FIXED_X, 0);
        this.dataWatcher.addObject(FIXED_Y, 0);
        this.dataWatcher.addObject(FIXED_Z, 0);
    }
}
