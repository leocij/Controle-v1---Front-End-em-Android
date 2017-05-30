package com.lemelo.controlev1;

import android.content.Intent;
import android.icu.math.BigDecimal;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Created by leoci on 30/05/2017.
 */

public class CadastraControleFragment extends Fragment {
    private String cookie;
    private View view;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_cadastra_controle, container, false);
        final EditText txtControleData = (EditText) view.findViewById(R.id.txtControleData);
        final EditText txtControleDescricao = (EditText) view.findViewById(R.id.txtControleDescricao);
        final EditText txtControleEntrada = (EditText) view.findViewById(R.id.txtControleEntrada);
        final EditText txtControleSaida = (EditText) view.findViewById(R.id.txtControleSaida);

        final SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        txtControleData.setText(data.format(Calendar.getInstance().getTime()));

        final Button btnControleSalvar = (Button) view.findViewById(R.id.btnControleSalvar);
        btnControleSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date d = sdf.parse(txtControleData.getText().toString());
                    java.sql.Date dataSql = new java.sql.Date(d.getTime());
                    jsonObject.put("data",dataSql);
                    jsonObject.put("descricao",txtControleDescricao.getText().toString());
                    String entradaStr = txtControleEntrada.getText().toString();
                    if(entradaStr.equals("")){
                        entradaStr = "0.0";
                    }
                    jsonObject.put("entrada",new BigDecimal(entradaStr));
                    String saidaStr = txtControleSaida.getText().toString();
                    if(saidaStr.equals("")){
                        saidaStr = "0.0";
                    }
                    jsonObject.put("saida",new BigDecimal(saidaStr));

                    ServerSide serverSide = new ServerSide();
                    PostAsyncTask postAsyncTask = new PostAsyncTask();

                    cookie = getArguments().getString("cookie");
                    String resposta = postAsyncTask.execute(serverSide.getServer() + "controles", jsonObject.toString(), cookie).get();

                    if (resposta == null){
                        Toast.makeText(getContext(), "Erro do servidor " + resposta, Toast.LENGTH_LONG).show();
                    } else if (resposta.equals("http")) {
                        Toast.makeText(getContext(), "Erro do servidor " + postAsyncTask.getCodeResponse(), Toast.LENGTH_LONG).show();
                    } else if (resposta.equals("sucess")){
                        txtControleDescricao.setText("");
                        txtControleEntrada.setText("");
                        txtControleSaida.setText("");
                        Toast.makeText(getContext(), "Dados salvos", Toast.LENGTH_LONG).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }
}
