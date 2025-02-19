package com.example.car.etlapdemo;

public class Food {
    private int id;
    private String nev;
    private String leiras;
    private int ar;
    private String kategoria;

    public Food(int id, String nev, String leiras, int ar, String kategoria) {
        this.id = id;
        this.nev = nev;
        this.leiras = leiras;
        this.ar = ar;
        this.kategoria = kategoria;
    }

    public int getId() { return id; }
    public String getNev() { return nev; }
    public String getLeiras() { return leiras; }
    public int getAr() { return ar; }
    public String getKategoria() { return kategoria; }
}