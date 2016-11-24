package org.mozilla.javascript.tests;

import java.util.ArrayList;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ScriptableObject;

import junit.framework.TestCase;

public class ArrayListConsumerTest extends TestCase {
    
    public void testNativeArray() {
        final ArrayList<String> arr = new ArrayList<>();
        arr.add("alpha");
        arr.add("bravo");
        arr.add("charlie");
        final String script = "var str = ''; arr.forEach(function(elem) { str += elem; }); str;";

        final ContextAction action = new ContextAction() {
            @Override
            public Object run(final Context _cx) {
                final ScriptableObject scope = _cx.initStandardObjects();
                ScriptableObject.putProperty(scope, "arr", Context.javaToJS(arr, scope));
                final Object result = _cx.evaluateString(scope, script, "test script", 0, null);
                assertEquals("alphabravocharlie", Context.toString(result));
                return null;
            }
        };
        Utils.runWithAllOptimizationLevels(action);
    }
    
    public void testJsArray() {
        final String script = "var str = '';\n" +
                "var arr = [ 'alpha', 'bravo', 'charlie' ];\n" +
                "arr.forEach(function(elem) { str += elem; });\n" +
                "str;";

        final ContextAction action = new ContextAction() {
            @Override
            public Object run(final Context _cx) {
                final ScriptableObject scope = _cx.initStandardObjects();
                final Object result = _cx.evaluateString(scope, script, "test script", 0, null);
                assertEquals("alphabravocharlie", Context.toString(result));
                return null;
            }
        };
        Utils.runWithAllOptimizationLevels(action);
    }
}