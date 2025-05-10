package com.direwolf20.buildinggadgets.common.items;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

import com.direwolf20.buildinggadgets.util.datatypes.BlockState;

public class FakeBuilderWorld implements IBlockAccess {

    private Set<ChunkCoordinates> positions;
    private BlockState state;
    private World realWorld;

    public void setWorldAndState(World rWorld, BlockState setBlock, Set<ChunkCoordinates> coordinates) {
        this.realWorld = rWorld;
        positions = coordinates;
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        ChunkCoordinates pos = new ChunkCoordinates(x, y, z);
        return positions.contains(pos) ? state.block() : Blocks.air;
    }

    @Override
    public TileEntity getTileEntity(int x, int y, int z) {
        return null;
    }

    @Override
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int light) {
        return realWorld.getLightBrightnessForSkyBlocks(x, y, z, light);
    }

    @Override
    public int getBlockMetadata(int x, int y, int z) {
        ChunkCoordinates pos = new ChunkCoordinates(x, y, z);
        return positions.contains(pos) ? state.metadata() : 0;
    }

    @Override
    public int isBlockProvidingPowerTo(int x, int y, int z, int directionIn) {
        return 0;
    }

    @Override
    public boolean isAirBlock(int x, int y, int z) {
        return !positions.contains(new ChunkCoordinates(x, y, z));
    }

    @Override
    public BiomeGenBase getBiomeGenForCoords(int x, int z) {
        return realWorld.getBiomeGenForCoords(x, z);
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
        return getBlock(x, y, z).isSideSolid(this, x, y, z, side);
    }

    public WorldType getWorldType() {
        return realWorld.getWorldInfo()
            .getTerrainType();
    }
}
