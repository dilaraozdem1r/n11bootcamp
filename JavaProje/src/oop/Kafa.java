package oop;

public class Kafa {
    public Goz goz;
    public String gozRengi;
    public Kulak kulak;
    public Burun burun;


    public Kafa(Goz goz, Kulak kulak, Burun burun)
    {
        this.goz = goz;

        this.kulak = kulak;
        this.burun = burun;

    }
    public Kafa(String gozRengi,String retina,int gozYukseklik,int gozGenislik, Kulak kulak, Burun burun)
    {
        this.gozRengi = gozRengi;
        this.kulak = kulak;
        this.burun = burun;
        //this.retina = retina;
        /// this.gozGenislik = gozGenislik;
        // this.gozYukseklik = gozYukseklik;
    }

}