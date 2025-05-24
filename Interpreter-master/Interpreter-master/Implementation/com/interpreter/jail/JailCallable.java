package com.interpreter.jail;

import java.util.List;
interface JailCallable {

    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);

}
