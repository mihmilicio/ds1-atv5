package com.company;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Main {
    public static final String[] categorias = { "BEBIDA", "VINHO", "PRATO" };
    public static final String[] artigosCategorias = { "a", "o", "o" };
    public static final Scanner input = new Scanner(System.in);
    private static final List<List<Produto>> listCompleta = new ArrayList<>();
    private static Pedido pedidoCorrente;

    public static final NumberFormat formatter = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
    public static final String emptyPedidoMessage = "Não é possível enviar um pedido vazio... Adicione pelo menos um item!";
    public static final String invalidOptionMessage = "Ops... Escolha uma opção válida...";
    public static final String errorCreate = "Erro ao criar pedido. Esse ID já existe.";
    public static final String errorUpdate = "Erro ao alterar pedido. ID não encontrado.";
    public static final String errorDelete = "Erro ao deletar pedido. ID não encontrado.";

    public static void main(String[] args) throws IOException, InterruptedException {
        listCompleta.add(ProdutoManager.readProdutos(0));
        listCompleta.add(ProdutoManager.readProdutos(1));
        listCompleta.add(ProdutoManager.readProdutos(2));

        int flowOption;
        do {
            pedidoCorrente = new Pedido(PedidoManager.getNextId());
            flowOption = Menus.iniciar(listCompleta);
        } while (flowOption == 2);

    }

    /*
    * RETORNOS
    *
    * */
    public static int getOptionInicial(int op) throws IOException, InterruptedException {
        int retorno = 0;
        switch (op) {
            case 1 -> {
                retorno = Menus.menuPedido();
            }
            case 5 -> {
                System.out.println("Encerrando...");
                System.exit(99);
            }
            default -> {
                retorno = Menus.menuCategoria(op - 2);
            }
        }

        return retorno;
    }

    /*
     * RETORNOS
     * 0 - padrão, apenas termina
     * 1 - repeat menu
     * 2 - nova sessão
     * */
    public static int getOptionPedido(int op) throws IOException, InterruptedException {
        int retorno = 0;
        switch (op) {
            case 1 -> {
                System.out.println("\nNOVO PEDIDO");
                retorno = Menus.menuItens("CREATE");
            }
            case 2 -> {
                List<Pedido> pedidosRegistrados = PedidoManager.readPedidos();

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
                System.out.print("Insira o ID do pedido para editar: ");
                int id = input.nextInt();
                Pedido pedido = PedidoManager.getPedidoById(id);
                if (pedido != null) {
                    pedidoCorrente = pedido;
                    retorno = Menus.menuEditar();
                } else {
                    System.out.println(errorUpdate);
                    retorno = 1;
                }
            }
            case 4 -> {
                System.out.println("\nDELETAR PEDIDO");
                System.out.print("Insira o ID do pedido para deletar: ");
                int id = input.nextInt();
                boolean sucesso = PedidoManager.deletePedido(id);
                if (sucesso) {
                    System.out.println("Pedido #"+id+" deletado.");
                } else {
                    System.out.println(errorDelete);
                }

                retorno = 1;
            }
            case 5 -> {
                System.out.println("Retornando ao menu inicial...");
                retorno = 2;
            }
            case 6 -> {
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
                retorno = 2;
                System.out.println("Pedido cancelado, voltando ao menu inicial.");
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


    public static int getOptionEditar(int op) throws IOException, InterruptedException {
        int retorno = 0;
        switch (op) {
            case 1 -> {
                retorno = Menus.menuItens("UPDATE");
            }
            case 2 -> {
                System.out.println("OBS: "+ pedidoCorrente.observacao);
                System.out.println("Deseja alterar a observação? (S/N)");
                input.nextLine();

                String opNovo = input.nextLine();

                if (opNovo.equals("S") || opNovo.equals("s")) {
                    System.out.println("Insira a nova observação: ");
                    pedidoCorrente.setObservacao(input.nextLine());
                    System.out.println("Observação alterada com sucesso!");
                }
                retorno = 1;
            }
            case 3 -> {
                if (!pedidoCorrente.isValidPedido()) {
                    System.out.println(emptyPedidoMessage);
                    retorno = 1;
                } else {
                    retorno = enviarPedido("UPDATE");
                }
            }
            case 4 -> {
                retorno = 2;
                System.out.println("Alterações canceladas, voltando ao menu inicial.");
            }
        }
        return retorno;
    }



    public static int getOptionCategoria(int op, int catPos) throws IOException, InterruptedException {
        int retorno = 0;
        String cat = categorias[catPos];
        String art = artigosCategorias[catPos];
        listCompleta.set(catPos, ProdutoManager.readProdutos(catPos));
        List<Produto> catList = listCompleta.get(catPos);
        switch(op) {
            case 1 -> {
                System.out.println("\nNOV"+ art.toUpperCase() + " " + cat);

                System.out.print("Insira o nome d"+art+" "+cat.toLowerCase()+": ");
                input.nextLine();
                String nome = input.nextLine();

                System.out.print("Insira o preço d"+art+" "+cat.toLowerCase()+": ");
                Double preco = input.nextDouble();

                Produto novoProd = new Produto(cat, nome, preco);
                ProdutoManager.createProduto(catPos, novoProd);

                System.out.println("Salvo com sucesso!");
                Thread.sleep(750);

                retorno = 1;
            }
            case 2 -> {
                System.out.print("\n"+ cat + "S");
                Menus.printProdutosCategoria(catPos, catList, false);
                Thread.sleep(1250);
                retorno = 1;
            }
            case 3 -> {
                System.out.println("\nEDITAR "+ cat);
                Menus.printProdutosCategoria(catPos, catList, false);

                int idx;
                do {
                    System.out.println("\nInsira o índice d"+ art + " "+ cat.toLowerCase()+ " que deseja editar: ");
                    idx = input.nextInt();
                    if (idx < 1 || idx > catList.size()) {
                        System.out.println(invalidOptionMessage);
                    }
                } while (idx < 1 || idx > catList.size());

                System.out.println("Insira o novo nome d"+art+" "+cat.toLowerCase()+": ");
                input.nextLine();
                String nome = input.nextLine();

                System.out.println("Insira o novo preço d"+art+" "+cat.toLowerCase()+": ");
                Double preco = input.nextDouble();

                Produto updatedProd = new Produto(cat, nome, preco);
                ProdutoManager.updateProduto(catPos, idx - 1, updatedProd);
                System.out.println("Editado com sucesso!");
                Thread.sleep(750);

                retorno = 1;
            }
            case 4 -> {
                System.out.println("\nDELETAR "+ cat);
                Menus.printProdutosCategoria(catPos, catList, false);

                int idx;
                do {
                    System.out.println("\nInsira o índice d"+ art + " "+ cat.toLowerCase()+ " que deseja deletar: ");
                    idx = input.nextInt();
                    if (idx < 1 || idx > catList.size()) {
                        System.out.println(invalidOptionMessage);
                    }
                } while (idx < 1 || idx > catList.size());
                ProdutoManager.deleteProduto(catPos, idx - 1);
                System.out.println("Deletado com sucesso!");
                Thread.sleep(750);

                retorno = 1;
            }
            case 5 -> {
                System.out.println("Retornando ao menu inicial...");
                retorno = 2;
            }
            case 6 -> {
                System.out.println("Encerrando...");
                System.exit(99);
            }
        }

        return retorno;
    }

    private static Object pickItem (int catPos) throws FileNotFoundException {
        int pick;
        listCompleta.set(catPos, ProdutoManager.readProdutos(catPos));
        List<Produto> catList = listCompleta.get(catPos);
        do {
            Menus.printProdutosCategoria(catPos, catList, true);
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
        printPedido(pedidoCorrente, "Pedido corrente", true, false);

        Menus.printEnviarPedido(tipo);
        int op;
        int retorno = 0;

        do {
            op = input.nextInt();
            if (op < 1 || op > 3) {
                System.out.println(invalidOptionMessage);
            }
        } while (op < 1 || op > 3);

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
                if (tipo.equals("CREATE")) {
                    boolean sucesso = PedidoManager.createPedido(pedidoCorrente);
                    if (sucesso) {
                        System.out.println("Pedido salvo com sucesso! ID: " + pedidoCorrente.id);
                    } else {
                        System.out.println(errorCreate);
                    }
                } else if (tipo.equals("UPDATE")) {
                    boolean sucesso = PedidoManager.updatePedido(pedidoCorrente);
                    if (sucesso) {
                        System.out.println("Pedido alterado com sucesso! ID: " + pedidoCorrente.id);
                    } else {
                        System.out.println(errorUpdate);
                    }
                }

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
