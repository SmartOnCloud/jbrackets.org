package org.jbrackets.parser.tokens;

import java.util.Map;

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;

@SuppressWarnings("rawtypes")
public class MapFailoverAccessor implements PropertyAccessor {

    public boolean canRead(EvaluationContext context, Object target, String name)
	    throws AccessException {
	return true;
    }

    public TypedValue read(EvaluationContext context, Object target, String name)
	    throws AccessException {
	Map map = (Map) target;
	return new TypedValue(map.get(name));
    }

    public boolean canWrite(EvaluationContext context, Object target,
	    String name) throws AccessException {
	return true;
    }

    @SuppressWarnings("unchecked")
    public void write(EvaluationContext context, Object target, String name,
	    Object newValue) throws AccessException {
	Map map = (Map) target;
	map.put(name, newValue);
    }

    public Class[] getSpecificTargetClasses() {
	return new Class[] { Map.class };
    }

}
