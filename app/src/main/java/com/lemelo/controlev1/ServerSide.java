package com.lemelo.controlev1;

/*
 * Created by Leoci Melo on 26/05/2017.
 */

class ServerSide {
    String getServer() {
        //AWS
        //return "http://ec2-54-207-44-125.sa-east-1.compute.amazonaws.com:5000/";
        //Heroku
        return "https://ljmexpensectrl.herokuapp.com/";
        // Rede Casa
        //return "http://192.168.1.7:5000/";
        // Rede Celular
        //return "http://192.168.43.147:5000/";
    }
}
