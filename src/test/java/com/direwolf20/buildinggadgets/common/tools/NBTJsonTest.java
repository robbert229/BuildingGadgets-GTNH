package com.direwolf20.buildinggadgets.common.tools;

import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;

import org.apache.logging.log4j.core.helpers.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.direwolf20.buildinggadgets.util.NBTJson;
import com.direwolf20.buildinggadgets.util.ref.NBTKeys;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class NBTJsonTest {

    @Test
    void toNbtShouldParseIntArrays() throws NBTException {
        final String snippet = "{stateIntArray:[I;1,2]}";
        JsonElement parsed = new JsonParser().parse(snippet);
        NBTTagCompound tag = (NBTTagCompound) NBTJson.toNbt(parsed);
        NBTTagIntArray result = (NBTTagIntArray) tag.getTag(NBTKeys.GADGET_STATE_INT_ARRAY);
        Assertions.assertArrayEquals(new int[] { 1, 2 }, result.func_150302_c());
    }

    @Test
    void toNbtShouldParseSimpleExample() throws NBTException {
        final String snippet = "{\n" + "    stateIntArray:[I;1,2],\n"
            + "    dim:0,\n"
            + "    posIntArray:[I;16711680,0],\n"
            + "    startPos:{X:283,Y:3,Z:59},\n"
            + "    mapIntState:[\n"
            + "        {\n"
            + "            mapSlot:1s,\n"
            + "            mapState:{\n"
            + "            Properties:{snowy:\"false\"},\n"
            + "            Name:\"minecraft:grass\"\n"
            + "            }\n"
            + "        },\n"
            + "        {\n"
            + "            mapSlot:2s,\n"
            + "            mapState:{Name:\"minecraft:lapis_block\"}\n"
            + "        }\n"
            + "    ],\n"
            + "    endPos:{X:282,Y:3,Z:59}\n"
            + "}";

        JsonElement parsed = new JsonParser().parse(snippet);
        NBTTagCompound tag = (NBTTagCompound) NBTJson.toNbt(parsed);
        NBTTagIntArray result = (NBTTagIntArray) tag.getTag(NBTKeys.GADGET_STATE_INT_ARRAY);
        Assert.isNotNull(result, "GADGET_STATE_INT_ARRAY");
    }
}
