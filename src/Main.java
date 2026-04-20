import oop.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Insan i = new Insan();
        i.ad = "ibrahim";
        i.soyad = "gökyar";
        i.yas = 47;
        i.maas = 3.14;
        i.cinsiyet = true;
        i.uyu();

        Insan i2 = new Insan("Hakan", "Yılmaz");

        Insan i3 = new Insan("Metin", "yıldaz", 33, 3.14, true);

        Anne a = new Anne();
        a.ad = "Fahriye";
        a.soyad = "Gökyar";
        a.ekranaYaz(a.ad, a.soyad);
        a.oku(a.ad);
        a.yaz(a.ad);
        a.dinle(a.ad);
        Baba b = new Baba();
        b.ad = "Kasım";
        b.soyad = "Gökyar";
        b.ekranaYaz(b.ad, b.soyad);
        b.dinle(b.ad);
        Cocuk c = new Cocuk();
        c.ad = "ibrahim";
        c.soyad = "gökyar";
        c.ekranaYaz(c.ad, c.soyad);

        AkilliCocuk ak = new AkilliCocuk();
        ak.ad = "Hakan";
        ak.soyad = "Yılmaz";
        ak.sifat = "Akıllıdır";
        ak.adSoyadSifatYaz(ak.ad, ak.soyad, ak.sifat);

        UsluCocuk us = new UsluCocuk();
        us.adSoyadSifatYaz("Mehmet", "Yıldız", "Usludur");

        String[] gunler = new String[7];
        gunler[0] = "pazartesi";
        gunler[1] = "salı";
        gunler[2] = "çarşamba";
        gunler[3] = "perşembe";
        gunler[4] = "cuma";
        gunler[5] = "cumartesi";
        gunler[6] = "pazar";
        for (int z = 0; z < gunler.length; z++) {
            System.out.println(gunler[z]);
        }

    }
}