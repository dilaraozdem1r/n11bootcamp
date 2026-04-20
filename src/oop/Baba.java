package oop;

import oop.interfaces.IDinle;

public class Baba extends Insan implements IDinle  {
    @Override
    public void dinle(String adi) {
        System.out.println(adi + " dinliyor");
    }
}
