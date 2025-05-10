package com.direwolf20.buildinggadgets.common.entities;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import com.direwolf20.buildinggadgets.common.blocks.ConstructionBlock;
import com.direwolf20.buildinggadgets.common.blocks.ConstructionBlockPowder;
import com.direwolf20.buildinggadgets.common.blocks.ConstructionBlockTileEntity;
import com.direwolf20.buildinggadgets.util.NBTTool;
import com.direwolf20.buildinggadgets.util.datatypes.BlockState;

public class ConstructionBlockEntity extends Entity {

    private static int MAKING = 20;
    private static final int FIXED_X = 21;
    private static final int FIXED_Y = 22;
    private static final int FIXED_Z = 23;

    private int despawning = -1;
    public int maxLife = 80;
    private ChunkCoordinates setPos;
    private World world;

    public ConstructionBlockEntity(World worldIn) {
        super(worldIn);
        setSize(0.1F, 0.1F);
        world = worldIn;
    }

    public ConstructionBlockEntity(World worldIn, ChunkCoordinates spawnPos, boolean makePaste) {
        super(worldIn);
        setSize(0.1F, 0.1F);
        world = worldIn;
        setPosition(spawnPos.posX, spawnPos.posY, spawnPos.posZ);
        setPos = spawnPos;
        setMakingPaste(makePaste);
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
        if (setPos != null) {
            var block = world.getBlock(setPos.posX, setPos.posY, setPos.posZ);
            if (!(block instanceof ConstructionBlock) && !(block instanceof ConstructionBlockPowder)) {
                setDespawning();
            }
        }

        if (!isDespawning()) {
            //
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
            if (setPos != null) {
                if (!getMakingPaste()) {
                    TileEntity te = world.getTileEntity(setPos.posX, setPos.posY, setPos.posZ);
                    if (te instanceof ConstructionBlockTileEntity) {
                        BlockState tempState = ((ConstructionBlockTileEntity) te).getBlockState();
                        if (tempState == null) return;

                        int opacity = tempState.block()
                            .getLightOpacity(world, setPos.posX, setPos.posY, setPos.posZ);
                        boolean neighborBrightness = tempState.block()
                            .getUseNeighborBrightness();

                        if (opacity == 255 || neighborBrightness) {
                            BlockState tempSetBlock = ((ConstructionBlockTileEntity) te).getBlockState();
                            BlockState tempActualSetBlock = ((ConstructionBlockTileEntity) te).getActualBlockState();

                            // TODO(johnrowl) this whole thing needs to get reimplemented to actually look right.
                            // world.setBlockMetadataWithNotify(setPos, ModBlocks.constructionBlock.getDefaultState()
                            // .withProperty(ConstructionBlock.BRIGHT, opacity != 255)
                            // .withProperty(ConstructionBlock.NEIGHBOR_BRIGHTNESS, neighborBrightness));

                            te = world.getTileEntity(setPos.posX, setPos.posY, setPos.posZ);
                            if (te instanceof ConstructionBlockTileEntity) {
                                ((ConstructionBlockTileEntity) te).setBlockState(tempSetBlock);
                                ((ConstructionBlockTileEntity) te).setActualBlockState(tempActualSetBlock);
                            }
                        }
                    }
                } else if (world.getBlockMetadata(setPos.posX, setPos.posY, setPos.posZ) == 0) {
                    world.setBlockMetadataWithNotify(setPos.posX, setPos.posY, setPos.posZ, 0, 2);
                }
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

    public void setMakingPaste(Boolean paste) {
        this.dataWatcher.updateObject(MAKING, paste == true ? 1 : 0);
    }

    public boolean getMakingPaste() {
        return this.dataWatcher.getWatchableObjectInt(MAKING) == 1;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("despawning", despawning);
        compound.setInteger("ticksExisted", ticksExisted);
        compound.setTag("setPos", NBTTool.createPosTag(setPos));
        compound.setBoolean("makingPaste", getMakingPaste());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        // System.out.println(compound);
        despawning = compound.getInteger("despawning");
        ticksExisted = compound.getInteger("ticksExisted");
        setPos = NBTTool.getPosFromTag(compound.getCompoundTag("setPos"));
        setMakingPaste(compound.getBoolean("makingPaste"));
    }

    @Override
    protected void entityInit() {
        this.dataWatcher.addObject(FIXED_X, 0);
        this.dataWatcher.addObject(FIXED_Y, 0);
        this.dataWatcher.addObject(FIXED_Z, 0);
        this.dataWatcher.addObject(MAKING, 0);
    }

    public ChunkCoordinates getFixedPos() {
        int x = this.dataWatcher.getWatchableObjectInt(FIXED_X);
        int y = this.dataWatcher.getWatchableObjectInt(FIXED_Y);
        int z = this.dataWatcher.getWatchableObjectInt(FIXED_Z);
        return new ChunkCoordinates(x, y, z);
    }

    @Override
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return true;
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0; // After tr
    }

}
