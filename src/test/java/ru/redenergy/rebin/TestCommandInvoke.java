package ru.redenergy.rebin;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lwjgl.Sys;
import ru.redenergy.rebin.annotation.Arg;
import ru.redenergy.rebin.annotation.Command;

import java.io.PrintStream;

import static org.junit.Assert.*;
public class TestCommandInvoke {

    static CommandSet commandSet = new TestCommandSet();
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeClass
    public static void setup(){
        commandSet.collectCommands();
    }

    @Before
    public void connectOutPipe(){
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testEmptyInvoke(){
        commandSet.processCommand(new TestCommandSender(), new String[]{"empty"});
        assertEquals("empty", outContent.toString());
    }

    @Test
    public void testParametrizedInvoke(){
        String arg1 = "bob";
        String arg2 = "admin";
        commandSet.processCommand(new TestCommandSender(), new String[]{"add", arg1, "to", arg2});
        assertEquals("test" + ":" + arg1 + ":" + arg2, outContent.toString());
    }

    private static class TestCommandSet extends CommandSet {
        @Override
        public String getCommandName() {
            return "test";
        }

        @Override
        public String getCommandUsage(ICommandSender p_71518_1_) {
            return "/test <args>";
        }

        @Command("add {user} to {group}")
        public void testCommandParameterized(@Arg("group") String group, @Arg("user") String user, ICommandSender sender){
            System.out.print(sender.getCommandSenderName() + ":" + user + ":" + group);
        }

        @Command("empty")
        public void testEmptyCommand(){
            System.out.print("empty");
        }
    }

    private static class TestCommandSender implements ICommandSender{

        @Override
        public String getCommandSenderName() {
            return "test";
        }

        @Override
        public IChatComponent func_145748_c_() {
            return null;
        }

        @Override
        public void addChatMessage(IChatComponent p_145747_1_) {
            System.out.println(p_145747_1_.toString());
        }

        @Override
        public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_) {
            return true;
        }

        @Override
        public ChunkCoordinates getPlayerCoordinates() {
            return null;
        }

        @Override
        public World getEntityWorld() {
            return null;
        }
    }
}
