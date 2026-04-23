package oop;

public class Test {

    public static void main(String[] args)
    {

        Goz goz = new Goz("Kahvergi","retina",20,20);
        Kulak kulak = new Kulak("Kepçe");
        Burun burun = new Burun("Kemer");
        Kafa kafa = new Kafa(goz,kulak,burun);
        YeniInsan yeni = new YeniInsan(kafa,"ibrahim","gökyar");
        yeni.ekranaYaz();

    }
}
