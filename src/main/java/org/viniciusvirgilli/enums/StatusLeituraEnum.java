package org.viniciusvirgilli.enums;

public enum StatusLeituraEnum {
    A_LER("A ler"),
    LENDO("Lendo"),
    LIDO("Lido");

    private final String label;

    StatusLeituraEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
