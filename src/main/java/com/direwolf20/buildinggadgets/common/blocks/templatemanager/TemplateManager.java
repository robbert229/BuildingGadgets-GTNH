package com.direwolf20.buildinggadgets.common.blocks.templatemanager;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.blocks.BlockModBase;
import com.direwolf20.buildinggadgets.common.items.ITemplate;
import com.direwolf20.buildinggadgets.common.network.PacketBlockMap;
import com.direwolf20.buildinggadgets.common.network.PacketHandler;

public class TemplateManager extends BlockModBase {

    private static final int GUI_ID = 1;
    private IIcon iconFront, iconSide, iconBottom, iconTop;

    public TemplateManager() {
        super(Material.rock, 2.0f, "templatemanager");
        setHardness(2.0f);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack itemStack) {
        int facing = BlockModBase.determineBlockFacing(placer);
        world.setBlockMetadataWithNotify(x, y, z, facing, 2);
    }

    // @SideOnly(Side.CLIENT)
    // public void initModel() {
    // ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new
    // ModelResourceLocation(getRegistryName(), "inventory"));
    // }

    // @Override
    // public IBlockState getStateForPlacement(World worldIn, ChunkCoordinates pos, EnumFacing facing, float hitX, float
    // hitY, float hitZ, int meta, EntityLivingBase placer) {
    // return this.getDefaultState().withProperty(FACING_HORIZ, placer.getHorizontalFacing().getOpposite());
    // }

    // @Override
    // protected BlockStateContainer createBlockState() {
    // return new BlockStateContainer(this, new IProperty[]{FACING});
    // }

    // @Override
    // public IBlockState getStateFromMeta(int meta) {
    //
    // EnumFacing enumfacing = EnumFacing.getFront(meta);
    // if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
    // enumfacing = EnumFacing.NORTH;
    // }
    // return this.getDefaultState().withProperty(FACING, enumfacing);
    // }

    // @Override
    // public int getMetaFromState(IBlockState state) {
    // return state.getValue(FACING).getIndex();
    // }

    @Override
    public boolean hasTileEntity(int state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World worldin, int metadata) {
        return new TemplateManagerTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        }

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TemplateManagerTileEntity)) {
            return false;
        }

        var container = ((TemplateManagerTileEntity) te).getContainer(player);

        for (int i = 0; i <= 1; i++) {
            ItemStack itemStack = container.getSlot(i)
                .getStack();
            if (itemStack == null || !(itemStack.getItem() instanceof ITemplate template)) {
                continue;
            }

            String UUID = template.getUUID(itemStack);
            if (UUID == null) {
                continue;
            }

            NBTTagCompound tagCompound = template.getWorldSave(world)
                .getCompoundFromUUID(UUID);
            if (tagCompound != null) {
                PacketHandler.INSTANCE.sendTo(new PacketBlockMap(tagCompound), (EntityPlayerMP) player);
            }
        }

        player.openGui(BuildingGadgets.instance, GUI_ID, world, x, y, z);
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block blockBroken, int meta) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TemplateManagerTileEntity templateManager) {
            for (int i = 0; i < templateManager.getSizeInventory(); i++) {
                ItemStack stack = templateManager.getStackInSlot(i);
                if (stack != null) {
                    spawnItemStack(world, new ChunkCoordinates(x, y, z), stack);
                }
            }
        }

        super.breakBlock(world, x, y, z, blockBroken, meta);
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.iconSide = reg.registerIcon(String.join("_", this.getTexturePrefix()));
        this.iconBottom = reg.registerIcon(String.join("_", this.getTexturePrefix(), "bottom"));
        this.iconTop = reg.registerIcon(String.join("_", this.getTexturePrefix(), "top"));
        this.iconFront = reg.registerIcon(String.join("_", this.getTexturePrefix(), "front"));
    }

    public BlockSide getSideFromMeta(int meta) {
        return BlockSide.fromValue(meta);
    }

    @Override
    public IIcon getIcon(int sideInt, int meta) {
        var front = getSideFromMeta(meta);
        var side = BlockSide.fromValue(sideInt);

        if (side == front) {
            return this.iconFront;
        }

        if (side == BlockSide.Top) {
            return this.iconTop;
        }

        if (side == BlockSide.Bottom) {
            return this.iconBottom;
        }

        return this.iconSide;
    }

    public static void spawnItemStack(World world, ChunkCoordinates coordinates, ItemStack stack) {
        if (stack != null && stack.stackSize > 0) {
            float f = 0.5F;
            double offsetX = world.rand.nextFloat() * 0.8F + 0.1F;
            double offsetY = world.rand.nextFloat() * 0.8F + 0.1F;
            double offsetZ = world.rand.nextFloat() * 0.8F + 0.1F;

            while (stack.stackSize > 0) {
                int splitStackSize = world.rand.nextInt(21) + 10;
                if (splitStackSize > stack.stackSize) {
                    splitStackSize = stack.stackSize;
                }

                stack.stackSize -= splitStackSize;
                ItemStack splitStack = new ItemStack(stack.getItem(), splitStackSize, stack.getItemDamage());
                if (stack.hasTagCompound()) {
                    splitStack.setTagCompound(
                        (NBTTagCompound) stack.getTagCompound()
                            .copy());
                }

                EntityItem entityItem = new EntityItem(
                    world,
                    coordinates.posX + offsetX,
                    coordinates.posY + offsetY,
                    coordinates.posZ + offsetZ,
                    splitStack);
                entityItem.motionX = world.rand.nextGaussian() * 0.05D;
                entityItem.motionY = world.rand.nextGaussian() * 0.05D + 0.2D;
                entityItem.motionZ = world.rand.nextGaussian() * 0.05D;

                world.spawnEntityInWorld(entityItem);
            }
        }
    }
}
