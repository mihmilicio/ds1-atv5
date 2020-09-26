package com.company;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ProdutoManager {
    private static final String[] urls = {
        "./src/com/company/bebidas-tabuladas.txt",
        "./src/com/company/vinhos-tabulados.txt",
        "./src/com/company/pratos.csv"
    };
    private static final String[] separadores = { "\t", "\t", ";" };
    private static final int[] precoCol = new int[3];
    private static final int[] nomeCol = new int[3];

    public static List<Produto> readProdutos(int catPos) throws FileNotFoundException {
        File arquivo = new File(urls[catPos]);
        Scanner leitor = new Scanner(new FileInputStream(arquivo));
        String[] header = leitor.nextLine().split(separadores[catPos]);
        precoCol[catPos] = Arrays.asList(header).indexOf("PRECO");
        nomeCol[catPos] = (precoCol[catPos] == 0) ? 1 : 0;
        String categoria = header[nomeCol[catPos]];


        List<Produto> produtoList = new ArrayList<>();

        while (leitor.hasNextLine()) {
            String[] conteudo = leitor.nextLine().split(separadores[catPos]);
            if (conteudo.length > 1) {
                produtoList.add(new Produto(categoria, conteudo[nomeCol[catPos]], Double.parseDouble(conteudo[precoCol[catPos]].replace(",", "."))));
            }
        }
        leitor.close();

        return produtoList;
    }

    private static void salvarArquivo(int catPos, List<Produto> produtos) throws IOException {
        FileWriter arquivoOut = new FileWriter(urls[catPos]);
        PrintWriter gravador = new PrintWriter(arquivoOut);

        if (precoCol[catPos] == 0) {
            gravador.print("PRECO");
            gravador.print(separadores[catPos]);
            gravador.println(Main.categorias[catPos]);
        } else {
            gravador.print(Main.categorias[catPos]);
            gravador.print(separadores[catPos]);
            gravador.println("PRECO");
        }

        produtos.forEach(produto -> {
            if (precoCol[catPos] == 0) {
                gravador.print(String.format("%.2f", produto.preco));
                gravador.print(separadores[catPos]);
                gravador.println(produto.nome);
            } else {
                gravador.print(produto.nome);
                gravador.print(separadores[catPos]);
                gravador.println(String.format("%.2f", produto.preco));
            }
        });

        gravador.close();
    }

    public static void createProduto(int catPos, Produto produto) throws IOException {
        List<Produto> currentList = readProdutos(catPos);
        currentList.add(produto);
        salvarArquivo(catPos, currentList);
    }

    public static void deleteProduto(int catPos, int index) throws IOException {
        List<Produto> currentList = readProdutos(catPos);
        currentList.remove(index);
        salvarArquivo(catPos, currentList);
    }

    public static void updateProduto(int catPos, int index, Produto produto) throws IOException {
        List<Produto> currentList = readProdutos(catPos);
        currentList.set(index, produto);
        salvarArquivo(catPos, currentList);
    }
}
