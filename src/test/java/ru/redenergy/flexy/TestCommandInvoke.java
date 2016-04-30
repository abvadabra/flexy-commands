package ru.redenergy.flexy;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Test;
import ru.redenergy.flexy.cmds.TestCommandInvokeCmds;

import java.io.PrintStream;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class TestCommandInvoke {

    static FlexyCommand commandSet = new TestCommandInvokeCmds();
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

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

    @Test
    public void testDisabled(){
        TestCommandSender sender = new TestCommandSender();
        commandSet.execute(null, sender, new String[]{"test", "disabled"});
        assertEquals(((TextComponentTranslation)sender.lastMessage).getKey(), "commands.generic.notFound");
    }

}
