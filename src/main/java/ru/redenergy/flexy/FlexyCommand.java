package ru.redenergy.flexy;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import ru.redenergy.flexy.permission.IProvider;

public abstract class FlexyCommand extends CommandBase {

    private static IProvider provider = new IProvider.VanillaProvider();

    private final CommandBackend backend = new CommandBackend(this);

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        try {
            backend.resolveExecute(sender, args);
        } catch (Exception exception){
            exception.printStackTrace();

            TextComponentTranslation msg = new TextComponentTranslation("commands.generic.exception");
            msg.getChatStyle().setColor(TextFormatting.RED);
            sender.addChatMessage(msg);
        }
    }

    public static void register(FlexyCommand set){
        ((CommandHandler) FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager()).registerCommand(set);
    }

    public static IProvider getProvider() {
        return provider;
    }

    public static void setProvider(IProvider provider) {
        FlexyCommand.provider = provider;
    }
}