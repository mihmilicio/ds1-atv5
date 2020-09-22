package com.company;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Main {
    private static final String[] categorias = { "BEBIDA", "VINHO", "PRATO" };
    public static final Scanner input = new Scanner(System.in);
    private static final List<List<Produto>> listCompleta = new ArrayList<>();
    private static final List<Pedido> listPedidos = new ArrayList<>();
    private static int currentIndex = 0;

    public static final NumberFormat formatter = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
    public static final String emptyPedidoMessage = "Não é possível enviar um pedido vazio... Adicione pelo menos um item!";
    public static final String invalidOptionMessage = "Ops... Escolha uma opção válida...";

    public static void main(String[] args) throws IOException, InterruptedException {
        URL urlBebida = Main.class.getResource("bebidas-tabuladas.txt");
        listCompleta.add(MenuFileProcessor.processar(urlBebida, "\t"));

        URL urlVinho = Main.class.getResource("vinhos-tabulados.txt");
        listCompleta.add(MenuFileProcessor.processar(urlVinho, "\t"));

        URL urlPrato = Main.class.getResource("pratos.csv");
        listCompleta.add(MenuFileProcessor.processar(urlPrato, ";"));

        int flowOption;
        do {
            listPedidos.add(new Pedido());
            flowOption = Menus.iniciar(listCompleta);
            currentIndex++;
        } while (flowOption == 2);

    }

    /*
    * RETORNOS
    * 0 - padrão, apenas termina
    * 1 - repeat menuPrincipal
    * 2 - novo pedido (retornar true até o fim)
    * */
    public static int getOptionPrincipal(int op) throws InterruptedException, IOException {
        int retorno = 0;
        switch (op) {
            case 4 -> {
                System.out.println("\nCARRINHO");
                printPedido(listPedidos.get(currentIndex), "Pedido atual", true);
                Thread.sleep(1250);
                retorno = 1;
            }
            case 5 -> {
                if (!listPedidos.get(currentIndex).isValidPedido()) {
                    System.out.println(emptyPedidoMessage);
                    retorno = 1;
                } else {
                    retorno = enviarPedido();
                }
            }
            case 6 -> {
                List<Pedido> pedidosRegistrados = JsonManager.lerPedidos();

                System.out.println("\nPEDIDOS REGISTRADOS");
                int listSize = pedidosRegistrados.size();
                if (listSize > 0) {
                    IntStream.range(0, listSize).forEach(index -> {
                        Pedido pedido = pedidosRegistrados.get(index);
                        printPedido(pedido, "Pedido #" + (index + 1), false);
                    });
                } else {
                    System.out.println("Sem pedidos até o momento\n");
                }
                retorno = 1;
            }
            case 7 -> {
                System.out.println("Encerrando...");
                System.exit(99);
            }
            default -> {
                Object itemSelected;
                do {
                    itemSelected = pickItem(op - 1);
                    if (itemSelected != null) {
                        listPedidos.get(currentIndex).setItem((Produto) itemSelected);
                        Thread.sleep(750);
                    }
                } while (itemSelected != null);
                retorno = 1;
            }
        }

        return retorno;
    }

    private static Object pickItem (int catPos) {
        int pick;
        List<Produto> catList = listCompleta.get(catPos);
        do {
            Menus.printCategoria(catPos);
            pick = Main.input.nextInt();

            if (pick < 1 || pick > catList.size() + 1) {
                System.out.println(invalidOptionMessage);
            }
        } while (pick < 1 || pick > catList.size() + 1);

        if (pick == (catList.size() + 1)) {
            return null;
        } else {
            System.out.println(catList.get(pick - 1).nome + " adicionado ao carrinho. ");
            return catList.get(pick - 1);
        }
    }

    private static int enviarPedido () throws IOException {
        printPedido(listPedidos.get(currentIndex), "Pedido atual", true);

        Menus.printEnviarPedido();
        int op;
        int retorno = 0;

        do {
            op = input.nextInt();
            if (op < 1 || op > 4) {
                System.out.println(invalidOptionMessage);
            }
        } while (op < 1 || op > 4);

        switch (op) {
            case 1 -> retorno = 1;
            case 2 -> { //adicionar observação
                System.out.println("Insira a observação: ");
                input.nextLine();

                listPedidos.get(currentIndex).setObservacao(input.nextLine());
                System.out.println("Observação salva com sucesso!");
                retorno = enviarPedido();
            }
            case 3 -> {
                //salvar
                JsonManager.salvarPedidos(listPedidos);
                System.out.println("Pedido enviado com sucesso!");
                System.out.println("Deseja iniciar um novo pedido? (S/N)");
                input.nextLine();

                String opNovo = input.nextLine();

                if (opNovo.equals("S") || opNovo.equals("s")) {
                    retorno = 2;
                } else {
                    retorno = 0;
                }
            }
        }

        return retorno;
    }

    private static void printPedido(Pedido pedido, String title, boolean printObservacao) {
        System.out.println(title + " | Preço total: R$ " + formatter.format(pedido.precoTotal));
        pedido.itens.forEach(item -> System.out.println("\t"+ item.categoria + " " + item.nome + "\t ( R$ "+ formatter.format(item.preco) +" )"));
        if (printObservacao) {
            System.out.println("\tOBS: " + (pedido.observacao != null ? pedido.observacao : "---") );
        }
        System.out.println();
    }
}
