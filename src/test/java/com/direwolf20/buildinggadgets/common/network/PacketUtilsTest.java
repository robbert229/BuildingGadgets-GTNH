package com.direwolf20.buildinggadgets.common.network;

import static com.direwolf20.buildinggadgets.common.network.PacketUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PacketUtilsTest {

    @Test
    void testActualPayload() {
        String original = "{stateIntArray:[I;1,2],dim:0,posIntArray:[I;16711680,0],startPos:{X:283,Y:3,Z:59},mapIntState:[{mapSlot:1s,mapState:{Properties:{snowy:\"false\"},Name:\"minecraft:grass\"}},{mapSlot:2s,mapState:{Name:\"minecraft:lapis_block\"}}],endPos:{X:282,Y:3,Z:59}}";
        byte[] encoded = compress(original);
        String decompressed = decompress(encoded);
        assertEquals(original, decompressed);
    }

    @Test
    public void testSimpleStringCompression() {
        String original = "Hello, world!";
        byte[] compressed = compress(original);
        String decompressed = decompress(compressed);

        assertEquals(original, decompressed);
    }

    @Test
    public void testEmptyStringCompression() {
        String original = "";
        byte[] compressed = compress(original);
        String decompressed = decompress(compressed);

        assertEquals(original, decompressed);
        assertTrue(compressed.length > 0); // Even empty strings result in a small compressed array
    }

    @Test
    public void testLongStringCompression() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            buffer.append("Lorem ipsum dolor sit amet, consectetur adipiscing elit. ");
        }

        String original = buffer.toString();
        byte[] compressed = compress(original);
        String decompressed = decompress(compressed);

        assertEquals(original, decompressed);
        assertTrue(compressed.length < original.getBytes().length); // Ensure some compression occurred
    }

    @Test
    public void testUnicodeStringCompression() {
        String original = "こんにちは世界🌏🚀"; // Japanese + emojis
        byte[] compressed = compress(original);
        String decompressed = decompress(compressed);

        assertEquals(original, decompressed);
    }

    @Test
    public void testNullStringCompressionThrows() {
        assertThrows(NullPointerException.class, () -> compress(null));
    }

    @Test
    public void testDecompressionWithInvalidDataThrows() {
        byte[] invalidData = new byte[] { 0x00, 0x01, 0x02 };
        assertThrows(RuntimeException.class, () -> decompress(invalidData));
    }
}
