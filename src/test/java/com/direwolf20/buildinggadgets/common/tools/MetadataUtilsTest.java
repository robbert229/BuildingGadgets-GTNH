package com.direwolf20.buildinggadgets.common.tools;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.direwolf20.buildinggadgets.util.MetadataUtils;

public class MetadataUtilsTest {

    @Test
    void encodeAndDecodeConstructionBlockEntityMetadataWithBrightAndNeighbor() {
        ConstructionBlockEntityMetadata state = new ConstructionBlockEntityMetadata(true, true);
        int damage = MetadataUtils.getDamageFromConstructionBlockEntityMetadata(state);
        ConstructionBlockEntityMetadata decoded = MetadataUtils.getConstructionBlockEntityMetadataFromDamage(damage);
        int decodedDamage = MetadataUtils.getDamageFromConstructionBlockEntityMetadata(decoded);

        assertEquals(decoded, state);
        assertEquals(damage, decodedDamage);
    }

    @Test
    void encodeAndDecodeConstructionBlockEntityMetadataWithBright() {
        ConstructionBlockEntityMetadata state = new ConstructionBlockEntityMetadata(true, false);
        int damage = MetadataUtils.getDamageFromConstructionBlockEntityMetadata(state);
        ConstructionBlockEntityMetadata decoded = MetadataUtils.getConstructionBlockEntityMetadataFromDamage(damage);
        int decodedDamage = MetadataUtils.getDamageFromConstructionBlockEntityMetadata(decoded);

        assertEquals(decoded, state);
        assertEquals(damage, decodedDamage);
    }

    @Test
    void encodeAndDecodeConstructionBlockEntityMetadataWithNeighbor() {
        ConstructionBlockEntityMetadata state = new ConstructionBlockEntityMetadata(false, true);
        int damage = MetadataUtils.getDamageFromConstructionBlockEntityMetadata(state);
        ConstructionBlockEntityMetadata decoded = MetadataUtils.getConstructionBlockEntityMetadataFromDamage(damage);
        int decodedDamage = MetadataUtils.getDamageFromConstructionBlockEntityMetadata(decoded);

        assertEquals(decoded, state);
        assertEquals(damage, decodedDamage);
    }

    @Test
    void encodeAndDecodeConstructionBlockEntityMetadataWithNone() {
        ConstructionBlockEntityMetadata state = new ConstructionBlockEntityMetadata(false, false);
        int damage = MetadataUtils.getDamageFromConstructionBlockEntityMetadata(state);
        ConstructionBlockEntityMetadata decoded = MetadataUtils.getConstructionBlockEntityMetadataFromDamage(damage);
        int decodedDamage = MetadataUtils.getDamageFromConstructionBlockEntityMetadata(decoded);

        assertEquals(decoded, state);
        assertEquals(damage, decodedDamage);
    }
}
