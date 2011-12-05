package org.jbrackets.forms;

public interface ItemWrapper {
    String beforeItem(FieldMetadata metadata);

    String beforeItemLabel(FieldMetadata metadata);

    String afterItem(FieldMetadata metadata);

    String afterItemLabel(FieldMetadata metadata);

    String beforeItemField(FieldMetadata metadata);

    String afterItemField(FieldMetadata metadata);
}