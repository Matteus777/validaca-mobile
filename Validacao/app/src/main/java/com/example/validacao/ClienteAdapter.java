package com.example.validacao;

import static com.example.validacao.R.layout.layout_cliente;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class ClienteAdapter extends BaseAdapter {

    private Context contexto;
    private List<ClienteEntity> listaDeClientes;
    private LayoutInflater inflater;
    public ClienteAdapter(Context context, List<ClienteEntity> lista) {
        this.contexto = context;
        this.listaDeClientes = lista;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listaDeClientes.size();
    }

    @Override
    public Object getItem(int i) {
        return listaDeClientes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Suporte item;

        if(view == null) {
            view = inflater.inflate(R.layout.layout_cliente,null);

            item = new Suporte();
            item.tvNome = (TextView)  view.findViewById(R.id.tvNome);
            item.tvDocument = (TextView)  view.findViewById(R.id.tvDocument);
            item.btnDelete = (Button)  view.findViewById(R.id.btnDelete);
            view.setTag(item);
        } else {
            item = (Suporte) view.getTag();
        }
    ClienteEntity cliente = listaDeClientes.get(i);
        item.tvNome.setText(cliente.getName());
        item.tvDocument.setText(cliente.getDocument());
        return view;

    }

    private class Suporte {

        TextView tvNome, tvDocument,btnDelete;

    }

}
