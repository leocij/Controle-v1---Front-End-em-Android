package com.lemelo.controlev1;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/*
 * Created by leoci on 27/05/2017.
 */

public class ControleFragment extends Fragment {

    private String cookie;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_controle, container, false);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.controleFab);

        imprimeControles(view);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO criar método para chamar cadastro.

                //cadastraControle();

                Toast.makeText(getContext(), "Deu Certo", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void imprimeControles(View view) {

        ServerSide serverSide = new ServerSide();
        ThreadAsyncTask task = new ThreadAsyncTask();
        task.setMethod("GET");

        try {
            String teste = getArguments().getString("cookie");
            String resposta = task.execute(serverSide.getServer() + "controles", null, teste).get();
            if (resposta == null){
                Toast.makeText(getContext(), "Erro do servidor " + resposta, Toast.LENGTH_LONG).show();
            } else if (resposta.equals("http")) {
                Toast.makeText(getContext(), "Erro do servidor " + task.getCodeResponse(), Toast.LENGTH_LONG).show();
            } else if (resposta.equals("buffer")) {
                JSONArray jsonArray = new JSONArray(task.getBuffer().toString());
                List<Controle> list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Controle l = new Controle();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.has("identifier")) {
                        l.setIdentifier(jsonObject.getLong("identifier"));
                    }
                    if (jsonObject.has("data")) {
                        String strData = jsonObject.getString("data").toString();
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date utilData = new Date(dateFormat.parse(strData).getTime());
                        java.sql.Date sqlData = new java.sql.Date(utilData.getTime());
                        l.setData(sqlData);
                    }
                    if (jsonObject.has("descricao")) {
                        l.setDescricao(jsonObject.getString("descricao"));
                    }
                    if (jsonObject.has("entrada")) {
                        l.setEntrada(new BigDecimal(jsonObject.getDouble("entrada")));
                    }
                    if (jsonObject.has("saida")) {
                        l.setSaida(new BigDecimal(jsonObject.getDouble("saida")));
                    }
                    list.add(l);
                } // End of loop for

                ArrayAdapter<Controle> arrayAdapter = new ArrayAdapter<Controle>(getActivity(), android.R.layout.simple_list_item_1, list);
                ListView lvImprimeControles = (ListView) view.findViewById(R.id.lvImprimeControles);
                lvImprimeControles.setAdapter(arrayAdapter);

                lvImprimeControles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                        Controle controleSelecionado = (Controle) parent.getItemAtPosition(position);
                        trataControleSelecionado(controleSelecionado);
                    }
                });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void trataControleSelecionado(Controle controleSelecionado) {

    }
}
