package oop;

public class Insan {
    public String ad;
    public String soyad;
    public int yas;
    public double maas;
    public boolean cinsiyet;

    public void uyu() {
        System.out.println("Uyuyor");
    }

    public void ekranaYaz(String adi, String soyadi) {
        System.out.println("Kişinin adı:" + adi + " kişinin soyadı: " + soyadi);
    }

    //boş constructor
    public Insan() {

    }

    public Insan(String isim, String soyadi) {
        ad = isim;
        soyad = soyadi;
    }

    public Insan(String ad, String soyad, int yas, double maas, boolean cinsiyet) {
        this.ad = ad;
        this.soyad = soyad;
        this.yas = yas;
        this.maas = maas;
        this.cinsiyet = cinsiyet;
    }


}
