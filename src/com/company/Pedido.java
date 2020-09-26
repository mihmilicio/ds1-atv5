package com.company;

import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private final int id;
    private final List<Produto> itens;
    private String observacao;
    private double precoTotal;

    public Pedido(int id) {
        this.itens = new ArrayList<>();
        this.precoTotal = 0;
        this.id = id;
    }

    public void setItem(Produto prod) {
        this.itens.add(prod);
        this.precoTotal += prod.preco;
    }

    public void deleteItem(int index) {
        this.itens.remove(index);
        calcPrecoTotal();
    }

    public boolean isValidPedido() {
        return this.itens.size() > 0;
    }

    public void calcPrecoTotal() {
        this.precoTotal = this.itens.stream().map(produto -> produto.preco).reduce((double) 0, Double::sum);
    }

    public void setObservacao(String obs) {
        this.observacao = obs;
    }
    public String getObservacao() {
        return this.observacao;
    }

    public List<Produto> getItens() { return this.itens; }

    public double getPrecoTotal() { return this.precoTotal; }

    public int getId() { return this.id; }
}

