package com.direwolf20.buildinggadgets.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import com.direwolf20.buildinggadgets.BuildingGadgets;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class BlockModBase extends Block {
    private final String name;
    private final String texturePrefix;

    public BlockModBase(Material material, float hardness, String name) {
        super(material);

        this.name = name;
        this.texturePrefix = String.join(":", BuildingGadgets.MODID, name);

        init(this, hardness, name);
    }

    public static void init(Block block, float hardness, String name) {
        block.setHardness(hardness);
        block.setCreativeTab(BuildingGadgets.BUILDING_CREATIVE_TAB);
        block.setBlockName(String.join(".", BuildingGadgets.MODID, name));
        block.setBlockTextureName(String.join(".", BuildingGadgets.MODID, name));
    }

    protected String getName() {
        return this.name;
    }

    protected String getTexturePrefix() {
        return this.texturePrefix;
    }

    public static enum BlockSide {
        Bottom(0), Top(1), North(2), South(3), West(4), East(5);

        private final int value;

        private BlockSide(int value) {
            this.value = value;
        }

        public static BlockSide fromValue(int value) {
            return switch (value) {
                case 0 -> Bottom;
                case 1 -> Top;
                case 2 -> North;
                case 3 -> South;
                case 4 -> West;
                case 5 -> East;
                default -> North;
            };
        }

        public int getValue() {
            return value;
        }
    }

    public String iconNameFromSide(int side) {
        return switch (side) {
            case 0 -> "bottom";
            case 1 -> "top";
            case 2 -> "north";
            case 3 -> "south";
            case 4 -> "west";
            case 5 -> "east";
            default -> "";
        };
    }

    /**
     * Determines the block facing direction based on the player's position and orientation.
     *
     * @param placer The entity that placed the block.
     * @return The metadata value representing the direction the block should face.
     */
    public static int determineBlockFacing(EntityLivingBase placer) {
        int direction = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

        return switch (direction) {
            case 0 -> 2; // North
            case 1 -> 5; // East
            case 2 -> 3; // South
            case 3 -> 4; // West

            default -> 2; // Default to North if something goes wrong
        };
    }

    //    @SideOnly(Side.CLIENT)
    public void initModel() {
        initModel(this);
    }

    public static void initModel(Block block) {

    }
}
