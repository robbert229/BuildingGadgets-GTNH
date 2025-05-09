package com.direwolf20.buildinggadgets.common.network;

import java.nio.charset.StandardCharsets;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.annotation.Nonnull;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PacketUtils {

    public static byte[] compress(String data) {
        byte[] input = data.getBytes(StandardCharsets.UTF_8);
        Deflater deflater = new Deflater();
        deflater.setInput(input);
        deflater.finish();

        byte[] buffer = new byte[1024];
        try (java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream()) {
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            return outputStream.toByteArray();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decompress(byte[] compressedData) {
        Inflater inflater = new Inflater();
        inflater.setInput(compressedData);

        byte[] buffer = new byte[1024];
        try (java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream()) {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            return outputStream.toString(java.nio.charset.StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decompress(ByteBuf buf) {
        var data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        return decompress(data);
    }

    /// Returns true if the packet is too large to be used.
    public static boolean isPacketTooLarge(@Nonnull IMessage message) {
        var buffer = Unpooled.buffer();
        message.toBytes(buffer);

        var tooLarge = buffer.readableBytes() >= Short.MAX_VALUE - 200;
        buffer.release();

        return tooLarge;
    }
}
