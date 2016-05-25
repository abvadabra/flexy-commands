package ru.redenergy.flexy;

import org.junit.Test;
import ru.redenergy.flexy.cmds.TestCommandException;

public class TextExceptionHandler {

    private TestCommandException command = new TestCommandException();

    @Test
    public void testCatch(){
        command.execute(null, new TestCommandSender(), new String[]{});
    }

    @Test()
    public void testThrow(){
        command.execute(null, new TestCommandSender(), new String[]{"throw"});
    }
}
