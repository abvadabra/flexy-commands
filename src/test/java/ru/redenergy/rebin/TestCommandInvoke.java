package ru.redenergy.rebin;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.redenergy.rebin.annotation.Arg;
import ru.redenergy.rebin.annotation.Command;
import ru.redenergy.rebin.annotation.Flag;

import java.io.PrintStream;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
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
        commandSet.execute(null, new TestCommandSender(), new String[]{"empty"});
        assertEquals("empty", outContent.toString());
    }

    @Test
    public void testParametrizedInvoke(){
        String arg1 = "bob";
        String arg2 = "admin";
        commandSet.execute(null, new TestCommandSender(), new String[]{"add", arg1, "to", arg2});
        assertEquals("test" + ":" + arg1 + ":" + arg2, outContent.toString());
    }

    @Test
    public void testVarargInvoke(){
        String message = "Some funny rabbit in the sky";
        commandSet.execute(null, new TestCommandSender(), ImmutableList.builder()
                .add("add", "message")
                .addAll(Arrays.asList(message.split(" ")))
                .build().toArray(new String[8]));
        assertEquals(message, outContent.toString());
    }

    @Test
    public void testTypeInferenced(){
        String[] candidate = {"false", "10", "100000"};
        commandSet.execute(null, new TestCommandSender(), candidate);
        assertEquals(Joiner.on(":").join(candidate), outContent.toString());
    }


    @Test
    public void testSpecialSender(){
        commandSet.execute(null, new TestCommandSender(), new String[0]);
        assertEquals(new TestCommandSender().testUniqueMethid(), outContent.toString());
    }

    @Test
    public void testFlagged(){
        commandSet.execute(null, new TestCommandSender(), new String[]{"action", "-a", "player", "123", "123", "123"});
        assertEquals("ACTIONPLAYER", outContent.toString());
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
            System.out.print(sender.getName() + ":" + user + ":" + group);
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

        @Command("{action} {player} {something} {garbage} {bla}")
        public void commandFlagged(@Arg("action") String action, @Arg("player") String player, @Flag("-a") boolean big){
            if(big){
                System.out.print(action.toUpperCase() + player.toUpperCase());
            } else {
                System.out.print(action.toLowerCase() + player.toLowerCase());
            }
        }
    }

    private static class TestCommandSender implements ICommandSender{

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
        public void addChatMessage(ITextComponent component) {}

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
}
