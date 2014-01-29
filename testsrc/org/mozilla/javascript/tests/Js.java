package org.mozilla.javascript.tests;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Js {

	public static void main(String[] args) {

		Context cx = ContextFactory.getGlobal().enterContext();
		try {
			Scriptable scope = cx.initStandardObjects();
			ScriptableObject.putProperty(scope, "myArray", Context.javaToJS(new String[] { "alpha", "bravo" }, scope));
			Object result = cx.evaluateString(scope, "JSON.stringify({ a: myArray })", "source", 1, null);
			System.out.println(result);
		} finally {
			Context.exit();
		}

	}
}
