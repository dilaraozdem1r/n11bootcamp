package oop;

public class Goz {

    public String renk;
    public String retina;
    public int gozYukseklik;
    public int gozGenislik;

    public Goz(String renk)
    {
        this.renk = renk;
    }

    public Goz(String renk,String retina,int gozYukseklik,int gozGenislik)
    {
        this.renk = renk;
        this.retina = retina;
        this.gozGenislik = gozGenislik;
        this.gozYukseklik = gozYukseklik;
    }
}
