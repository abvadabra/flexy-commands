package ru.redenergy.flexy.cmds;

import net.minecraft.command.ICommandSender;
import ru.redenergy.flexy.FlexyCommand;
import ru.redenergy.flexy.annotation.Command;
import ru.redenergy.flexy.annotation.ExceptionHandler;

import static junit.framework.Assert.assertNotNull;

public class TestCommandException extends FlexyCommand{

    @Override
    public String getCommandName() {
        return "test";
    }

    @Command("throw")
    public void shouldThrow(){
        throw new RuntimeException();
    }

    @Command("")
    public void shouldCatch(){
        throw new TestException("Exception during command execution");
    }

    @ExceptionHandler(TestException.class)
    public void handleTestException(ICommandSender sender, TestException ex){
        assertNotNull(sender);
        assertNotNull(ex);
    }

    public static class TestException extends RuntimeException {
        public TestException(String message) {
            super(message);
        }
    }
}
