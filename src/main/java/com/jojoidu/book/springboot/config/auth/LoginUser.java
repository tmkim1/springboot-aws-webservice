package com.jojoidu.book.springboot.config.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) //메서드의 파라미터로 선언도니 객체에서만 사용 가능을 뜻함.
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {
}
