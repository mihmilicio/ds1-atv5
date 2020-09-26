package com.company;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class ObjetoPedidos {
    final List<Pedido> pedidos;

    ObjetoPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }
}

public class PedidoManager {
    public static boolean createPedido(Pedido pedido) throws IOException {
        // mantêm os pedidos já registrados
        List<Pedido> registros = readPedidos();

        if (registros.stream().map(ped -> ped.id).collect(Collectors.toList()).contains(pedido.id)) {
            return false;
        } else {
            registros.add(pedido);
            // cria novo objeto incrementando registros antigos
            salvarArquivo(registros);
            return true;
        }
    }

    public static boolean updatePedido(Pedido pedido) throws IOException {
        List<Pedido> registros = readPedidos();

        if (registros.stream().map(ped -> ped.id).collect(Collectors.toList()).contains(pedido.id)) {
            int index = IntStream.range(0, registros.size())
                    .filter(i -> pedido.id == registros.get(i).id)
                    .findFirst()
                    .orElse(0);
            registros.set(index, pedido);
            // cria novo objeto incrementando registros antigos
            salvarArquivo(registros);
            return true;
        } else {
            return false;
        }
    }

    public static void salvarArquivo (List<Pedido> pedidos) throws IOException {
        Gson gsonB = new GsonBuilder().setPrettyPrinting().create();
        ObjetoPedidos objPedidos = new ObjetoPedidos(pedidos);
        String json = gsonB.toJson(objPedidos);
        FileWriter arquivoOut = new FileWriter("./src/com/company/registro_pedidos.json");
        PrintWriter gravador = new PrintWriter(arquivoOut);
        gravador.print(json);
        gravador.close();
    }

    public static List<Pedido> readPedidos() throws IOException {
        Gson gson = new Gson();

        File arquivo = new File("./src/com/company/registro_pedidos.json");

        List<Pedido> retorno;
        if (arquivo.exists()) {
            Reader reader = new FileReader(arquivo);

            ObjetoPedidos objetoPedidos = gson.fromJson(reader, ObjetoPedidos.class);
            retorno = objetoPedidos.pedidos;
            reader.close();
        } else {
            retorno = new ArrayList<>();
        }

        return retorno;
    }

    public static int getNextId() throws IOException {
        List<Pedido> pedidosExistentes = readPedidos();
        int listSize = pedidosExistentes.size();
        if (listSize > 0) {
            return pedidosExistentes.get(pedidosExistentes.size() - 1).id + 1;
        } else {
            return 1;
        }
    }

    public static boolean deletePedido(int id) throws IOException {
        List<Pedido> pedidosExistentes = readPedidos();

        if (pedidosExistentes.stream().map(ped -> ped.id).collect(Collectors.toList()).contains(id)) {
            int index = IntStream.range(0, pedidosExistentes.size())
                    .filter(i -> id == pedidosExistentes.get(i).id)
                    .findFirst()
                    .orElse(-1);
            if (index >= 0) {
                pedidosExistentes.remove(index);
                salvarArquivo(pedidosExistentes);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
