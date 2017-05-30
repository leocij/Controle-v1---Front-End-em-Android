package com.lemelo.controlev1;


import android.os.Bundle;
import android.support.annotation.Nullable;
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

/**
 * Created by leoci on 29/05/2017.
 */

public class BarganhaFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_barganha, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.barganhaFab);

        try {
            imprimeBarganhas(view);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

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

    private void imprimeBarganhas(View view) throws ExecutionException, InterruptedException, JSONException, ParseException {
        ServerSide serverSide = new ServerSide();
        GetAsyncTask getAsyncTask = new GetAsyncTask();

        String cookie = getArguments().getString("cookie");
        String resposta = getAsyncTask.execute(serverSide.getServer() + "barganhas", null, cookie).get();

        if (resposta == null){
            Toast.makeText(getContext(), "Erro do servidor " + resposta, Toast.LENGTH_LONG).show();
        } else if (resposta.equals("http")) {
            Toast.makeText(getContext(), "Erro do servidor " + getAsyncTask.getCodeResponse(), Toast.LENGTH_LONG).show();
        } else if (resposta.equals("buffer")) {
            JSONArray jsonArray = new JSONArray(getAsyncTask.getBuffer().toString());
            List<Barganha> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                Barganha l = new Barganha();
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
                if (jsonObject.has("valor")) {
                    l.setValor(new BigDecimal(jsonObject.getDouble("valor")));
                }
                list.add(l);
            }// End of loop for

            ArrayAdapter<Barganha> arrayAdapter = new ArrayAdapter<Barganha>(getActivity(), android.R.layout.simple_list_item_1, list);
            ListView lvImprimeBarganhas = (ListView) view.findViewById(R.id.lvImprimeBarganhas);
            lvImprimeBarganhas.setAdapter(arrayAdapter);

            lvImprimeBarganhas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    Barganha selecionado = (Barganha) parent.getItemAtPosition(position);
                    trataSelecionado(selecionado);
                }
            });
        }
    }

    private void trataSelecionado(Barganha selecionado) {

    }
}


