package com.direwolf20.buildinggadgets.util.datatypes;

import javax.annotation.Nullable;

import com.github.bsideup.jabel.Desugar;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@Desugar
public record BlockState(Block block, int metadata) {

    @Nullable
    public static BlockState getBlockState(World world, int x, int y, int z) {
        var block = world.getBlock(x, y, z);
        if (block == null) {
            return null;
        }

        var metadata = world.getBlockMetadata(x, y, z);

        return new BlockState(block, metadata);
    }

    @Nullable
    public static BlockState getBlockState(World world, ChunkCoordinates coordinates) {
        return getBlockState(world, coordinates.posX, coordinates.posY, coordinates.posZ);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        BlockState blockMeta = (BlockState) obj;

        if (metadata != blockMeta.metadata) {
            return false;
        }

        return block == blockMeta.block;
    }

    public boolean isAir(IBlockAccess world, int x, int y, int z) {
        return this.isAir();
    }

    public boolean isAir(IBlockAccess world, ChunkCoordinates coordinates) {
        return this.isAir();
    }

    public boolean isAir(BlockState blockState, IBlockAccess world, ChunkCoordinates coordinates) {
        return this.isAir();
    }

    public float getBlockHardness(World world, ChunkCoordinates coordinates) {
        return this.block.getBlockHardness(world, coordinates.posX, coordinates.posY, coordinates.posZ);
    }

    public boolean isAir() {
        return block.getMaterial() == Material.air;
    }

    public Material getMaterial() {
        return block.getMaterial();
    }
}
