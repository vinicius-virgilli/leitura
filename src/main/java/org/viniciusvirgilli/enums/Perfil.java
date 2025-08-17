package org.viniciusvirgilli.enums;

public enum Perfil {
    ADMIN("Administrador"),
    USER("Usuário"),
    MODERATOR("Moderador");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return this.name();
    }
}