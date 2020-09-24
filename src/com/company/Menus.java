package com.company;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.company.Main.invalidOptionMessage;

public class Menus {
    private static List<List<Produto>> listCompleta = new ArrayList<>();

    private static void printInicial () {
        System.out.println("\nMENU INICIAL");
        System.out.println("[1] - Novo pedido");
        System.out.println("[2] - Consultar pedidos registrados");
        System.out.println("[3] - Modificar pedido");
        System.out.println("[4] - Deletar pedido");
        System.out.println("[5] - Encerrar :(");
    }

    public static int menuInicial () throws IOException, InterruptedException {
        int op;
        int retorno = 0;
        do {
            printInicial();

            op = Main.input.nextInt();

            if (op < 1 || op > 5) {
                System.out.println(invalidOptionMessage);
            } else {
                retorno = Main.getOptionInicial(op);
            }

            if (retorno == 2) {
                retorno = menuItens("CREATE");
            }
            if (retorno == 3) {
                retorno = menuItens("UPDATE");
            }
        } while (op > 5 || op < 1 || retorno == 1);



        return retorno;
    }

    private static void printMenuItens (String tipo) {
        System.out.println("\nMENU DE PRODUTOS");
        System.out.println("O que você deseja?");
        System.out.println("[1] - Soft Drinks");
        System.out.println("[2] - Vinhos");
        System.out.println("[3] - Pratos");
        System.out.println("[4] - Consultar carrinho");
        if (tipo.equals("CREATE")) {
            System.out.println("[5] - Enviar pedido");
            System.out.println("[6] - Cancelar pedido");
        } else if (tipo.equals("UPDATE")) {
            System.out.println("[5] - Salvar alterações no pedido");
            System.out.println("[6] - Cancelar alterações do pedido");
        }
        System.out.println("[7] - Encerrar :(");
    }

    public static int menuItens (String tipo) throws InterruptedException, IOException {
        int op;
        int retorno = 0;
        do {
            printMenuItens(tipo);

            op = Main.input.nextInt();

            if (op < 1 || op > 7) {
                System.out.println(invalidOptionMessage);
            } else {
                retorno = Main.getOptionItens(op, tipo);
            }
        } while (op > 7 || op < 1 || retorno == 1);

        return retorno;

    }

    public static void printCategoria (int catPos) {
        String banner;
        if (catPos == 0) {
            banner = "|           SOFT DRINKS           |";
        } else if (catPos == 1) {
            banner = "|              VINHOS             |";
        } else {
            banner = "|              PRATOS             |";
        }
        System.out.println("  _______________________________ ");
        System.out.println(" /                               \\");
        System.out.println(banner);
        System.out.println(" \\_______________________________/");
        System.out.println();

        int listSize = listCompleta.get(catPos).size();
        IntStream.range(0, listSize).forEach(index -> {
            Produto produto = listCompleta.get(catPos).get(index);
            System.out.println("["+ (index + 1) +"] - " + produto.nome + "\t\t( R$ " + Main.formatter.format(produto.preco) + " )");
        });
        System.out.println("["+ (listSize + 1) +"] - Voltar ao menu principal");
    }

    public static void printEnviarPedido () {
        System.out.println("[1] - Continuar comprando");
        System.out.println("[2] - Adicionar observação ao pedido");
        System.out.println("[3] - Enviar");
        System.out.println("[4] - Cancelar pedido e encerrar");
    }

    public static void printOpcoesCarrinho () {
        System.out.println("[1] - Continuar");
        System.out.println("[2] - Remover itens");
    }


    public static int iniciar (List<List<Produto>> list) throws InterruptedException, IOException {
        System.out.println("Bem-vindo ao restaurante!");

        listCompleta = list;

        return menuInicial();
    }
}
