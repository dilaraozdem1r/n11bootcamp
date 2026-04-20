package oop;

import oop.interfaces.IDinle;
import oop.interfaces.IOku;
import oop.interfaces.IYaz;

public class Anne extends Insan implements IOku, IDinle, IYaz {

    @Override
    public void dinle(String adi) {
        System.out.print(adi + " dinliyor");
    }

    @Override
    public void oku(String adi) {
        System.out.print(adi + " okuyor");
    }

    @Override
    public void yaz(String adi) {
        System.out.print(adi + " yaziyor");
    }
}
