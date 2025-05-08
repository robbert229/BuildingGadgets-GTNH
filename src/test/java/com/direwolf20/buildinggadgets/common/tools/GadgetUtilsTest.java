package com.direwolf20.buildinggadgets.common.tools;

import com.direwolf20.buildinggadgets.util.NBTTool;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.direwolf20.buildinggadgets.util.ref.NBTKeys;

public class GadgetUtilsTest {
    @Test
    void test() throws NBTException {
        final String snippet = "{stateIntArray:[I;1,2]}";
        NBTTagCompound tag = (NBTTagCompound) JsonToNBT.func_150315_a(snippet);
        int[] result = NBTTool.readIntList(tag.getCompoundTag(NBTKeys.GADGET_STATE_INT_ARRAY));
        Assertions.assertEquals(new int[]{1,2}, result);
    }

    @Test
    void getPOSFromNBTShouldSupportBothCasingsOfXYZ() throws NBTException {
        final String snippet = "{startPos:{X:283,Y:3,Z:59},endPos:{X:282,Y:3,Z:59}}";
        final String snippetLowercase = "{startPos:{x:283,y:3,z:59},endPos:{x:282,y:3,z:59}}";

        NBTTagCompound tag = (NBTTagCompound) JsonToNBT.func_150315_a(snippet);
        ChunkCoordinates startPos = GadgetUtils.getPOSFromNBT(tag, NBTKeys.GADGET_START_POS);
        ChunkCoordinates endPos = GadgetUtils.getPOSFromNBT(tag, NBTKeys.GADGET_END_POS);

        // check the first set of coordinates.
        Assertions.assertNotNull(startPos);

        Assertions.assertEquals(283, startPos.posX);
        Assertions.assertEquals(3, startPos.posY);
        Assertions.assertEquals(59, startPos.posZ);

        Assertions.assertNotNull(endPos);

        Assertions.assertEquals(282, endPos.posX);
        Assertions.assertEquals(3, endPos.posY);
        Assertions.assertEquals(59, endPos.posZ);

        // checks the second set of coordinates against the first.

        NBTTagCompound tag2 = (NBTTagCompound) JsonToNBT.func_150315_a(snippetLowercase);
        ChunkCoordinates startPos2 = GadgetUtils.getPOSFromNBT(tag2, NBTKeys.GADGET_START_POS);
        ChunkCoordinates endPos2 = GadgetUtils.getPOSFromNBT(tag2, NBTKeys.GADGET_END_POS);

        Assertions.assertEquals(startPos, startPos2);
        Assertions.assertEquals(endPos, endPos2);
    }
}
