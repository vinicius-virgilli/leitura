package org.viniciusvirgilli.enums;

public enum CategoriaLivroEnum {
    ESPIRITUAL("Espiritual"),
    INTELECTUAL("Intelectual");

    private final String label;

    CategoriaLivroEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
