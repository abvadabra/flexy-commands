package ru.redenergy.flexy;

import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class TestCommandSender implements ICommandSender {

    public ITextComponent lastMessage;

    public String testUniqueMethid(){
        return "I am Test Sender";
    }

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(getName());
    }

    @Override
    public void addChatMessage(ITextComponent component) {
        lastMessage = component;
    }

    @Override
    public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
        return true;
    }

    @Override
    public BlockPos getPosition() {
        return null;
    }

    @Override
    public Vec3d getPositionVector() {
        return null;
    }

    @Override
    public World getEntityWorld() {
        return null;
    }

    @Override
    public Entity getCommandSenderEntity() {
        return null;
    }

    @Override
    public boolean sendCommandFeedback() {
        return false;
    }

    @Override
    public void setCommandStat(CommandResultStats.Type type, int amount) {
    }

    @Override
    public MinecraftServer getServer() {
        return null;
    }
}
