package ru.redenergy.flexy;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Test;
import ru.redenergy.flexy.cmds.TestCommandParametersCmds;

import java.io.PrintStream;

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
    }

    @Test
    public void testOptional(){
        command.execute(null, new TestCommandSender(), new String[]{"optional", "--p", "42"});
    }

    @Test
    public void testOptionalTyped(){
        int val = 42;
        command.execute(null, new TestCommandSender(), new String[]{"typed", "--p", String.valueOf(val)});
    }

    @Test
    public void testParAbsent(){
        command.execute(null, new TestCommandSender(), new String[]{"absent"});
    }
}
