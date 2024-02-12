package com.direwolf20.buildinggadgets.common.blocks;

// import com.direwolf20.buildinggadgets.common.entities.ConstructionBlockEntity;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.direwolf20.buildinggadgets.common.tools.WorldUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ConstructionBlockPowder extends BlockFalling {

    public ConstructionBlockPowder() {
        super(Material.sand);
        BlockModBase.init(this, 0.5F, "constructionblockpowder");
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        BlockModBase.initModel(this);
    }

    // @Override
    // @SideOnly(Side.CLIENT)
    // public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
    // super.addInformation(stack, player, list, b);
    // list.add(I18n.format("tooltip.constructionblockpowder.helptext"));
    // }

    // @Override
    // public void onEndFalling(World worldIn, ChunkCoordinates pos, IBlockState p_176502_3_, IBlockState p_176502_4_) {
    // if (p_176502_4_.getMaterial().isLiquid()) {
    // worldIn.spawnEntity(new ConstructionBlockEntity(worldIn, pos, true));
    // }
    // }

    private boolean tryTouchWater(IBlockAccess worldIn, ChunkCoordinates pos) {
        boolean flag = false;

        for (EnumFacing enumfacing : EnumFacing.values()) {
            if (enumfacing != EnumFacing.DOWN) {
                ChunkCoordinates blockpos = WorldUtils.offset(pos, enumfacing, 1);

                if (worldIn.getBlock(blockpos.posX, blockpos.posY, blockpos.posZ)
                    .getMaterial() == Material.water) {
                    flag = true;
                    break;
                }
            }
        }

        if (flag) {
            // TODO(johnrowl) re-enable tile entity detection logic here.
            // if (worldIn.getEntitiesWithinAABB(ConstructionBlockEntity.class, new AxisAlignedBB(pos.getX() - 0.5,
            // pos.getY() - 0.5, pos.getZ() - 0.5, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)).isEmpty()) {
            // worldIn.spawnEntity(new ConstructionBlockEntity(worldIn, pos, true));
            // }
        }

        return flag;
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     *
     * @param world The world
     * @param x     The x position of this block instance
     * @param y     The y position of this block instance
     * @param z     The z position of this block instance
     * @param tileX The x position of the tile that changed
     * @param tileY The y position of the tile that changed
     * @param tileZ The z position of the tile that changed
     */
    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        if (!this.tryTouchWater(world, new ChunkCoordinates(x, y, z))) {
            super.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
        }
    }

    /**
     * Called after the block is set in the Chunk data, but before the Tile Entity is set
     */
    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        if (!this.tryTouchWater(worldIn, new ChunkCoordinates(x, y, z))) {
            super.onBlockAdded(worldIn, x, y, z);
        }
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        return false;
    }

}
