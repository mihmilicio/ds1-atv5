package com.company;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    public static List<Produto> readProdutos(int catPos) throws FileNotFoundException {
        File arquivo = new File(urls[catPos]);
        Scanner leitor = new Scanner(new FileInputStream(arquivo));
        String[] header = leitor.nextLine().split(separadores[catPos]);
        int precoCol = Arrays.asList(header).indexOf("PRECO");
        int nomeCol = (precoCol == 0) ? 1 : 0;
        String categoria = header[nomeCol];


        List<Produto> produtoList = new ArrayList<>();

        while (leitor.hasNextLine()) {
            String[] conteudo = leitor.nextLine().split(separadores[catPos]);
            if (conteudo.length > 1) {
                produtoList.add(new Produto(categoria, conteudo[nomeCol], conteudo[precoCol]));
            }
        }
        leitor.close();

        return produtoList;
    }
}
