package ru.redenergy.flexy;

import org.junit.Test;
import ru.redenergy.flexy.resolve.ResolveResult;
import ru.redenergy.flexy.resolve.TemplateResolver;

import java.util.Arrays;

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
    public void testParametrizedInvalid(){
        String template = "do {something} bad";
        String[] candidate = {"do", "bla", "bla"};
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

    @Test
    public void testVararg(){
        String template = "set {target} {*values}";
        String[] candidate ={"set", "Player", "VAL=1", "VAL=2", "VAL=3", "VAL=4"};
        ResolveResult result = resolver.resolve(template, candidate);

        assertTrue(result.isSuccess());
        assertEquals("Player", result.getArguments().get("target"));
        assertEquals("VAL=1 VAL=2 VAL=3 VAL=4", result.getArguments().get("values"));
    }

    @Test
    public void testFlagParse(){
        String template = "do something";
        String[] candidate = {"do", "-a", "something", "-b"};
        ResolveResult result = resolver.resolve(template, candidate, Arrays.asList("-a", "-b"));

        assertTrue(result.isSuccess());
        assertEquals(Arrays.asList("-a", "-b"), result.getFoundFlags());
    }
}
