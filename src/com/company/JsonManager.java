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

public class JsonManager {
    public static void salvarPedido(Pedido pedido) throws IOException {
        // mantêm os pedidos já registrados
        Gson gson = new Gson();
        File arquivo = new File("./src/com/company/registro_pedidos.json");

        List<Pedido> currList;

        if (arquivo.exists()) {
            Reader reader = new FileReader(arquivo);
            ObjetoPedidos currObjPedidos = gson.fromJson(reader, ObjetoPedidos.class);
            reader.close();
            currList = currObjPedidos.pedidos;
        } else {
            currList = new ArrayList<>();
        }

        if (currList.stream().map(ped -> ped.id).collect(Collectors.toList()).contains(pedido.id)) {
            int index = IntStream.range(0, currList.size())
                    .filter(i -> pedido.id == currList.get(i).id)
                    .findFirst()
                    .orElse(0);
            currList.set(index, pedido);
        } else {
            currList.add(pedido);
        }

        // cria novo objeto incrementando registros antigos
        Gson gsonB = new GsonBuilder().setPrettyPrinting().create();
        ObjetoPedidos objPedidos = new ObjetoPedidos(currList);
        String json = gsonB.toJson(objPedidos);
        FileWriter arquivoOut = new FileWriter("./src/com/company/registro_pedidos.json");
        PrintWriter gravador = new PrintWriter(arquivoOut);
        gravador.print(json);
        gravador.close();
    }

    public static List<Pedido> lerPedidos() throws IOException {
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
        List<Pedido> pedidosExistentes = lerPedidos();
        int listSize = pedidosExistentes.size();
        if (listSize > 0) {
            return pedidosExistentes.get(pedidosExistentes.size() - 1).id + 1;
        } else {
            return 1;
        }
    }
}
