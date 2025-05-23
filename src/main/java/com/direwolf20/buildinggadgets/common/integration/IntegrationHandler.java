package com.direwolf20.buildinggadgets.common.integration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.Set;

import com.direwolf20.buildinggadgets.BuildingGadgets;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class IntegrationHandler {

    private static final Set<IIntegratedMod> MODS = new HashSet<>();

    public static void preInit(FMLPreInitializationEvent event) {
        for (ASMData asmData : event.getAsmData()
            .getAll(IntegratedMod.class.getName())) {
            String name = asmData.getClassName();
            try {
                if (Loader.isModLoaded(
                    (String) asmData.getAnnotationInfo()
                        .get("value"))) {
                    IIntegratedMod mod = Class.forName(name)
                        .asSubclass(IIntegratedMod.class)
                        .newInstance();
                    mod.initialize(Phase.PRE_INIT);
                    MODS.add(mod);
                }
            } catch (Exception e) {
                BuildingGadgets.LOG.error(String.format("Integration with %s failed", name), e);
            }
        }
    }

    public static void init() {
        MODS.forEach(mod -> mod.initialize(Phase.INIT));
    }

    public static void postInit() {
        MODS.forEach(mod -> mod.initialize(Phase.POST_INIT));
    }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface IntegratedMod {

        String value();
    }

    public static enum Phase {
        PRE_INIT,
        INIT,
        POST_INIT;
    }

    public static interface IIntegratedMod {

        void initialize(Phase phase);
    }
}
