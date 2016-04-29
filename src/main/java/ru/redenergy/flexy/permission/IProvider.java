package ru.redenergy.flexy.permission;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.api.entity.living.player.Player;
import ru.redenergy.vault.ForgeVault;

public interface IProvider {

    boolean hasPermission(EntityPlayerMP player, String permission);

    /**
     * Vanilla provider, will be used in single player or on pure forge server without sponge
     */
    class VanillaProvider implements IProvider{

        @Override
        public boolean hasPermission(EntityPlayerMP player, String permission) {
            return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().canSendCommands(player.getGameProfile());
        }
    }

    /**
     * Will be used if forge-vault is loaded
     */
    class BukkitProvider implements IProvider {

        @Override
        public boolean hasPermission(EntityPlayerMP player, String permission) {
            return ForgeVault.getPermission().has((String)null, player.getName(), permission);
        }
    }

    /**
     * Will be used if sponge is loaded
     */
    class SpongeProvider implements IProvider {

        @Override
        public boolean hasPermission(EntityPlayerMP player, String permission) {
            return ((Player)player).hasPermission(permission);
        }
    }
}
