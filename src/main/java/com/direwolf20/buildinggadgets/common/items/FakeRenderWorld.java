package com.direwolf20.buildinggadgets.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
//import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.EnumFacing;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
//import net.minecraft.world.WorldType;
//import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class FakeRenderWorld implements IBlockAccess {
    @Override
    public Block getBlock(int p_147439_1_, int p_147439_2_, int p_147439_3_) {
        return null;
    }

    @Override
    public TileEntity getTileEntity(int x, int y, int z) {
        return null;
    }

    @Override
    public int getLightBrightnessForSkyBlocks(int p_72802_1_, int p_72802_2_, int p_72802_3_, int p_72802_4_) {
        return 0;
    }

    @Override
    public int getBlockMetadata(int p_72805_1_, int p_72805_2_, int p_72805_3_) {
        return 0;
    }

    @Override
    public int isBlockProvidingPowerTo(int x, int y, int z, int directionIn) {
        return 0;
    }

    @Override
    public boolean isAirBlock(int x, int y, int z) {
        return false;
    }

    @Override
    public BiomeGenBase getBiomeGenForCoords(int x, int z) {
        return null;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public boolean extendedLevelsInChunkCache() {
        return false;
    }

    @Override
    public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
        return false;
    }
    private Map<ChunkCoordinates, Block> posMap = new HashMap<ChunkCoordinates, Block>();
    private IBlockAccess realWorld;

    public void setState(IBlockAccess rWorld, Block setBlock, ChunkCoordinates coordinate) {
        this.realWorld = rWorld;
        posMap.put(coordinate, setBlock);
    }

//    @Override
//    @SideOnly(Side.CLIENT)
//    public int getCombinedLight(int x, int y, int z, int lightValue) {
//        return realWorld.getCombinedLight(pos, lightValue);
//    }
//
//    @Nullable
//    @Override
//    public TileEntity getTileEntity(ChunkCoordinates pos) {
//        return realWorld.getTileEntity(pos);
//    }
//
//
//    @Override
//    public IBlockState getBlockState(ChunkCoordinates pos) {
//        return posMap.containsKey(pos) ? posMap.get(pos) : realWorld.getBlockState(pos);
//    }
//
//    @Override
//    public boolean isAirBlock(ChunkCoordinates pos) {
//        if (posMap.containsKey(pos)) {
//            return posMap.get(pos).equals(Blocks.AIR.getDefaultState());
//        }
//        return realWorld.isAirBlock(pos);
//    }
//
//    @Override
//    public Biome getBiome(ChunkCoordinates pos) {
//        return realWorld.getBiome(pos);
//    }
//
//    @Override
//    public int getStrongPower(ChunkCoordinates pos, EnumFacing direction) {
//        return 0;
//    }
//
//    @Override
//    public WorldType getWorldType() {
//        return realWorld.getWorldType();
//    }
//
//    @Override
//    public boolean isSideSolid(ChunkCoordinates pos, EnumFacing side, boolean _default) {
//        return getBlockState(pos).isSideSolid(this, pos, side);
//    }
}
