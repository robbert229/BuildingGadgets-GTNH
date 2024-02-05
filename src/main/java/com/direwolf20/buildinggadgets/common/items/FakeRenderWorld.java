package com.direwolf20.buildinggadgets.common.items;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class FakeRenderWorld implements IBlockAccess {
    private Map<ChunkCoordinates, IBlockState> posMap = new HashMap<ChunkCoordinates, IBlockState>();
    private IBlockAccess realWorld;


    public void setState(IBlockAccess rWorld, IBlockState setBlock, ChunkCoordinates coordinate) {
        this.realWorld = rWorld;
        posMap.put(coordinate, setBlock);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getCombinedLight(ChunkCoordinates pos, int lightValue) {
        return realWorld.getCombinedLight(pos, lightValue);
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(ChunkCoordinates pos) {
        return realWorld.getTileEntity(pos);
    }


    @Override
    public IBlockState getBlockState(ChunkCoordinates pos) {
        return posMap.containsKey(pos) ? posMap.get(pos) : realWorld.getBlockState(pos);
    }

    @Override
    public boolean isAirBlock(ChunkCoordinates pos) {
        if (posMap.containsKey(pos)) {
            return posMap.get(pos).equals(Blocks.AIR.getDefaultState());
        }
        return realWorld.isAirBlock(pos);
    }

    @Override
    public Biome getBiome(ChunkCoordinates pos) {
        return realWorld.getBiome(pos);
    }

    @Override
    public int getStrongPower(ChunkCoordinates pos, EnumFacing direction) {
        return 0;
    }

    @Override
    public WorldType getWorldType() {
        return realWorld.getWorldType();
    }

    @Override
    public boolean isSideSolid(ChunkCoordinates pos, EnumFacing side, boolean _default) {
        return getBlockState(pos).isSideSolid(this, pos, side);
    }
}
