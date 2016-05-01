package ru.redenergy.flexy.cmds;

import com.google.common.base.Optional;
import net.minecraft.command.ICommandSender;
import ru.redenergy.flexy.FlexyCommand;
import ru.redenergy.flexy.annotation.Command;
import ru.redenergy.flexy.annotation.Par;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class TestCommandParametersCmds extends FlexyCommand {
    @Override
    public String getCommandName() {
        return "test";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/test";
    }

    @Command("simple")
    public void simple(@Par("--p") int val){
        assertEquals(42, val);
    }

    @Command("optional")
    public void optonalPar(@Par("--p") Optional val){
        assertTrue(val.isPresent());
        assertEquals("42", val.get());
    }

    @Command("typed")
    public void optionalParTyped(@Par(value = "--p", type = int.class) Optional<Integer> val){
        assertTrue(val.isPresent());
        assertTrue(val.get() instanceof Integer);
        assertEquals(42, val.get().intValue());
    }

    @Command("absent")
    public void parAbsent(@Par("--p") Optional val){
        assertFalse(val.isPresent());
    }
}
