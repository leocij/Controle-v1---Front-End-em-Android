package com.lemelo.controlev1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

/**
 * Created by Leoci Melo 26/05/2017.
 */

public class LoginActivity extends Activity{

    private String resposta = null;
    private ProgressDialog dialog;
    private String cookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ScrollView loginScrollView = (ScrollView) findViewById(R.id.loginScrollView);
        loginScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService( INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                v.setFocusableInTouchMode(true);
                v.requestFocus();
                v.setFocusableInTouchMode(false);
                return false;
            }
        });

        final Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideSoftKeyboard(v);

                final AutoCompleteTextView userAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.userAutoCompleteTextView);
                final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

                String username = userAutoCompleteTextView.getText().toString();
                String password = passwordEditText.getText().toString();
                String postParamaters = "username=" + username + "&password=" + password;

                UserPostAsync userPostAsync = new UserPostAsync();
                //userPostAsync.setContext(getApplicationContext());
                ServerSide serverSide = new ServerSide();
                try {
                    dialog = new ProgressDialog(v.getContext());
                    dialog.setTitle("Aviso!");
                    dialog.setMessage("Conectando...");
                    userPostAsync.setDialog(dialog);

                    resposta = userPostAsync.execute(serverSide.getServer() + "login", postParamaters).get();
                    Toast.makeText(getApplicationContext(), "Resposta: " + resposta, Toast.LENGTH_LONG).show();
                    if (resposta != null) {
                        String[] respostaStr = resposta.split(" ");
                        if (respostaStr[0].equals("http")) {
                            if (respostaStr[1].equals("401")) {
                                Toast.makeText(getApplicationContext(), "Username or password incorrect!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Aviso! Erro HTTP: " + respostaStr[1], Toast.LENGTH_LONG).show();
                            }
                        } else if (respostaStr[0].equals("cookie")) {
                            if (respostaStr[1] != null) {
                                String[] cookies1 = respostaStr[1].split(";");
                                String[] cookies2 = cookies1[0].split("=");
                                setCookie(cookies2[1]);
                                Toast.makeText(getApplicationContext(), getCookie(), Toast.LENGTH_LONG).show();
                                //Toast.makeText(getApplicationContext(), respostaStr[1], Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                String cookieStr = getCookie();
                                intent.putExtra("cookie", cookieStr);
                                startActivity(intent);

                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Servidor não responde, tente mais tarde!", Toast.LENGTH_LONG).show();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService( INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setFocusableInTouchMode(false);
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getCookie() {
        return cookie;
    }
}
