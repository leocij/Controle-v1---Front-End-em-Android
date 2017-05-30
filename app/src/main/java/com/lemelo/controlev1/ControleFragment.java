package com.lemelo.controlev1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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
    private View view;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_controle, container, false);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.controleFab);

        imprime(view);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cookie = getArguments().getString("cookie");
                Bundle bundle = new Bundle();
                bundle.putString("cookie", cookie);
                CadastraControleFragment cadastra = new CadastraControleFragment();
                cadastra.setArguments(bundle);
                android.support.v4.app.FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragment_content, cadastra);
                ft.commit();
            }
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void imprime(View view) {

        ServerSide serverSide = new ServerSide();
        GetAsyncTask getAsyncTask = new GetAsyncTask();

        try {
            cookie = getArguments().getString("cookie");
            String resposta = getAsyncTask.execute(serverSide.getServer() + "controles", null, cookie).get();
            if (resposta == null){
                Toast.makeText(getContext(), "Erro do servidor " + resposta, Toast.LENGTH_LONG).show();
            } else if (resposta.equals("http")) {
                Toast.makeText(getContext(), "Erro do servidor " + getAsyncTask.getCodeResponse(), Toast.LENGTH_LONG).show();
            } else if (resposta.equals("buffer")) {
                JSONArray jsonArray = new JSONArray(getAsyncTask.getBuffer().toString());
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
                        Controle selecionado = (Controle) parent.getItemAtPosition(position);
                        trataSelecionado(selecionado);
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

    private void trataSelecionado(final Controle selecionado) {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(getActivity());
        dialogo.setTitle("Editar / Apagar?");
        dialogo.setMessage(selecionado.toString());

        dialogo.setNegativeButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CarregaTelaEditar(selecionado);
            }
        });

        dialogo.setPositiveButton("Apagar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                AlertDialog.Builder dialogDel = new AlertDialog.Builder(getActivity());
                dialogDel.setTitle("Deseja realmente apagar?");
                dialogDel.setMessage(selecionado.toString());

                dialogDel.setNegativeButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogDel, int which) {
                        Long idDelete = selecionado.getIdentifier();
                        ServerSide serverSide = new ServerSide();
                        DeleteAsyncTask deleteAsyncTask = new DeleteAsyncTask();
                        try {
                            String resposta = deleteAsyncTask.execute(serverSide.getServer() + "controles/" + idDelete, null, cookie).get();
                            if (resposta == null){
                                Toast.makeText(getContext(), "Erro do servidor " + resposta, Toast.LENGTH_LONG).show();
                            } else if (resposta.equals("http")) {
                                Toast.makeText(getContext(), "Erro do servidor " + deleteAsyncTask.getCodeResponse(), Toast.LENGTH_LONG).show();
                            }else if (resposta.equals("sucess")){
                                Toast.makeText(getContext(), "Dado deletado!" + deleteAsyncTask.getDado(), Toast.LENGTH_LONG).show();
                                imprime(view);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                });

                dialogDel.setPositiveButton("Não", null);
                dialogDel.show();
            }

        });

        dialogo.setNeutralButton("Cancelar", null);
        dialogo.show();
    }

    private void CarregaTelaEditar(Controle selecionado) {

    }


}
