package com.xyz.desensitize.aspects;

import com.xyz.desensitize.util.DesensitizeUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 接口返回结果的脱敏切面处理
 *
 * @author dev-sxl
 */
@Slf4j
@Aspect
public class DesensitizeAspect {

    @Around("@annotation(com.xyz.desensitize.annotation.Desensitize)")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        Object o = pjp.proceed();
        try {
            return DesensitizeUtil.desensitizeObj(o);
        } catch (Exception e) {
            log.warn("脱敏失败,response: {}", o);
            log.warn("脱敏失败,异常堆栈:", e);
        }
        return o;
    }
}
