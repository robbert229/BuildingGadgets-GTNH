package com.direwolf20.buildinggadgets.common.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.BuildingGadgetsConfig.GeneralConfig;
import com.direwolf20.buildinggadgets.BuildingGadgetsConfig.PasteConfig;
import com.direwolf20.buildinggadgets.common.blocks.ModBlocks;
import com.direwolf20.buildinggadgets.common.items.ModItems;

import cpw.mods.fml.common.registry.GameRegistry;

public final class ModRecipes {

    private ModRecipes() {}

    public static void init() {
        RecipeSorter.register(
            BuildingGadgets.MODID + ":construction_paste_container_upgrade",
            ConstructionPasteContainerUpgradeRecipe.class,
            RecipeSorter.Category.SHAPED,
            "after:minecraft:shaped");

        registerGadgetRecipes();
        registerTemplateManagerRecipe();

        if (GeneralConfig.enableDestructionGadget) {
            registerDestructionGadgetRecipe();
        }

        if (PasteConfig.enablePaste) {
            registerConstructionPasteRecipes();
        }
    }

    private static void registerGadgetRecipes() {
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                new ItemStack(ModItems.gadgetBuilding),
                "iri",
                "drd",
                "ili",
                'i',
                "ingotIron",
                'r',
                "dustRedstone",
                'd',
                "gemDiamond",
                'l',
                "gemLapis"));

        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                new ItemStack(ModItems.gadgetExchanger),
                "iri",
                "dld",
                "ili",
                'i',
                "ingotIron",
                'r',
                "dustRedstone",
                'd',
                "gemDiamond",
                'l',
                "gemLapis"));

        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                new ItemStack(ModItems.gadgetCopyPaste),
                "iri",
                "ere",
                "ili",
                'i',
                "ingotIron",
                'r',
                "dustRedstone",
                'e',
                "gemEmerald",
                'l',
                "gemLapis"));
    }

    private static void registerDestructionGadgetRecipe() {
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                new ItemStack(ModItems.gadgetDestruction),
                "iri",
                "ere",
                "ili",
                'i',
                "ingotIron",
                'r',
                "dustRedstone",
                'e',
                Items.ender_pearl,
                'l',
                "gemLapis"));
    }

    private static void registerTemplateManagerRecipe() {
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                new ItemStack(ModBlocks.templateManager),
                "iri",
                "ere",
                "ili",
                'i',
                "ingotGold",
                'r',
                "dustRedstone",
                'e',
                "gemEmerald",
                'l',
                "gemLapis"));
    }

    private static void registerConstructionPasteRecipes() {
        GameRegistry.addRecipe(
            new ShapedOreRecipe(
                new ItemStack(ModItems.constructionPasteContainer),
                "iii",
                "iri",
                "iii",
                'i',
                "ingotIron",
                'r',
                ModItems.constructionPaste));

        GameRegistry.addRecipe(
            new ConstructionPasteContainerUpgradeRecipe(
                new ItemStack(ModItems.constructionPasteContainerT2),
                "cgc",
                "ggg",
                "cgc",
                'g',
                "ingotGold",
                'c',
                new ItemStack(ModItems.constructionPasteContainer, 1, OreDictionary.WILDCARD_VALUE)));

        GameRegistry.addRecipe(
            new ConstructionPasteContainerUpgradeRecipe(
                new ItemStack(ModItems.constructionPasteContainerT3),
                "cgc",
                "ggg",
                "cgc",
                'g',
                "gemDiamond",
                'c',
                new ItemStack(ModItems.constructionPasteContainerT2, 1, OreDictionary.WILDCARD_VALUE)));

        GameRegistry.addRecipe(
            new ShapelessOreRecipe(
                new ItemStack(ModBlocks.constructionBlockPowder),
                new ItemStack(Blocks.sand, 1, 0),
                "gemLapis",
                Items.clay_ball));
    }
}
