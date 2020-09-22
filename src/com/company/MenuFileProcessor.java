package com.company;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MenuFileProcessor {
    public static List<Produto> processar (URL url, String separator) throws FileNotFoundException {
        File arquivo = new File(url.getPath());
        Scanner leitor = new Scanner(new FileInputStream(arquivo));
        String[] header = leitor.nextLine().split(separator);
        int precoCol = Arrays.asList(header).indexOf("PRECO");
        int nomeCol = (precoCol == 0) ? 1 : 0;
        String categoria = header[nomeCol];


        List<Produto> genList = new ArrayList<>();

        while (leitor.hasNextLine()) {
            String[] conteudo = leitor.nextLine().split(separator);
            if (conteudo.length > 1) {
                genList.add(new Produto(categoria, conteudo[nomeCol], conteudo[precoCol]));
            }
        }
        leitor.close();

        return genList;
    }
}
