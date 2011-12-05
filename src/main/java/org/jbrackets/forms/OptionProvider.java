package org.jbrackets.forms;

import java.util.List;

/**
 * @author michal.jemala
 */
public interface OptionProvider {
    List<? extends Option> getOptions();

    interface Option {
	String value();

	String label();

	boolean standsFor(Object object);
    }
}
