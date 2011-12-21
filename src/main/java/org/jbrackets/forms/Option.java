package org.jbrackets.forms;

public interface Option {

    String value();

    String label();

    boolean standsFor(Object object);
}