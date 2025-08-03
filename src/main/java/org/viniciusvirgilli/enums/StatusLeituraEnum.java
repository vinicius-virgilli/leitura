package org.viniciusvirgilli.enums;

public enum StatusLeituraEnum {
    TO_READ("A ler"),
    READING("Lendo"),
    READ("Lido");

    private final String label;

    StatusLeituraEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
