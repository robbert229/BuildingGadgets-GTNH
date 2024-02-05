package com.direwolf20.buildinggadgets.building.coreTests;

import com.direwolf20.buildinggadgets.common.building.IBlockProvider;
import com.direwolf20.buildinggadgets.common.building.placement.SingleTypeProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ChunkCoordinates;
import org.junit.jupiter.api.*;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class TranslationWrapperTest {

    private final Random random = new Random();

    private void wrapperShouldTranslateParameterByAddingMethodGetTranslation(ChunkCoordinates translation, ChunkCoordinates access, ChunkCoordinates expected) {
        ChunkCoordinates[] request = new ChunkCoordinates[1];
        request[0] = new ChunkCoordinates(0,0,0);

        IBlockProvider handle = new SingleTypeProvider(null) {
            @Override
            public IBlockState at(ChunkCoordinates pos) {
                request[0] = pos;
                return super.at(pos);
            }
        };

        handle.translate(translation).at(access);

        assertEquals(expected, request[0]);
    }

    private void wrapperShouldAccumulateAllTranslations(ChunkCoordinates... translations) {
        IBlockProvider wrapper = new SingleTypeProvider(null);
        ChunkCoordinates totalTranslation = new ChunkCoordinates(0,0,0);
        for (ChunkCoordinates translation : translations) {
            wrapper = wrapper.translate(translation);
            totalTranslation = totalTranslation.add(translation);
        }

        assertEquals(totalTranslation, wrapper.getTranslation());
    }

    @Test
    void wrapperShouldTranslateParameterByAddingMethodGetTranslationPositiveCase() {
        ChunkCoordinates translation = new ChunkCoordinates(8, 8, 8);
        ChunkCoordinates access = new ChunkCoordinates(16, 16, 16);
        ChunkCoordinates expected = new ChunkCoordinates(24, 24, 24);
        wrapperShouldTranslateParameterByAddingMethodGetTranslation(translation, access, expected);
    }

    @Test
    void wrapperShouldTranslateParameterByAddingMethodGetTranslationNegativeCase() {
        ChunkCoordinates translation = new ChunkCoordinates(-8, -8, -8);
        ChunkCoordinates access = new ChunkCoordinates(-16, -16, -16);
        ChunkCoordinates expected = new ChunkCoordinates(-24, -24, -24);
        wrapperShouldTranslateParameterByAddingMethodGetTranslation(translation, access, expected);
    }

    @Test
    void wrapperShouldTranslateParameterByAddingMethodGetTranslationMixedCase() {
        ChunkCoordinates translation = new ChunkCoordinates(-2, -2, -2);
        ChunkCoordinates access = new ChunkCoordinates(18, 18, 18);
        ChunkCoordinates expected = new ChunkCoordinates(16, 16, 16);
        wrapperShouldTranslateParameterByAddingMethodGetTranslation(translation, access, expected);
    }

    @Test
    void wrapperShouldAccumulateAllTranslationsCaseRandomMixedRandom() {
        ChunkCoordinates[] translations = new ChunkCoordinates[4];
        for (int i = 0; i < 4; i++) {
            int x = random.nextInt(65) - 32;
            int y = random.nextInt(65) - 32;
            int z = random.nextInt(65) - 32;
            translations[i] = new ChunkCoordinates(x, y, z);
        }
        wrapperShouldAccumulateAllTranslations(translations);
    }

}
