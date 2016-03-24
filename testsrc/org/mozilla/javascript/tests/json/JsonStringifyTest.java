package org.mozilla.javascript.tests.json;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.json.JsonParser;
import org.mozilla.javascript.json.JsonParser.ParseException;

public class JsonStringifyTest {

    private Context cx;
    private Scriptable scope;
    
    @Before
    public void setUp() {
        cx = Context.enter();
        scope = cx.initStandardObjects();
    }

    @After
    public void tearDown() {
        Context.exit();
    }

    @Test
    public void writeEnum() throws Exception {
		ScriptableObject.putProperty(scope, "myArray", Context.javaToJS(new Object[] { MYENUM.ALPHA, MYENUM.BRAVO, MYENUM.CHARLIE }, scope));
		cx.evaluateString(scope, "JSON.stringify(myArray)", "[inline command]", 1, null);
    }
    
    @Test
    public void writeUnicodeLineSeps() throws Exception {
		ScriptableObject.putProperty(scope, "myString", Context.javaToJS("Hello\u2028World\u2029", scope));
		cx.evaluateString(scope, "JSON.stringify(myString)", "[inline command]", 1, null);
    }
    
    public enum MYENUM { ALPHA, BRAVO, CHARLIE };
}
