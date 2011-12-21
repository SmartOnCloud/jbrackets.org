package org.jbrackets.forms;

import java.util.List;

/**
 * @author michal.jemala
 */
public interface OptionProvider {
    List<? extends Option> getOptions();
}
