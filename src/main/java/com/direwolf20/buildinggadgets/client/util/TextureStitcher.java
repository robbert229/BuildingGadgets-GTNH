package com.direwolf20.buildinggadgets.client.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

/**
 * Utility class for stitching multiple sprite images into sprite sheets.
 * When a sprite sheet reaches the maximum size, a new sheet is created.
 */
public class TextureStitcher {
    
    /**
     * Maximum size for a sprite sheet in pixels (width and height).
     * A sprite sheet of 8192x8192 can contain 128x128 sprites of 64x64 pixels each.
     */
    public static final int MAX_SPRITE_SHEET_SIZE = 8192;
    
    private final int spriteSize;
    private final List<BufferedImage> spriteSheets;
    private final Map<String, SpriteLocation> spriteLocations;
    private int currentSheetIndex;
    private int currentX;
    private int currentY;
    private int spritesPerRow;
    
    /**
     * Represents the location of a sprite within the sprite sheets.
     */
    public static class SpriteLocation {
        public final int sheetIndex;
        public final int x;
        public final int y;
        public final int width;
        public final int height;
        
        public SpriteLocation(int sheetIndex, int x, int y, int width, int height) {
            this.sheetIndex = sheetIndex;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        /**
         * Adds sprite location information to a JSON object.
         */
        public void addToJson(JsonObject json) {
            json.addProperty("sprite_sheet_index", sheetIndex);
            json.addProperty("sprite_x", x);
            json.addProperty("sprite_y", y);
            json.addProperty("sprite_width", width);
            json.addProperty("sprite_height", height);
        }
    }
    
    /**
     * Creates a new TextureStitcher with the specified sprite size.
     * 
     * @param spriteSize The size (width and height) of each sprite in pixels
     */
    public TextureStitcher(int spriteSize) {
        if (spriteSize <= 0 || spriteSize > MAX_SPRITE_SHEET_SIZE) {
            throw new IllegalArgumentException("Sprite size must be between 1 and " + MAX_SPRITE_SHEET_SIZE);
        }
        
        this.spriteSize = spriteSize;
        this.spriteSheets = new ArrayList<>();
        this.spriteLocations = new HashMap<>();
        this.currentSheetIndex = 0;
        this.currentX = 0;
        this.currentY = 0;
        this.spritesPerRow = MAX_SPRITE_SHEET_SIZE / spriteSize;
        
        // Create the first sprite sheet
        createNewSpriteSheet();
    }
    
    /**
     * Creates a new sprite sheet and adds it to the list.
     */
    private void createNewSpriteSheet() {
        BufferedImage newSheet = new BufferedImage(
            MAX_SPRITE_SHEET_SIZE, 
            MAX_SPRITE_SHEET_SIZE, 
            BufferedImage.TYPE_INT_ARGB
        );
        spriteSheets.add(newSheet);
    }
    
    /**
     * Adds a sprite to the sprite sheets.
     * 
     * @param spriteId Unique identifier for the sprite
     * @param sprite The sprite image to add
     * @return The location where the sprite was placed
     * @throws IllegalArgumentException if sprite size doesn't match or sprite is null
     */
    public SpriteLocation addSprite(String spriteId, BufferedImage sprite) {
        if (sprite == null) {
            throw new IllegalArgumentException("Sprite cannot be null");
        }
        
        if (sprite.getWidth() != spriteSize || sprite.getHeight() != spriteSize) {
            throw new IllegalArgumentException(
                String.format("Sprite size must be %dx%d, got %dx%d", 
                    spriteSize, spriteSize, sprite.getWidth(), sprite.getHeight())
            );
        }
        
        // Check if sprite already exists
        if (spriteLocations.containsKey(spriteId)) {
            return spriteLocations.get(spriteId);
        }
        
        // Check if we need to move to the next row
        if (currentX + spriteSize > MAX_SPRITE_SHEET_SIZE) {
            currentX = 0;
            currentY += spriteSize;
        }
        
        // Check if we need to create a new sprite sheet
        if (currentY + spriteSize > MAX_SPRITE_SHEET_SIZE) {
            currentSheetIndex++;
            currentX = 0;
            currentY = 0;
            createNewSpriteSheet();
        }
        
        // Draw the sprite onto the current sheet
        BufferedImage currentSheet = spriteSheets.get(currentSheetIndex);
        Graphics2D g2d = currentSheet.createGraphics();
        g2d.drawImage(sprite, currentX, currentY, null);
        g2d.dispose();
        
        // Record the sprite location
        SpriteLocation location = new SpriteLocation(
            currentSheetIndex, 
            currentX, 
            currentY, 
            spriteSize, 
            spriteSize
        );
        spriteLocations.put(spriteId, location);
        
        // Move to the next position
        currentX += spriteSize;
        
        return location;
    }
    
    /**
     * Gets the location of a previously added sprite.
     * 
     * @param spriteId The sprite identifier
     * @return The sprite location, or null if not found
     */
    public SpriteLocation getSpriteLocation(String spriteId) {
        return spriteLocations.get(spriteId);
    }
    
    /**
     * Gets the sprite sheet at the specified index.
     * 
     * @param index The sheet index
     * @return The sprite sheet image
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public BufferedImage getSpriteSheet(int index) {
        return spriteSheets.get(index);
    }
    
    /**
     * Gets the number of sprite sheets created.
     * 
     * @return The number of sprite sheets
     */
    public int getSpriteSheetCount() {
        return spriteSheets.size();
    }
    
    /**
     * Gets all sprite sheets.
     * 
     * @return List of all sprite sheets
     */
    public List<BufferedImage> getAllSpriteSheets() {
        return new ArrayList<>(spriteSheets);
    }
    
    /**
     * Gets the maximum sprite sheet size.
     * 
     * @return The maximum size in pixels
     */
    public static int getMaxSpriteSheetSize() {
        return MAX_SPRITE_SHEET_SIZE;
    }
    
    /**
     * Gets the maximum number of sprites that can fit in one sheet.
     * 
     * @return The maximum number of sprites per sheet
     */
    public int getMaxSpritesPerSheet() {
        return spritesPerRow * spritesPerRow;
    }
}
