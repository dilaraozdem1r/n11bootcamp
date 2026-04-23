package com.n11bootcamp.aopornek.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceAspect {

    @Before("execution(* com.n11bootcamp.aopornek.service.MessageService.mesajVer(..))")
    public void MesajVerMetodundanOnce(JoinPoint joinPoint) {
        System.out.println("MesajVer metodundan önce çağrılan parametre" + joinPoint.getArgs()[0]);
    }

    @After("execution(* com.n11bootcamp.aopornek.service.*.*(..))")
    public void MesajVerMetodundanSonra(JoinPoint joinPoint) {
        System.out.println("Mesaj ver metodundan sonra çağırılan parametre" + joinPoint.getArgs()[0]);
    }

    @AfterReturning(pointcut = "execution(* com.n11bootcamp.aopornek.service.MessageService.mesajVer(..))", returning = "retVal")
    public void MesajVerMetodundanDegerDonduktenSonra(Object retVal) {
        System.out.println("mesajver metodundan geriye deger dondukten sonra " + retVal);
    }


}