/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */


package org.mozilla.javascript.optimizer;

import org.mozilla.javascript.*;
import org.mozilla.javascript.xml.XMLObject;

public final class OptRuntime extends ScriptRuntime
{

    public static final Double zeroObj = new Double(0.0);
    public static final Double oneObj = new Double(1.0);
    public static final Double minusOneObj = new Double(-1.0);

    /**
     * Implement ....() call shrinking optimizer code.
     */
    public static Object call0(Callable fun, Scriptable thisObj,
                               Context cx, Scriptable scope)
    {
        return fun.call(cx, scope, thisObj, ScriptRuntime.emptyArgs);
    }

    /**
     * Implement ....(arg) call shrinking optimizer code.
     */
    public static Object call1(Callable fun, Scriptable thisObj, Object arg0,
                               Context cx, Scriptable scope)
    {
        return fun.call(cx, scope, thisObj, new Object[] { arg0 } );
    }

    /**
     * Implement ....(arg0, arg1) call shrinking optimizer code.
     */
    public static Object call2(Callable fun, Scriptable thisObj,
                               Object arg0, Object arg1,
                               Context cx, Scriptable scope)
    {
        return fun.call(cx, scope, thisObj, new Object[] { arg0, arg1 });
    }

    /**
     * Implement ....(arg0, arg1, ...) call shrinking optimizer code.
     */
    public static Object callN(Callable fun, Scriptable thisObj,
                               Object[] args,
                               Context cx, Scriptable scope)
    {
        return fun.call(cx, scope, thisObj, args);
    }

    /**
     * Implement name(args) call shrinking optimizer code.
     */
    public static Object callName(Object[] args, String name,
                                  Context cx, Scriptable scope)
    {
        Callable f = getNameFunctionAndThis(name, cx, scope);
        Scriptable thisObj = lastStoredScriptable(cx);
        return f.call(cx, scope, thisObj, args);
    }

    /**
     * Implement name(args) call shrinking optimizer code.
     */
    public static Object callName(Object[] args, String name,  int optCallLevel,
                                  int optCallIndex, Context cx, Scriptable scope)
    {
        Callable f = getNameFunctionAndThis(name, cx, scope, optCallLevel, optCallIndex);
        Scriptable thisObj = lastStoredScriptable(cx);
        return f.call(cx, scope, thisObj, args);
    }

    /**
     * Implement name() call shrinking optimizer code.
     */
    public static Object callName0(String name,
                                   Context cx, Scriptable scope)
    {
        Callable f = getNameFunctionAndThis(name, cx, scope);
        Scriptable thisObj = lastStoredScriptable(cx);
        return f.call(cx, scope, thisObj, ScriptRuntime.emptyArgs);
    }

    /**
     * Implement name() call shrinking optimizer code.
     */
    public static Object callName0(String name, int optCallLevel, int optCallIndex,
                                   Context cx, Scriptable scope)
    {
        Callable f = getNameFunctionAndThis(name, cx, scope, optCallLevel, optCallIndex);
        Scriptable thisObj = lastStoredScriptable(cx);
        return f.call(cx, scope, thisObj, ScriptRuntime.emptyArgs);
    }

    /**
     * Implement x.property() call shrinking optimizer code.
     */
    public static Object callProp0(Object value, String property,
                                   Context cx, Scriptable scope)
    {
        Callable f = getPropFunctionAndThis(value, property, cx, scope);
        Scriptable thisObj = lastStoredScriptable(cx);
        return f.call(cx, scope, thisObj, ScriptRuntime.emptyArgs);
    }

    public static Object add(Object val1, double val2)
    {
        if (val1 instanceof Scriptable)
            val1 = ((Scriptable) val1).getDefaultValue(null);
        if (!(val1 instanceof CharSequence))
            return wrapDouble(toNumber(val1) + val2);
        return new ConsString((CharSequence)val1, toString(val2));
    }

    public static Object add(double val1, Object val2)
    {
        if (val2 instanceof Scriptable)
            val2 = ((Scriptable) val2).getDefaultValue(null);
        if (!(val2 instanceof CharSequence))
            return wrapDouble(toNumber(val2) + val1);
        return new ConsString(toString(val1), (CharSequence)val2);
    }

    public static Object elemIncrDecr(Object obj, double index,
                                      Context cx, int incrDecrMask)
    {
        return ScriptRuntime.elemIncrDecr(obj, new Double(index), cx,
                                          incrDecrMask);
    }

    public static Object[] padStart(Object[] currentArgs, int count) {
        Object[] result = new Object[currentArgs.length + count];
        System.arraycopy(currentArgs, 0, result, count, currentArgs.length);
        return result;
    }

    public static void initFunction(NativeFunction fn, int functionType,
                                    Scriptable scope, Context cx)
    {
        ScriptRuntime.initFunction(cx, scope, fn, functionType, false);
    }

    public static void initFunction(NativeFunction fn, int functionType,
                                    int optCallIndex, Scriptable scope,
                                    Context cx, boolean isEval)
    {
        if (optCallIndex != -1) {
            while (scope instanceof NativeWith) {
                scope = scope.getParentScope();
            }
            ((OptCall)scope).set(optCallIndex, fn);
        } else {
            ScriptRuntime.initFunction(cx, scope, fn, functionType, isEval);
        }
    }

    public static Object callSpecial(Context cx, Callable fun,
                                     Scriptable thisObj, Object[] args,
                                     Scriptable scope,
                                     Scriptable callerThis, int callType,
                                     String fileName, int lineNumber)
    {
        return ScriptRuntime.callSpecial(cx, fun, thisObj, args, scope,
                                         callerThis, callType,
                                         fileName, lineNumber);
    }

    public static Object newObjectSpecial(Context cx, Object fun,
                                          Object[] args, Scriptable scope,
                                          Scriptable callerThis, int callType)
    {
        return ScriptRuntime.newSpecial(cx, fun, args, scope, callType);
    }

    public static Object setName(Scriptable bound, Object value,
                                 Context cx, Scriptable scope, String id,
                                 int optCallIndex, boolean strict)
    {
        if (bound instanceof OptCall) {
            ((OptCall)bound).set(optCallIndex, value);
            return value;
        }
        return strict ? ScriptRuntime.strictSetName(bound, value, cx, scope, id)
                      : ScriptRuntime.setName(bound, value, cx, scope, id);
    }


    public static Object setConst(Scriptable bound, Object value, Context cx,
                                  String id, int optCallIndex)
    {
        if (bound instanceof OptCall) {
            ((OptCall) bound).setConst(optCallIndex, value);
            return value;
        }
        return ScriptRuntime.setConst(bound, value, cx, id);
    }

    public static Object name(Context cx, Scriptable scope, String name,
                              int optCallLevel, int optCallIndex)
    {
        Object result;
        Scriptable parent = scope.getParentScope();

        while (parent != null) {
            if (scope instanceof OptCall) {
                if (optCallLevel-- == 0) {
                    // We found our target opt activation object
                    return ((OptCall)scope).get(optCallIndex);
                }
            } else if (scope instanceof NativeCall) {
                // Activation scopes do not prototype chain and Scriptable.get
                // can be called directly.
                result = scope.get(name, scope);
                if (result != Scriptable.NOT_FOUND) {
                    return result;
                }
            } else {
                if (scope instanceof NativeWith
                        && scope.getPrototype() instanceof XMLObject) {
                    if (scope.has(name, scope)) {
                        return scope.get(name, scope);
                    }
                } else {
                    result = ScriptableObject.getProperty(scope, name);
                    if (result != Scriptable.NOT_FOUND) {
                        return result;
                    }
                }
            }
            scope = parent;
            parent = parent.getParentScope();
        }

        result = topScopeName(cx, scope, name);
        if (result == Scriptable.NOT_FOUND) {
            throw notFoundError(scope, name);
        }

        return result;
    }

    public static Scriptable bind(Context cx, Scriptable scope, String id,
                                  int optCallLevel)
    {
        Scriptable parent = scope.getParentScope();
        while (parent != null) {
            if (scope instanceof OptCall) {
                if (optCallLevel-- == 0) {
                    return scope;
                }
            } else if (scope instanceof NativeCall) {
                if (scope.has(id, scope)) {
                    return scope;
                }
            } else if (scope instanceof NativeWith) {
                Scriptable withObj = scope.getPrototype();
                if (ScriptableObject.hasProperty(withObj, id)) {
                    return withObj;
                }
            } else {
                if (ScriptableObject.hasProperty(scope, id)) {
                    return scope;
                }
            }
            scope = parent;
            parent = parent.getParentScope();
        }

        // scope here is top scope
        return ScriptRuntime.bind(cx, scope, id);
    }

    public static Callable getNameFunctionAndThis(String name, Context cx,
                                                  Scriptable scope,
                                                  int optCallLevel,
                                                  int optCallIndex)
    {
        Object result;
        Scriptable thisObj;
        Scriptable parent = scope.getParentScope();

        for (;;) {
            if (parent == null) {
                result = topScopeName(cx, scope, name);
                thisObj = scope;
                break;
            } else if (scope instanceof OptCall) {
                if (optCallLevel-- == 0) {
                    // We found our target opt activation object
                    result = ((OptCall)scope).get(optCallIndex);
                    thisObj = ScriptableObject.getTopLevelScope(parent);
                    break;
                }
            } else if (scope instanceof NativeCall) {
                // Activation scopes do not prototype chain and Scriptable.get
                // can be called directly.
                result = scope.get(name, scope);
                if (result != Scriptable.NOT_FOUND) {
                    // this object for nested functions is top scope
                    thisObj = ScriptableObject.getTopLevelScope(parent);
                    break;
                }
            } else if (scope instanceof NativeWith) {
                // we can't just use getProperty() on with objects because
                // XMLObject.get() returns empty XMLList instead of  NOT_FOUND
                Scriptable withObj = scope.getPrototype();
                if (withObj instanceof XMLObject) {
                    if (withObj.has(name, withObj)) {
                        result = withObj.get(name, withObj);
                        thisObj = withObj;
                        break;
                    }
                } else {
                    result = ScriptableObject.getProperty(scope, name);
                    if (result != Scriptable.NOT_FOUND) {
                        thisObj = withObj;
                        break;
                    }
                }
            } else {
                // Can happen if Rhino embedding decided that nested
                // scopes are useful for what ever reasons.
                result = ScriptableObject.getProperty(scope, name);
                if (result != Scriptable.NOT_FOUND) {
                    thisObj = scope;
                    break;
                }
            }
            scope = parent;
            parent = parent.getParentScope();
        }

        if (!(result instanceof Callable)) {
            if (result == Scriptable.NOT_FOUND) {
                throw notFoundError(scope, name);
            } else {
                throw notFunctionError(result, name);
            }
        }
        storeScriptable(cx, thisObj);
        return (Callable) result;
    }

    public static String typeofName(Context cx, Scriptable scope, String id,
                                    int optCallLevel, int optCallIndex)
    {
        Scriptable bound = bind(cx, scope, id, optCallLevel);
        if (bound == null)
            return "undefined";
        Object value;
        if (bound instanceof OptCall) {
            value = ((OptCall)bound).get(optCallIndex);
        } else {
            value = ScriptableObject.getProperty(bound, id);
        }
        return typeof(value);
    }

    public static Object nameIncrDecr(Scriptable scope, String id,
                                      Context cx, int incrDecrMask,
                                      int optCallLevel, int optCallIndex) {
        Scriptable target;
        Object value;
        search: {
            do {
                if (scope.getParentScope() == null) {
                    // delegate top level scopes to ScriptRuntime
                    return ScriptRuntime.nameIncrDecr(scope, id, cx, incrDecrMask);
                }
                target = scope;
                if (target instanceof OptCall) {
                    if (optCallLevel-- == 0) {
                        value = ((OptCall) target).get(optCallIndex);
                        break search;
                    }
                } else {
                    do {
                        if (target instanceof NativeWith &&
                                target.getPrototype() instanceof XMLObject) {
                            break;
                        }
                        value = target.get(id, scope);
                        if (value != Scriptable.NOT_FOUND) {
                            break search;
                        }
                        target = target.getPrototype();
                    } while (target != null);
                }
                scope = scope.getParentScope();
            } while (scope != null);
            throw notFoundError(scope, id);
        }

        boolean post = ((incrDecrMask & Node.POST_FLAG) != 0);
        double number;

        if (value instanceof Number) {
            number = ((Number)value).doubleValue();
        } else {
            number = toNumber(value);
            if (post) {
                // convert result to number
                value = wrapNumber(number);
            }
        }
        if ((incrDecrMask & Node.DECR_FLAG) == 0) {
            ++number;
        } else {
            --number;
        }
        Number result = wrapNumber(number);
        if (target instanceof OptCall && optCallIndex > -1) {
            ((OptCall)target).set(optCallIndex, result);
        } else {
            target.put(id, scope, result);
        }
        if (post) {
            return value;
        } else {
            return result;
        }
    }

    public static Scriptable createOptFuncActivation(NativeFunction funObj,
                                                     Scriptable scope,
                                                     Object[] args)
    {
        return new OptCall(funObj, scope, args);
    }

    public static Double wrapDouble(double num)
    {
        if (num == 0.0) {
            if (1 / num > 0) {
                // +0.0
                return zeroObj;
            }
        } else if (num == 1.0) {
            return oneObj;
        } else if (num == -1.0) {
            return minusOneObj;
        } else if (num != num) {
            return NaNobj;
        }
        return new Double(num);
    }

    static String encodeIntArray(int[] array)
    {
        // XXX: this extremely inefficient for small integers
        if (array == null) { return null; }
        int n = array.length;
        char[] buffer = new char[1 + n * 2];
        buffer[0] = 1;
        for (int i = 0; i != n; ++i) {
            int value = array[i];
            int shift = 1 + i * 2;
            buffer[shift] = (char)(value >>> 16);
            buffer[shift + 1] = (char)value;
        }
        return new String(buffer);
    }

    private static int[] decodeIntArray(String str, int arraySize)
    {
        // XXX: this extremely inefficient for small integers
        if (arraySize == 0) {
            if (str != null) throw new IllegalArgumentException();
            return null;
        }
        if (str.length() != 1 + arraySize * 2 && str.charAt(0) != 1) {
            throw new IllegalArgumentException();
        }
        int[] array = new int[arraySize];
        for (int i = 0; i != arraySize; ++i) {
            int shift = 1 + i * 2;
            array[i] = (str.charAt(shift) << 16) | str.charAt(shift + 1);
        }
        return array;
    }

    public static Scriptable newArrayLiteral(Object[] objects,
                                             String encodedInts,
                                             int skipCount,
                                             Context cx,
                                             Scriptable scope)
    {
        int[] skipIndexces = decodeIntArray(encodedInts, skipCount);
        return newArrayLiteral(objects, skipIndexces, cx, scope);
    }

    public static void main(final Script script, final String[] args)
    {
        ContextFactory.getGlobal().call(new ContextAction() {
            public Object run(Context cx)
            {
                ScriptableObject global = getGlobal(cx);

                // get the command line arguments and define "arguments"
                // array in the top-level object
                Object[] argsCopy = new Object[args.length];
                System.arraycopy(args, 0, argsCopy, 0, args.length);
                Scriptable argsObj = cx.newArray(global, argsCopy);
                global.defineProperty("arguments", argsObj,
                                      ScriptableObject.DONTENUM);
                script.exec(cx, global);
                return null;
            }
        });
    }

    public static void throwStopIteration(Object obj) {
        throw new JavaScriptException(
            NativeIterator.getStopIterationObject((Scriptable)obj), "", 0);
    }

    public static Scriptable createNativeGenerator(NativeFunction funObj,
                                                   Scriptable scope,
                                                   Scriptable thisObj,
                                                   int maxLocals,
                                                   int maxStack)
    {
        return new NativeGenerator(scope, funObj,
                new GeneratorState(thisObj, maxLocals, maxStack));
    }

    public static Object[] getGeneratorStackState(Object obj) {
        GeneratorState rgs = (GeneratorState) obj;
        if (rgs.stackState == null)
            rgs.stackState = new Object[rgs.maxStack];
        return rgs.stackState;
    }

    public static Object[] getGeneratorLocalsState(Object obj) {
        GeneratorState rgs = (GeneratorState) obj;
        if (rgs.localsState == null)
            rgs.localsState = new Object[rgs.maxLocals];
        return rgs.localsState;
    }

    public static class GeneratorState {
        static final String CLASS_NAME =
            "org/mozilla/javascript/optimizer/OptRuntime$GeneratorState";

        public int resumptionPoint;
        static final String resumptionPoint_NAME = "resumptionPoint";
        static final String resumptionPoint_TYPE = "I";

        public Scriptable thisObj;
        static final String thisObj_NAME = "thisObj";
        static final String thisObj_TYPE =
            "Lorg/mozilla/javascript/Scriptable;";

        Object[] stackState;
        Object[] localsState;
        int maxLocals;
        int maxStack;

        GeneratorState(Scriptable thisObj, int maxLocals, int maxStack) {
            this.thisObj = thisObj;
            this.maxLocals = maxLocals;
            this.maxStack = maxStack;
        }
    }
}
