package ru.redenergy.rebin;

import org.junit.Test;
import ru.redenergy.rebin.resolve.ResolveResult;
import ru.redenergy.rebin.resolve.TemplateResolver;

import java.util.Map;

import static org.junit.Assert.*;

public class TestTemplateResolver {

    TemplateResolver resolver = new TemplateResolver();

    @Test
    public void testSimpleValid(){
        String template = "get test";
        String[] candidate = {"get", "test"};
        assertTrue(resolver.resolve(template, candidate).isSuccess());
    }

    @Test
    public void testSimpleInvalid(){
        String template = "add testEmptyCommand";
        String[] candidate = {"get", "nothing"};
        assertFalse(resolver.resolve(template, candidate).isSuccess());
    }

    @Test
    public void testTemplateArgument(){
        String template = "add {user} to {group}";
        String[] candidate = {"add", "player", "to", "admin"};
        ResolveResult result = resolver.resolve(template, candidate);

        assertTrue(result.isSuccess());
        assertEquals("player", result.getArguments().get("user"));
        assertEquals("admin", result.getArguments().get("group"));
    }

    @Test
    public void testSingleCommand(){
        String template = "";
        String[] candidate = new String[0];
        assertTrue(resolver.resolve(template, candidate).isSuccess());
    }
}
