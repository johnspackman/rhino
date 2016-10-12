/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.javascript.jdk18;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import org.mozilla.javascript.*;

public class VMBridge_jdk18 extends org.mozilla.javascript.jdk15.VMBridge_jdk15
{
    public VMBridge_jdk18() throws SecurityException, InstantiationException {
        super();
    }

    @Override
    public void checkOneMethodInterface(Class<?> cl) {
        Method[] methods = cl.getMethods();
        int length = methods.length;
        if (length == 0) {
            throw Context.reportRuntimeError1(
                "msg.no.empty.interface.conversion", cl.getName());
        }
        if (length > 1) {
            ArrayList<Method> abstractMethods = new ArrayList<Method>();
            for (Method m : methods) {
                if (!m.isDefault() && !Modifier.isStatic(m.getModifiers())) {
                    abstractMethods.add(m);
                }
            }
            if (abstractMethods.size() > 1) {
                String methodName = abstractMethods.get(0).getName();
                for (int i = 1; i < abstractMethods.size(); i++) {
                    if (!methodName.equals(abstractMethods.get(i).getName())) {
                        throw Context.reportRuntimeError1(
                                 "msg.no.function.interface.conversion",
                                 cl.getName());
                    }
                }
            }
        }
    }
}
