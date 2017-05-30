package com.lemelo.controlev1;

import android.icu.math.BigDecimal;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static android.content.Context.INPUT_METHOD_SERVICE;

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

        ScrollView loginScrollView = (ScrollView) view.findViewById(R.id.scrollViewCadastraControle);
        loginScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService( INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                v.setFocusableInTouchMode(true);
                v.requestFocus();
                v.setFocusableInTouchMode(false);
                return false;
            }
        });


        final EditText txtControleData = (EditText) view.findViewById(R.id.txtData);
        final EditText txtControleDescricao = (EditText) view.findViewById(R.id.txtDescricao);
        final EditText txtControleEntrada = (EditText) view.findViewById(R.id.txtEntrada);
        final EditText txtControleSaida = (EditText) view.findViewById(R.id.txtSaida);

        final SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        txtControleData.setText(data.format(Calendar.getInstance().getTime()));

        final Button btnControleSalvar = (Button) view.findViewById(R.id.btnSalvar);
        btnControleSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Fecha Teclado
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService( INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                v.setFocusableInTouchMode(true);
                v.requestFocus();
                v.setFocusableInTouchMode(false);

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
