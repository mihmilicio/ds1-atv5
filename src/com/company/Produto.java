package com.company;

public class Produto {
    final String categoria;
    final String nome;
    final Double preco;

    public Produto(String categoria, String nome, String preco) {
        this.categoria = categoria;
        this.nome = nome;
        this.preco = Double.parseDouble(preco.replace(",", "."));
    }
}
