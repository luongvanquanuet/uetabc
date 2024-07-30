package com.abc.configuration;



public class Splender extends Bike {
    public void run() {
        System.out.println("running safely with 60km");
    }

    public static void main(String args[]) {
        Bike b = new Bike(); // upcasting
        b.run();
    }
}