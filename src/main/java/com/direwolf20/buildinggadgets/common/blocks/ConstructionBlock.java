package com.direwolf20.buildinggadgets.common.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.cricketcraft.chisel.api.IFacade;
import com.direwolf20.buildinggadgets.common.items.FakeRenderWorld;
import com.direwolf20.buildinggadgets.common.items.ModItems;
import com.direwolf20.buildinggadgets.util.ChunkCoordinateUtils;
import com.direwolf20.buildinggadgets.util.MetadataUtils;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// @Optional.Interface(iface = "team.chisel.ctm.api.IFacade", modid = "ctm-api")
@Optional.Interface(iface = "com.cricketcraft.chisel.api.IFacade", modid = "ChiselAPI")
public class ConstructionBlock extends BlockModBase implements IFacade {

    public ConstructionBlock() {
        super(Material.rock, 2F, "constructionblock");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        super.initModel();
        // StateMapperBase ignoreState = new StateMapperBase() {
        // @Override
        // protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) {
        // return ConstructionBakedModel.modelFacade;
        // }
        // };
        // ModelLoader.setCustomStateMapper(this, ignoreState);
    }

    @Override
    public Item getItemDropped(int state, Random rand, int fortune) {
        return ModItems.constructionPaste;
    }

    @Override
    public boolean canSilkHarvest() {
        return false;
    }

    @Override
    public boolean hasTileEntity(int state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World worldIn, int state) {
        return new ConstructionBlockTileEntity();
    }

    private static ConstructionBlockTileEntity getTE(World world, ChunkCoordinates pos) {
        return (ConstructionBlockTileEntity) world.getTileEntity(pos.posX, pos.posY, pos.posZ);
    }

    private static ConstructionBlockTileEntity getTE(World world, int x, int y, int z) {
        return getTE(world, new ChunkCoordinates(x, y, z));
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        ConstructionBlockTileEntity te = getTE(world, x, y, z);
        ItemStack heldItem = player.getHeldItem();
        if (heldItem == null || heldItem.getItem() == null) {
            return false;
        }

        var block = Block.getBlockFromItem(heldItem.getItem());
        te.blockMetadata = block.getDamageValue(world, x, y, z);

        world.setTileEntity(x, y, z, te);
        // te.setBlockState(newState);
        // te.setActualBlockState(newState);
        return true;
        // System.out.println("Failed: " + newState + ":" + te.getBlockState() + ":" + world.isRemote + ":" +
        // te.getActualBlockState());

    }

    // @Override
    // public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    // IExtendedBlockState extendedBlockState = (IExtendedBlockState) super.getExtendedState(state, world, pos);
    // IBlockState mimicBlock = getActualMimicBlock(world, pos);
    // if (mimicBlock != null) {
    // FakeRenderWorld fakeRenderWorld = new FakeRenderWorld();
    // fakeRenderWorld.setState(world, mimicBlock, pos);
    // IBlockState extState = mimicBlock.getBlock().getExtendedState(mimicBlock, fakeRenderWorld, pos);
    // //ConstructionID mimicID = new ConstructionID(mimicBlock);
    // return extendedBlockState.withProperty(FACADE_ID, mimicBlock).withProperty(FACADE_EXT_STATE, extState);
    // }
    // return extendedBlockState;
    // }
    //

    private Block getActualMimicBlock(IBlockAccess blockAccess, ChunkCoordinates coordinates) {
        try {
            TileEntity te = blockAccess.getTileEntity(coordinates.posX, coordinates.posY, coordinates.posZ);
            if (te instanceof ConstructionBlockTileEntity) {
                return te.getBlockType();
            }
            return null;
        } catch (Exception var8) {
            return null;
        }
    }

    @Nullable
    private Block getActualMimicBlock(IBlockAccess blockAccess, int x, int y, int z) {
        return getActualMimicBlock(blockAccess, new ChunkCoordinates(x, y, z));
    }
    //
    // @Override
    // protected BlockStateContainer createBlockState() {
    // IProperty<?>[] listedProperties = new IProperty<?>[]{BRIGHT, NEIGHBOR_BRIGHTNESS};
    // IUnlistedProperty<?>[] unlistedProperties = new IUnlistedProperty<?>[]{FACADE_ID, FACADE_EXT_STATE};
    // return new ExtendedBlockState(this, listedProperties, unlistedProperties);
    // }
    //
    // @Override
    // @Deprecated
    // public EnumBlockRenderType getRenderType(IBlockState state) {
    // return EnumBlockRenderType.MODEL;
    // }
    //
    // @Override
    // @SideOnly(Side.CLIENT)
    // public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
    // return true; // delegated to FacadeBakedModel#getQuads
    // }

    // @Override
    // @SideOnly(Side.CLIENT)
    // public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
    // Block mimicBlock = getActualMimicBlock(blockAccess, x,y,z);
    // return mimicBlock == null ? true : mimicBlock.shouldSideBeRendered(blockAccess, x,y,z , side);
    // }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    //
    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        return MetadataUtils.getConstructionBlockEntityMetadataFromDamage(world.getBlockMetadata(x, y, z))
            .bright() ? 0 : 255;
    }

    //
    // @Override
    // public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
    // IBlockState mimicBlock = getActualMimicBlock(world, pos);
    // return mimicBlock == null ? true : mimicBlock.getBlock().doesSideBlockRendering(mimicBlock, world, pos, face);
    // }
    //
    // @SideOnly(Side.CLIENT)
    // public void initColorHandler(BlockColors blockColors) {
    // blockColors.registerBlockColorHandler((state, world, pos, tintIndex) -> {
    // IBlockState mimicBlock = getActualMimicBlock(world, pos);
    // return mimicBlock != null ? blockColors.colorMultiplier(mimicBlock, world, pos, tintIndex) : -1;
    // }, this);
    // }
    //
    // @Override
    // @Deprecated
    // public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    // IBlockState mimicBlock = getActualMimicBlock(worldIn, pos);
    // try {
    // return mimicBlock == null ? BlockFaceShape.SOLID : mimicBlock.getBlock().getBlockFaceShape(worldIn, mimicBlock,
    // pos, face);
    // } catch (Exception var8) {
    // return BlockFaceShape.SOLID;
    // }
    // }
    //
    // @Override
    // @Deprecated
    // public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
    // List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
    // IBlockState mimicBlock = getActualMimicBlock(worldIn, pos);
    // if (mimicBlock == null) {
    // super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
    // } else {
    // try {
    // mimicBlock.getBlock().addCollisionBoxToList(mimicBlock, worldIn, pos, entityBox, collidingBoxes, entityIn,
    // isActualState);
    // } catch (Exception var8) {
    // super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
    // }
    // }
    // }
    //
    // @Override
    // @Nullable
    // @Deprecated
    // public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
    // IBlockState mimicBlock = getActualMimicBlock(worldIn, pos);
    // if (mimicBlock == null) {
    // return super.getBoundingBox(blockState, worldIn, pos);
    // }
    // try {
    // return mimicBlock.getBlock().getBoundingBox(mimicBlock, worldIn, pos);
    // } catch (Exception var8) {
    // return super.getBoundingBox(blockState, worldIn, pos);
    // }
    // }
    //
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        return shouldSideBeRendered(worldIn, new ChunkCoordinates(x, y, z), side);
    }

    public boolean shouldSideBeRendered(IBlockAccess worldIn, ChunkCoordinates pos, int side) {
        FakeRenderWorld fakeWorld = new FakeRenderWorld();
        EnumFacing parsedSide = EnumFacing.getFront(side);

        Block mimicBlock = getActualMimicBlock(worldIn, pos);
        if (mimicBlock == null) {
            return super.shouldSideBeRendered(worldIn, pos.posX, pos.posY, pos.posZ, side);
        }

        ChunkCoordinates offsetCoordinates = ChunkCoordinateUtils.offset(pos, parsedSide, 1);
        Block sideBlockState = worldIn.getBlock(offsetCoordinates.posX, offsetCoordinates.posY, offsetCoordinates.posZ);
        if (sideBlockState.equals(ModBlocks.constructionBlock)) {
            if (!(getActualMimicBlock(worldIn, ChunkCoordinateUtils.offset(pos, parsedSide, 1)) == null)) {
                sideBlockState = getActualMimicBlock(worldIn, ChunkCoordinateUtils.offset(pos, parsedSide, 1));
            }
        }

        fakeWorld.setState(worldIn, mimicBlock, pos);
        fakeWorld.setState(worldIn, sideBlockState, ChunkCoordinateUtils.offset(pos, parsedSide, 1));

        try {
            return mimicBlock.shouldSideBeRendered(fakeWorld, pos.posX, pos.posY, pos.posZ, side);
        } catch (Exception var8) {
            return super.shouldSideBeRendered(worldIn, pos.posX, pos.posY, pos.posZ, side);
        }
    }

    //
    // @Override
    // @Deprecated
    // public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    // IBlockState mimicBlock = getActualMimicBlock(source, pos);
    // if (mimicBlock == null) {
    // return super.getBoundingBox(state, source, pos);
    // }
    // try {
    // return mimicBlock.getBlock().getBoundingBox(mimicBlock, source, pos);
    // } catch (Exception var8) {
    // return super.getBoundingBox(state, source, pos);
    // }
    // }
    //
    // @Override
    // @Deprecated
    // @SideOnly(Side.CLIENT)
    // public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
    // IBlockState mimicBlock = getActualMimicBlock(worldIn, pos);
    // if (mimicBlock == null) {
    // return super.getSelectedBoundingBox(state, worldIn, pos);
    // }
    // try {
    // return mimicBlock.getBlock().getSelectedBoundingBox(mimicBlock, worldIn, pos);
    // } catch (Exception var8) {
    // return super.getSelectedBoundingBox(state, worldIn, pos);
    // }
    // }
    //

    // @Override
    // public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
    // Block mimicBlock = getActualMimicBlock(world, x, y, z);
    // if (mimicBlock == null) {
    // return super.isNormalCube(world, x, y, z);
    // }
    // try {
    // return mimicBlock.isNormalCube(world, x, y, z);
    // } catch (Exception var8) {
    // return super.isNormalCube(world, x, y, z);
    // }
    // }

    //
    // @Override
    // @Deprecated
    // @SideOnly(Side.CLIENT)
    // public float getAmbientOcclusionLightValue(IBlockState state) {
    // Boolean bright = state.getValue(ConstructionBlock.BRIGHT);
    // Boolean neighborBrightness = state.getValue(ConstructionBlock.NEIGHBOR_BRIGHTNESS);
    // if (bright || neighborBrightness) {
    // return 1f;
    // }
    // return 0.2f;
    // }
    //
    @Override
    @Deprecated
    public boolean getUseNeighborBrightness() {
        // TODO(johnrowl) investigate neighbor brightness.
        // return state.getValue(ConstructionBlock.NEIGHBOR_BRIGHTNESS);
        return false;
    }

    /**
     * The below implements support for CTM's Connected Textures to work properly
     *
     * @param world IBlockAccess
     * @param x
     * @param y
     * @param z
     * @param side  EnumFacing
     * @return IBlockState
     * @deprecated see {@link IFacade#getFacade(IBlockAccess, int, int, int, int)}
     */
    @Override
    public Block getFacade(IBlockAccess world, int x, int y, int z, int side) {
        Block mimicBlock = getActualMimicBlock(world, x, y, z);
        return mimicBlock != null ? mimicBlock : world.getBlock(x, y, z);
    }

    @Override
    public int getFacadeMetadata(IBlockAccess world, int x, int y, int z, int side) {
        return 0;
    }
}
