package com.lemelo.controlev1;

import android.icu.math.BigDecimal;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import java.util.concurrent.ExecutionException;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by leoci on 30/05/2017.
 */

public class EditaControleFragment  extends Fragment {
    private String cookie;
    private View view;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_edita_controle, container, false);

        ScrollView scrollViewEditaControle = (ScrollView) view.findViewById(R.id.scrollViewEditaControle);
        scrollViewEditaControle.setOnTouchListener(new View.OnTouchListener() {
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

        final EditText txtData = (EditText) view.findViewById(R.id.txtData);
        txtData.setText(getArguments().getString("data"));
        final EditText txtDescricao = (EditText) view.findViewById(R.id.txtDescricao);
        txtDescricao.setText(getArguments().getString("descricao"));
        final EditText txtEntrada = (EditText) view.findViewById(R.id.txtEntrada);
        txtEntrada.setText(getArguments().getString("entrada"));
        final EditText txtSaida = (EditText) view.findViewById(R.id.txtSaida);
        txtSaida.setText(getArguments().getString("saida"));

        final Button btnSalvar = (Button) view.findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
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
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    java.util.Date d = sdf.parse(txtData.getText().toString());
                    java.sql.Date dataSql = new java.sql.Date(d.getTime());
                    jsonObject.put("data",dataSql);
                    jsonObject.put("descricao",txtDescricao.getText().toString());
                    String entradaStr = txtEntrada.getText().toString();
                    if(entradaStr.equals("")){
                        entradaStr = "0.0";
                    }
                    jsonObject.put("entrada",new BigDecimal(entradaStr));
                    String saidaStr = txtSaida.getText().toString();
                    if(saidaStr.equals("")){
                        saidaStr = "0.0";
                    }
                    jsonObject.put("saida",new BigDecimal(saidaStr));
                    ServerSide serverSide = new ServerSide();
                    PutAsyncTask putAsyncTask = new PutAsyncTask();
                    cookie = getArguments().getString("cookie");
                    String resposta = putAsyncTask.execute(serverSide.getServer() + "controles/" + Long.parseLong(getArguments().getString("identifier")), jsonObject.toString(), cookie).get();

                    if (resposta == null){
                        Toast.makeText(getContext(), "Erro do servidor " + resposta, Toast.LENGTH_LONG).show();
                    } else if (resposta.equals("http")) {
                        Toast.makeText(getContext(), "Erro do servidor " + putAsyncTask.getCodeResponse(), Toast.LENGTH_LONG).show();
                    } else if (resposta.equals("sucess")){
                        Toast.makeText(getContext(), "Editado com sucesso!", Toast.LENGTH_LONG).show();
                        Bundle bundle = new Bundle();
                        bundle.putString("cookie", cookie);
                        ControleFragment fragment = new ControleFragment();
                        fragment.setArguments(bundle);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_content, fragment);
                        ft.addToBackStack(null);
                        ft.commit();
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
