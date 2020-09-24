package com.company;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Pedido {
    private static final NumberFormat formatter = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));

    int id;
    final List<Produto> itens;
    public String observacao;
    double precoTotal;

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

    public void printItens() {
        this.itens.forEach(item -> System.out.println("\t"+ item.nome + "\t ( R$ "+ formatter.format(item.preco) +" )"));
    }

    public double getPrecoTotal() {
        return this.precoTotal;
    }

    public void setObservacao(String obs) {
        this.observacao = obs;
    }
}
