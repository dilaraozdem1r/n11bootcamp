package oop;

public class YeniInsan {

    public Kafa kafa;
    public String ad;
    public String soyad;

    public YeniInsan(Kafa kafa,String ad,String soyad)
    {
        this.kafa = kafa;
        this.ad = ad;
        this.soyad = soyad;
    }

    public void ekranaYaz()
    {
        System.out.println("Kişinin ad ı: " + ad+ " Soyadı : " + soyad+ " göz rengi : "
                + kafa.gozRengi+ " kulağı " + kafa.kulak.sekil+ " burnu : " + kafa.burun.tip);
    }


}