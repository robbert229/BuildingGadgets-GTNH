package com.direwolf20.buildinggadgets.common.integration;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.direwolf20.buildinggadgets.common.integration.IntegrationHandler.IIntegratedMod;
import com.direwolf20.buildinggadgets.common.integration.IntegrationHandler.Phase;
import com.direwolf20.buildinggadgets.common.tools.NetworkIO.Operation;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;

public abstract class NetworkProvider implements IIntegratedMod {
    private boolean isLoaded = true;
    private static final Set<NetworkProvider> PROVIDERS = new HashSet<>();

    @Override
    public void initialize(Phase phase) {
        if (phase == Phase.PRE_INIT) {
            isLoaded = true;
            PROVIDERS.add(this);
        }
    }

    @Nullable
    protected abstract IInventory getWrappedNetworkInternal(TileEntity te, EntityPlayer player, Operation operation);

    @Nullable
    private IInventory getWrappedNetworkIfLoaded(TileEntity te, EntityPlayer player, Operation operation) {
        return !isLoaded ? null : getWrappedNetworkInternal(te, player, operation);
    }

    @Nullable
    public static IInventory getWrappedNetwork(TileEntity te, EntityPlayer player, Operation operation) {
        IInventory network = null;
        for (NetworkProvider provider : PROVIDERS) {
            network = provider.getWrappedNetworkIfLoaded(te, player, operation);
            if (network != null) break;
        }
        return network;
    }
}