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
    private static Pedido pedidoCorrente;

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
            pedidoCorrente = new Pedido(JsonManager.getNextId());
            flowOption = Menus.iniciar(listCompleta);
        } while (flowOption == 2);

    }

    /*
     * RETORNOS
     * 0 - padrão, apenas termina
     * 1 - repeat menu
     * 2 - nova sessão
     * */
    public static int getOptionInicial(int op) throws IOException, InterruptedException {
        int retorno = 0;
        switch (op) {
            case 1 -> {
                System.out.println("\nNOVO PEDIDO");
                retorno = Menus.menuItens("CREATE");
            }
            case 2 -> {
                List<Pedido> pedidosRegistrados = JsonManager.lerPedidos();

                System.out.println("\nPEDIDOS REGISTRADOS");
                int listSize = pedidosRegistrados.size();
                if (listSize > 0) {
                    IntStream.range(0, listSize).forEach(index -> {
                        Pedido pedido = pedidosRegistrados.get(index);
                        printPedido(pedido, "Pedido #" + pedido.id, false, false);
                    });
                } else {
                    System.out.println("Sem pedidos até o momento\n");
                }

                Thread.sleep(1250);
                retorno = 1;

            }
            case 3 -> {
                System.out.println("\nEDITAR PEDIDO");
                // todo: editar obs, adicionar e remover itens
            }
            case 4 -> {
                System.out.println("\nDELETAR PEDIDO");
                System.out.print("Insira o ID do pedido para deletar: ");
                int id = input.nextInt();
                boolean sucesso = JsonManager.deletarPedido(id);
                if (sucesso) {
                    System.out.println("Pedido #"+id+" deletado.");
                } else {
                    System.out.println("Pedido com ID "+id+" não encontrado.");
                }

                retorno = 1;
            }
            case 5 -> {
                System.out.println("Encerrando...");
                System.exit(99);
            }
        }

        return retorno;
    }


    /*
     * RETORNOS
     * 0 - padrão, apenas termina
     * 1 - repeat menuItens
     * 2 - nova sessão (retornar true até o fim)
     * */
    public static int getOptionItens(int op, String tipo) throws InterruptedException, IOException {
        int retorno = 0;
        switch (op) {
            case 4 -> {
                do {
                    System.out.println("\nCARRINHO");
                    printPedido(pedidoCorrente, "Pedido corrente", true, true);
                    Thread.sleep(1250);
                    if (pedidoCorrente.itens.size() > 0) {
                        Menus.printOpcoesCarrinho();
                        int opCarrinho = 0;
                        do {
                            opCarrinho = Main.input.nextInt();

                            if (opCarrinho < 1 || opCarrinho > 2) {
                                System.out.println(invalidOptionMessage);
                            }
                        } while (opCarrinho < 1 || opCarrinho > 2);

                        if (opCarrinho == 1) {
                            retorno = 1;
                        } else {
                            removeItensFromPedido();
                            retorno = -1;
                        }
                    } else {
                        retorno = 1;
                    }
                } while (retorno == -1);
            }
            case 5 -> {
                if (!pedidoCorrente.isValidPedido()) {
                    System.out.println(emptyPedidoMessage);
                    retorno = 1;
                } else {
                    retorno = enviarPedido(tipo);
                }
            }
            case 6 -> {

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
                        pedidoCorrente.setItem((Produto) itemSelected);
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

    private static void removeItensFromPedido () {
        int op;
        do {
            System.out.print("Insira o número do produto para remover: ");
            op = input.nextInt();

            if (op < 1 || op > pedidoCorrente.itens.size()) {
                System.out.println(invalidOptionMessage);
            }
        } while (op < 1 || op > pedidoCorrente.itens.size());

        String item = pedidoCorrente.itens.get(op - 1).nome;
        pedidoCorrente.deleteItem(op - 1);
        System.out.println(item + " removido do carrinho.");
    }

    private static int enviarPedido (String tipo) throws IOException {
        printPedido(pedidoCorrente, "Pedido atual", true, false);

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

                pedidoCorrente.setObservacao(input.nextLine());
                System.out.println("Observação salva com sucesso!");
                retorno = enviarPedido(tipo);
            }
            case 3 -> {
                //salvar
                int id = JsonManager.salvarPedido(pedidoCorrente);
                System.out.println("Pedido salvo com sucesso! ID: " + id);
                System.out.println("Deseja iniciar uma nova sessão? (S/N)");
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

    private static void printPedido(Pedido pedido, String title, boolean printObservacao, boolean printIndex) {
        System.out.println(title + " | Preço total: R$ " + formatter.format(pedido.precoTotal));
        IntStream.range(0, pedido.itens.size()).forEach(index -> {
            String indice = printIndex ? "["+(index + 1)+"] - " : "";
            Produto item = pedido.itens.get(index);
            System.out.println("\t"+ indice + item.categoria + " " + item.nome + "\t ( R$ "+ formatter.format(item.preco) +" )");
        });
        if (printObservacao) {
            System.out.println("\tOBS: " + (pedido.observacao != null ? pedido.observacao : "---") );
        }
        System.out.println();
    }
}
