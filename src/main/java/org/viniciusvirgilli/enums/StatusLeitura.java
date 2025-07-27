package org.viniciusvirgilli.enums;

public enum StatusLeitura {
    TO_READ("A ler"),
    READING("Lendo"),
    READ("Lido");

    private final String label;

    StatusLeitura(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
