package org.jbrackets.web;

import org.springframework.expression.spel.support.StandardEvaluationContext;

public interface StandardEvaluationContextRegistrat {

    void register(StandardEvaluationContext evalContext);
}
