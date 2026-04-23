package oop.annotation;

import java.lang.annotation.Annotation;

@BilgiRuntime(bilgi="Annotation ornek", tarih="23.04.2026", yazar="dilara ozdemir")
public class Test {
    public static void main(String[] args){
        Class<?> c;

        try{
            c=Class.forName("oop.annotation.Test");
            Annotation[] ann=c.getDeclaredAnnotations();
            for(Annotation a: ann){
                Class<?> annType=a.annotationType();
                System.out.println("annotation type: " +annType);
            }
            BilgiRuntime infoAnn= c.getAnnotation(BilgiRuntime.class);
            System.out.println("Bilgi : " + infoAnn.bilgi());
            System.out.println("Tarih : " + infoAnn.tarih());
            System.out.println("Yazar : " + infoAnn.yazar());


        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
