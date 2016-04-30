package ru.redenergy.flexy;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Test;
import ru.redenergy.flexy.cmds.TestCommandParametersCmds;

import java.io.PrintStream;

import static junit.framework.Assert.assertEquals;

public class TestParametersInvocation {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private FlexyCommand command = new TestCommandParametersCmds();

    @Before
    public void connectOutPipe(){
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testSimple(){
        command.execute(null, new TestCommandSender(), new String[]{"--p", "42", "simple"});
        assertEquals("Val:42", outContent.toString());
    }

    @Test
    public void testOptional(){
        command.execute(null, new TestCommandSender(), new String[]{"optional", "--p", "42"});
        assertEquals("Optional val:42", outContent.toString());
    }
}
