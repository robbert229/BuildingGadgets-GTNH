package com.direwolf20.buildinggadgets.common.config;

import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.ImmutableList;

public final class PatternList {

    private final ImmutableList<Pattern> patterns;

    @Nonnull
    public static String getName(Item item) {
        String name = Item.itemRegistry.getNameForObject(item);
        if (name == null) throw new IllegalArgumentException(
            "A registry name for the following IForgeRegistryEntry (" + item.getClass()
                .getName() + ") could not be found: " + item);

        return name;
    }

    @Nonnull
    static String[] getNames(Item... blocks) {
        return Stream.of(blocks)
            .map(PatternList::getName)
            .toArray(String[]::new);
    }

    public static PatternList ofResourcePattern(String... regex) {
        return of(Stream.of(regex), true);
    }

    public static PatternList ofResourcePattern(Collection<String> regex) {
        return of(regex.stream(), true);
    }

    public static PatternList of(Stream<String> strings, boolean convertToResourceLocations) {
        if (convertToResourceLocations) // this is done, so that users can continue omitting the Minecraft namespace,
            // etc.
            strings = strings.map(ResourceLocation::new)
                .map(ResourceLocation::toString);
        return new PatternList(
            ImmutableList.copyOf(
                strings.map(Pattern::compile)
                    .collect(Collectors.toList())));

    }

    private PatternList(ImmutableList<Pattern> patterns) {
        this.patterns = patterns;
    }

    public boolean containsOre(ItemStack stack) {
        return !(stack != null && stack.getItem() != null)
            && (contains(stack.getItem()) || IntStream.of(OreDictionary.getOreIDs(stack))
                .mapToObj(OreDictionary::getOreName)
                .anyMatch(this::contains));
    }

    public boolean contains(Item object) {
        return contains(getName(object));
    }

    public boolean contains(String s) {
        return patterns.stream()
            .anyMatch(
                p -> p.matcher(s)
                    .matches());
    }

    public String[] toArray() {
        return patterns.stream()
            .map(Pattern::toString)
            .toArray(String[]::new);
    }
}
