package com.direwolf20.buildinggadgets.common.entities;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import com.direwolf20.buildinggadgets.common.blocks.ConstructionBlock;
import com.direwolf20.buildinggadgets.common.blocks.ConstructionBlockPowder;
import com.direwolf20.buildinggadgets.common.blocks.ConstructionBlockTileEntity;
import com.direwolf20.buildinggadgets.common.blocks.ModBlocks;
import com.direwolf20.buildinggadgets.common.tools.ConstructionBlockEntityMetadata;
import com.direwolf20.buildinggadgets.common.tools.MetadataUtils;
import com.direwolf20.buildinggadgets.common.tools.NBTTool;

public class ConstructionBlockEntity extends Entity {

    // private static final DataParameter<ChunkCoordinates> FIXED =
    // EntityDataManager.createKey(ConstructionBlockEntity.class, DataSerializers.BLOCK_POS);
    // private static final DataParameter<Boolean> MAKING = EntityDataManager.createKey(ConstructionBlockEntity.class,
    // DataSerializers.BOOLEAN);

    private int despawning = -1;
    public int maxLife = 80;
    private ChunkCoordinates setPos;
    // private EntityLivingBase spawnedBy;
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
            Block setPosBlock = world.getBlock(setPos.posX, setPos.posY, setPos.posZ);
            if (!(setPosBlock instanceof ConstructionBlock) && !(setPosBlock instanceof ConstructionBlockPowder)) {
                setDespawning();
            }
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
            if (setPos != null) {
                if (!getMakingPaste()) {
                    TileEntity te = world.getTileEntity(setPos.posX, setPos.posY, setPos.posZ);
                    if (te instanceof ConstructionBlockTileEntity) {

                        int tempState = te.getBlockMetadata();
                        if (tempState == 0) return;

                        int opacity = world.getBlockLightOpacity(setPos.posX, setPos.posY, setPos.posZ);
                        boolean neighborBrightness = world.getBlock(setPos.posX, setPos.posY, setPos.posZ)
                            .getUseNeighborBrightness();
                        if (opacity == 255 || neighborBrightness) {
                            // IBlockState tempSetBlock = ((ConstructionBlockTileEntity) te).getBlockState();
                            // IBlockState tempActualSetBlock = ((ConstructionBlockTileEntity)
                            // te).getActualBlockState();

                            boolean bright = opacity != 255;
                            int blockDamage = MetadataUtils.getDamageFromConstructionBlockEntityMetadata(
                                new ConstructionBlockEntityMetadata(bright, neighborBrightness));
                            world.setBlock(
                                setPos.posX,
                                setPos.posY,
                                setPos.posZ,
                                ModBlocks.constructionBlock,
                                blockDamage,
                                3);

                            // world.setBlockState(setPos, ModBlocks.constructionBlock.getDefaultState()
                            // .withProperty(ConstructionBlock.BRIGHT, opacity != 255)
                            // .withProperty(ConstructionBlock.NEIGHBOR_BRIGHTNESS, neighborBrightness));

                            te = world.getTileEntity(setPos.posX, setPos.posY, setPos.posZ);
                            if (te instanceof ConstructionBlockTileEntity) {
                                te.blockMetadata = blockDamage;
                                world.setTileEntity(setPos.posX, setPos.posY, setPos.posZ, te);
                                // ((ConstructionBlockTileEntity) te).setBlockState(tempSetBlock);
                                // ((ConstructionBlockTileEntity) te).setActualBlockState(tempActualSetBlock);

                            }
                        }
                    }
                } else if (world.getBlockMetadata(setPos.posX, setPos.posY, setPos.posZ)
                    == ModBlocks.constructionBlockPowder.getDefaultMetadata()) {
                        world.setBlock(setPos.posX, setPos.posY, setPos.posZ, ModBlocks.constructionBlockDense);
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
        // this.dataManager.set(MAKING, paste);
    }

    public boolean getMakingPaste() {
        return false;
        // return this.dataManager.get(MAKING);
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
        // this.dataManager.register(FIXED, new ChunkCoordinates(0,0,0));
        // this.dataManager.register(MAKING, false);
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
