package com.direwolf20.buildinggadgets;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

public class BuildingGadgetsConfig {

    static final String CATEGORY_ROOT = "general";

    private static final String LANG_KEY_ROOT = "config." + BuildingGadgets.MODID + "." + CATEGORY_ROOT;

    private static final String LANG_KEY_BLACKLIST = LANG_KEY_ROOT + ".subCategoryBlacklist";

    private static final String LANG_KEY_GADGETS = LANG_KEY_ROOT + ".subCategoryGadgets";

    private static final String LANG_KEY_PASTE_CONTAINERS = LANG_KEY_ROOT + ".subCategoryPasteContainers";

    private static final String LANG_KEY_GADGET_BUILDING = LANG_KEY_GADGETS + ".gadgetBuilding";

    private static final String LANG_KEY_GADGET_EXCHANGER = LANG_KEY_GADGETS + ".gadgetExchanger";

    private static final String LANG_KEY_GADGET_DESTRUCTION = LANG_KEY_GADGETS + ".gadgetDestruction";

    private static final String LANG_KEY_GADGET_COPY_PASTE = LANG_KEY_GADGETS + ".gadgetCopyPaste";

    private static final String LANG_KEY_PASTE_CONTAINERS_CAPACITY = LANG_KEY_PASTE_CONTAINERS + ".capacity";

    private static final String LANG_KEY_GADGETS_ENERGY = LANG_KEY_GADGETS + ".energyCost";

    private static final String LANG_KEY_GADGETS_DAMAGE = LANG_KEY_GADGETS + ".damageCost";

    private static final String LANG_KEY_GADGETS_DURABILITY = LANG_KEY_GADGETS + ".durability";

    private static final String LANG_KEY_GADGETS_ENERGY_COMMENT = "The Gadget's Energy cost per Operation";

    private static final String LANG_KEY_GADGETS_DAMAGE_COMMENT = "The Gadget's Damage cost per Operation";

    private static final String LANG_KEY_GADGETS_DURABILITY_COMMENT = "The Gadget's Durability (0 means no durability is used) (Ignored if powered by FE)";

    @Config(modid = BuildingGadgets.MODID, category = "general")
    public static final class GeneralConfig {
        @Config.RangeDouble(min = 1, max = 48)
        @Config.DefaultDouble(32)
        @Config.Name("Max Build Distance")
        @Config.Comment("Defines how far away you can build")
        @Config.LangKey(LANG_KEY_ROOT + ".rayTraceRange")
        public static double rayTraceRange;

        @Config.Name("Powered by Forge Energy")
        @Config.Comment("Set to true for Forge Energy Support, set to False for vanilla Item Damage")
        @Config.RequiresWorldRestart
        @Config.DefaultBoolean(true)
        @Config.LangKey(LANG_KEY_ROOT + ".poweredByFE")
        public static boolean poweredByFE;

        @Config.RequiresMcRestart
        @Config.RequiresWorldRestart
        @Config.Name("Enable Destruction Gadget")
        @Config.Comment("Set to false to disable the Destruction Gadget.")
        @Config.LangKey(LANG_KEY_ROOT + ".enableDestructionGadget")
        @Config.DefaultBoolean(true)
        public static boolean enableDestructionGadget;

        @Config.Name("Default to absolute Coord-Mode")
        @Config.Comment({ "Determines if the Copy/Paste GUI's coordinate mode starts in 'Absolute' mode by default.",
                "Set to true for Absolute, set to False for Relative." })
        @Config.LangKey(LANG_KEY_ROOT + ".absoluteCoordDefault")
        @Config.DefaultBoolean(false)
        public static boolean absoluteCoordDefault;

        @Config.Name("Allow absolute Coord-Mode")
        @Config.Comment("Disable absolute coords-mode for the Copy-Paste gadget")
        @Config.DefaultBoolean(true)
        public static boolean allowAbsoluteCoords;

        @Config.Name("Allow non-Air-Block-Overwrite")
        @Config.Comment({
                "Whether the Building / CopyPaste Gadget can overwrite blocks like water, lava, grass, etc (like a player can).",
                "False will only allow it to overwrite air blocks." })
        @Config.LangKey(LANG_KEY_ROOT + ".canOverwriteBlocks")
        @Config.DefaultBoolean(true)
        public static boolean canOverwriteBlocks;
    }

    @Config(modid = BuildingGadgets.MODID, category = "blacklist")
    public static final class BlacklistConfig {

        @Config.Name("Blacklisted Blocks")
        @Config.Comment({ "All Blocks added to this will be treated similar to TileEntities. Not at all.",
                "Notice that you can use Regular Expressions as defined by Java Patterns to express more complex name combinations.",
                "Use for example \"awfulmod:.*\" to blacklist all awfulmod Blocks." })
        @Config.LangKey(LANG_KEY_BLACKLIST + " + blockBlacklist")
        @Config.DefaultStringList({"minecraft:.*_door.*", "minecraft:piston_head", "astralsorcery:blockflarelight"})
        public static String[] blockBlacklist;
    }

    @Config(modid = BuildingGadgets.MODID, category = "gadgets")
    public static final class GadgetsConfig {

        private GadgetsConfig() {}

        @Config.RangeInt(min = 1, max = 25)
        @Config.Name("Maximum allowed Range")
        @Config.Comment("The max range of the Gadgets")
        @Config.LangKey(LANG_KEY_GADGETS + ".maxRange")
        @Config.DefaultInt(15)
        public static int maxRange;

        @Config.RangeInt(min = 0)
        @Config.Name("Maximum Energy")
        @Config.Comment("The max energy of Building, Exchanging & Copy-Paste Gadget")
        @Config.LangKey(LANG_KEY_GADGETS + ".maxEnergy")
        @Config.DefaultInt(500000)
        public static int maxEnergy;

        @Config(modid = BuildingGadgets.MODID, category = "gadgets.building")
        public static final class GadgetBuildingConfig {

            private GadgetBuildingConfig() {}

            @Config.RangeInt(min = 0, max = 100000)
            @Config.Name("Energy Cost")
            @Config.Comment(LANG_KEY_GADGETS_ENERGY_COMMENT)
            @Config.LangKey(LANG_KEY_GADGETS_ENERGY)
            @Config.DefaultInt(50)
            public static int energyCostBuilder;

            @Config.RangeInt(min = 0, max = 2000)
            @Config.Name("Damage Cost")
            @Config.Comment(LANG_KEY_GADGETS_DAMAGE_COMMENT)
            @Config.LangKey(LANG_KEY_GADGETS_DAMAGE)
            @Config.DefaultInt(1)
            public static int damageCostBuilder;

            @Config.RequiresWorldRestart
            @Config.RangeInt(min = 0, max = 100000)
            @Config.Name("Durability")
            @Config.Comment(LANG_KEY_GADGETS_DURABILITY_COMMENT)
            @Config.LangKey(LANG_KEY_GADGETS_DURABILITY)
            @Config.DefaultInt(10000)
            public static int durabilityBuilder;
        }

        @Config(modid = BuildingGadgets.MODID, category = "gadgets.exchanger")
        public static final class GadgetExchangerConfig {

            private GadgetExchangerConfig() {}

            @Config.RangeInt(min = 0, max = 100000)
            @Config.Name("Energy Cost")
            @Config.Comment(LANG_KEY_GADGETS_ENERGY_COMMENT)
            @Config.LangKey(LANG_KEY_GADGETS_ENERGY)
            @Config.DefaultInt(100)
            public static int energyCostExchanger;

            @Config.RangeInt(min = 0, max = 2000)
            @Config.Name("Damage Cost")
            @Config.Comment(LANG_KEY_GADGETS_DAMAGE_COMMENT)
            @Config.LangKey(LANG_KEY_GADGETS_DAMAGE)
            @Config.DefaultInt(2)
            public static int damageCostExchanger;

            @Config.RequiresWorldRestart
            @Config.RangeInt(min = 0, max = 100000)
            @Config.Name("Durability")
            @Config.Comment(LANG_KEY_GADGETS_DURABILITY_COMMENT)
            @Config.LangKey(LANG_KEY_GADGETS_DURABILITY)
            @Config.DefaultInt(10000)
            public static int durabilityExchanger;
        }

        @Config(modid = BuildingGadgets.MODID, category = "gadgets.destruction")
        public static final class GadgetDestructionConfig {

            private GadgetDestructionConfig() {}

            @Config.RangeInt(min = 0)
            @Config.Name("Maximum Energy")
            @Config.Comment("The max energy of the Destruction Gadget")
            @Config.LangKey(LANG_KEY_GADGET_DESTRUCTION + ".maxEnergy")
            @Config.DefaultInt(1000000)
            public static int energyMaxDestruction;

            @Config.RangeInt(min = 0, max = 100000)
            @Config.Name("Energy Cost")
            @Config.Comment(LANG_KEY_GADGETS_ENERGY_COMMENT)
            @Config.LangKey(LANG_KEY_GADGETS_ENERGY)
            @Config.DefaultInt(200)
            public static int energyCostDestruction;

            @Config.RangeInt(min = 0, max = 2000)
            @Config.Name("Damage Cost")
            @Config.Comment(LANG_KEY_GADGETS_DAMAGE_COMMENT)
            @Config.LangKey(LANG_KEY_GADGETS_DAMAGE)
            @Config.DefaultInt(2)
            public static int damageCostDestruction;

            @Config.RequiresWorldRestart
            @Config.RangeInt(min = 0, max = 100000)
            @Config.Name("Durability")
            @Config.Comment(LANG_KEY_GADGETS_DURABILITY_COMMENT)
            @Config.LangKey(LANG_KEY_GADGETS_DURABILITY)
            @Config.DefaultInt(10000)
            public static int durabilityDestruction;

            @Config.RangeDouble(min = 0)
            @Config.Name("Non-Fuzzy Mode Multiplier")
            @Config.Comment("The cost in energy/durability will increase by this amount when not in fuzzy mode")
            @Config.LangKey(LANG_KEY_GADGET_DESTRUCTION + ".nonfuzzy.multiplier")
            @Config.DefaultInt(2)
            public static double nonFuzzyMultiplier;

            @Config.Name("Non-Fuzzy Mode Enabled")
            @Config.Comment("If enabled, the Destruction Gadget can be taken out of fuzzy mode, allowing only instances of the block "
                + "clicked to be removed (at a higher cost)")
            @Config.LangKey(LANG_KEY_GADGET_DESTRUCTION + ".nonfuzzy.enabled")
            @Config.DefaultBoolean(false)
            public static boolean nonFuzzyEnabled;
        }

        @Config(modid = BuildingGadgets.MODID, category = "gadgets.paste")
        public static final class GadgetCopyPasteConfig {

            private GadgetCopyPasteConfig() {}

            @Config.RangeInt(min = 0, max = 100000)
            @Config.Name("Energy Cost")
            @Config.Comment(LANG_KEY_GADGETS_ENERGY_COMMENT)
            @Config.LangKey(LANG_KEY_GADGETS_ENERGY)
            @Config.DefaultInt(50)
            public static int energyCostCopyPaste;

            @Config.RangeInt(min = 0, max = 2000)
            @Config.Name("Damage Cost")
            @Config.Comment(LANG_KEY_GADGETS_DAMAGE_COMMENT)
            @Config.LangKey(LANG_KEY_GADGETS_DAMAGE)
            @Config.DefaultInt(1)
            public static int damageCostCopyPaste;

            @Config.RequiresWorldRestart
            @Config.RangeInt(min = 0, max = 100000)
            @Config.Name("Durability")
            @Config.Comment(LANG_KEY_GADGETS_DURABILITY_COMMENT)
            @Config.LangKey(LANG_KEY_GADGETS_DURABILITY)
            @Config.DefaultInt(10000)
            public static int durabilityCopyPaste;
        }
    }

    @Config(modid = BuildingGadgets.MODID, category = "paste")
    public static final class PasteConfig {

        private PasteConfig() {}

        @Config.RequiresMcRestart
        @Config.RequiresWorldRestart
        @Config.Name("Enable Construction Paste")
        @Config.Comment("Set to false to disable the recipe for construction paste.")
        @Config.LangKey(LANG_KEY_ROOT + ".paste.enabled")
        @Config.DefaultBoolean(true)
        public static boolean enablePaste;

        @Config.RangeInt(min = 0)
        @Config.Name("Construction Paste Drop Count - Min")
        @Config.Comment("The minimum number of construction paste items dropped by a dense construction block.")
        @Config.LangKey(LANG_KEY_PASTE_CONTAINERS_CAPACITY + ".paste.dropped.min")
        @Config.DefaultInt(1)
        public static int pasteDroppedMin;

        @Config.RangeInt(min = 0)
        @Config.Name("Construction Paste Drop Count - Max")
        @Config.Comment("The maximum number of construction paste items dropped by a dense construction block.")
        @Config.LangKey(LANG_KEY_PASTE_CONTAINERS_CAPACITY + ".paste.dropped.max")
        @Config.DefaultInt(3)
        public static int pasteDroppedMax;

        @Config.RangeInt(min = 1)
        @Config.Comment("The maximum capacity of a tier 1 (iron) Construction Paste Container")
        @Config.Name("T1 Container Capacity")
        @Config.LangKey(LANG_KEY_PASTE_CONTAINERS_CAPACITY + ".t1")
        @Config.DefaultInt(512)
        public static int t1Capacity;

        @Config.RangeInt(min = 1)
        @Config.Comment("The maximum capacity of a tier 2 (gold) Construction Paste Container")
        @Config.Name("T2 Container Capacity")
        @Config.LangKey(LANG_KEY_PASTE_CONTAINERS_CAPACITY + ".t2")
        @Config.DefaultInt(2048)
        public static int t2Capacity;

        @Config.RangeInt(min = 1)
        @Config.Comment("The maximum capacity of a tier 3 (diamond) Construction Paste Container")
        @Config.Name("T3 Container Capacity")
        @Config.LangKey(LANG_KEY_PASTE_CONTAINERS_CAPACITY + ".t3")
        @Config.DefaultInt(8192)
        public static int t3Capacity;
    }

    public static boolean DEVENV = false;

    public static void init() {
        try {
            ConfigurationManager.registerConfig(BlacklistConfig.class);
            ConfigurationManager.registerConfig(GadgetsConfig.class);
            ConfigurationManager.registerConfig(GeneralConfig.class);
            ConfigurationManager.registerConfig(GadgetsConfig.GadgetCopyPasteConfig.class);
            ConfigurationManager.registerConfig(GadgetsConfig.GadgetExchangerConfig.class);
            ConfigurationManager.registerConfig(GadgetsConfig.GadgetBuildingConfig.class);
            ConfigurationManager.registerConfig(GadgetsConfig.GadgetDestructionConfig.class);
            ConfigurationManager.registerConfig(PasteConfig.class);
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }

        try {
            Class.forName("net.minecraft.server.MinecraftServer");
            DEVENV = true;
        } catch (ClassNotFoundException e) {
            // ignored
        }
    }
}
