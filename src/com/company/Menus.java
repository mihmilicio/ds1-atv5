package com.company;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static com.company.Main.invalidOptionMessage;

public class Menus {
    private static void printInicial() {
        System.out.println("\nMENU INICIAL");
        System.out.println("Para qual menu deseja ir?");
        System.out.println("[1] - Pedidos");
        System.out.println("[2] - Bebidas");
        System.out.println("[3] - Vinhos");
        System.out.println("[4] - Pratos");
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
        } while (op > 5 || op < 1 || retorno == 1);



        return retorno;
    }

    private static void printMenuPedido() {
        System.out.println("\nMENU DE PEDIDO");
        System.out.println("[1] - Novo pedido");
        System.out.println("[2] - Consultar pedidos registrados");
        System.out.println("[3] - Modificar pedido");
        System.out.println("[4] - Deletar pedido");
        System.out.println("[5] - Voltar");
        System.out.println("[6] - Encerrar :(");
    }

    public static int menuPedido() throws IOException, InterruptedException {
        int op;
        int retorno = 0;
        do {
            printMenuPedido();

            op = Main.input.nextInt();

            if (op < 1 || op > 6) {
                System.out.println(invalidOptionMessage);
            } else {
                retorno = Main.getOptionPedido(op);
            }
        } while (op > 6 || op < 1 || retorno == 1);

        return retorno;
    }

    private static void printMenuItens (String tipo) {
        System.out.println("\nMENU DE COMPRA");
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

    public static void printProdutosCategoria(int catPos, List<Produto> produtoList, boolean showExtras) {
        if (showExtras) {
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
        }
        System.out.println();

        int listSize = produtoList.size();
        IntStream.range(0, listSize).forEach(index -> {
            Produto produto = produtoList.get(index);
            System.out.println("["+ (index + 1) +"] - " + produto.nome + "\t\t( R$ " + Main.formatter.format(produto.preco) + " )");
        });
        if (showExtras) {
            System.out.println("[" + (listSize + 1) + "] - Voltar ao menu principal");
        }
    }

    public static void printEnviarPedido (String tipo) {
        System.out.println("[1] - Continuar comprando");
        if (tipo.equals("CREATE")) {
            System.out.println("[2] - Adicionar observação ao pedido");
            System.out.println("[3] - Enviar");
        } else if (tipo.equals("UPDATE")) {
            System.out.println("[2] - Editar observação do pedido");
            System.out.println("[3] - Salvar alterações");
        }
    }

    public static void printOpcoesCarrinho () {
        System.out.println("[1] - Continuar");
        System.out.println("[2] - Remover itens");
    }

    private static void printMenuEditar () {
        System.out.println("[1] - Editar itens");
        System.out.println("[2] - Editar observação");
        System.out.println("[3] - Salvar alterações");
        System.out.println("[4] - Cancelar alterações");
    }

    public static int menuEditar () throws IOException, InterruptedException {
        int op;
        int retorno = 0;
        do {
            printMenuEditar();

            op = Main.input.nextInt();

            if (op < 1 || op > 4) {
                System.out.println(invalidOptionMessage);
            } else {
                retorno = Main.getOptionEditar(op);
            }
        } while (op > 4 || op < 1 || retorno == 1);

        return retorno;
    }

    private static void printMenuCategoria (int catPos) {
        String cat = Main.categorias[catPos];
        String art = Main.artigosCategorias[catPos];
        System.out.println("\nMENU DE " + cat);
        System.out.println("[1] - Nov"+ art +" "+cat.toLowerCase());
        System.out.println("[2] - Consultar "+ cat.toLowerCase() +"s registrad"+art+"s");
        System.out.println("[3] - Modificar "+ cat.toLowerCase());
        System.out.println("[4] - Deletar "+ cat.toLowerCase());
        System.out.println("[5] - Voltar");
        System.out.println("[6] - Encerrar :(");
    }

    public static int menuCategoria (int catPos) throws IOException, InterruptedException {
        int op;
        int retorno = 0;
        do {
            printMenuCategoria(catPos);

            op = Main.input.nextInt();

            if (op < 1 || op > 6) {
                System.out.println(invalidOptionMessage);
            } else {
                retorno = Main.getOptionCategoria(op, catPos);
            }
        } while (op > 6 || op < 1 || retorno == 1);

        return retorno;
    }


    public static int iniciar (List<List<Produto>> list) throws InterruptedException, IOException {
        System.out.println("Bem-vindo ao restaurante!");

        return menuInicial();
    }
}
