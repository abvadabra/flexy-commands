package ru.redenergy.rebin;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lwjgl.Sys;
import ru.redenergy.rebin.annotation.Arg;
import ru.redenergy.rebin.annotation.Command;

import java.io.PrintStream;
import java.util.Arrays;

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

    @Test
    public void testVarargInvoke(){
        String message = "Some funny rabbit in the sky";
        commandSet.processCommand(new TestCommandSender(), ImmutableList.builder()
                .add("add", "message")
                .addAll(Arrays.asList(message.split(" ")))
                .build().toArray(new String[8]));
        assertEquals(message, outContent.toString());
    }

    @Test
    public void testTypeInferenced(){
        String[] candidate = {"false", "10", "100000"};
        commandSet.processCommand(new TestCommandSender(), candidate);
        assertEquals(Joiner.on(":").join(candidate), outContent.toString());
    }


    @Test
    public void testSpecialSender(){
        commandSet.processCommand(new TestCommandSender(), new String[0]);
        assertEquals(new TestCommandSender().testUniqueMethid(), outContent.toString());
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

        @Command("add message {*message}")
        public void addMessage(@Arg("message") String message){
            System.out.print(message);
        }

        @Command
        public void specialSender(TestCommandSender testCommandSender){
            System.out.print(testCommandSender.testUniqueMethid());
        }

        @Command("{bol} {number} {long}")
        public void typeInferenced(@Arg("bol") boolean bol, @Arg("number") int number, @Arg("long") long longArg){
            System.out.print(bol + ":" + number + ":" + longArg);
        }
    }

    private static class TestCommandSender implements ICommandSender{

        public String testUniqueMethid(){
            return "I am Test Sender";
        }

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
