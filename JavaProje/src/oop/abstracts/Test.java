package oop.abstracts;

public class Test {
    public static void main(String[] args) {
        Kedi k = new Kedi();
        k.hareketEt();

        GenelMudur gm = new GenelMudur();
        gm.ad = "Hakan";bak olmuyor
        gm.soyad = "Yılmaz";
        gm.departman = "Genel Müdür";

        Mudur m = new Mudur();
        Programci p = new Programci();
        Stajyer s = new Stajyer();
        double toplam = 0;
        toplam = gm.maasinizNedir()+m.maasinizNedir()+p.maasinizNedir()+s.maasinizNedir();
        System.out.println("Toplam : " + toplam+ " TL maaş alıyorlar");

        Calisan[] calisanlar=new Calisan[4];
        calisanlar[0]=gm;
        calisanlar[1] = m;
        calisanlar[2] = p;
        calisanlar[3] = s;

        for(int j=0; j< calisanlar.length;j++)
        {
            System.out.println("Çalışanın adı : " +calisanlar[j].ad + " Soyadı : " + calisanlar[j].soyad+ " Maaşı : " + calisanlar[0].maasinizNedir());
        }

        Matematik ma = new Matematik();
        int gelendeger = ma.topla(5,5);
        System.out.println(" iki sayının toplamı : " + gelendeger);


    }
}
