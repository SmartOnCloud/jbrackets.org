package org.jbrackets.forms;

/**
 * HTML5 enabled INPUT tag types.
 */
public enum InputType {
    text, file, email, url, tel, date, time, password;

    public String getValue() {
	return name();
    }

}
