package com.vrto;

import lombok.val;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;

import java.lang.reflect.ParameterizedType;
import java.time.ZonedDateTime;

@Aspect
public class EntityStamper {

    @Before("execution(public * org.springframework.data.repository.Repository+.*(..))")
    public void setTimestampsOnEntities(JoinPoint joinPoint) throws NoSuchMethodException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // only intercept save calls - that's when timestamp setting should happen
        if (!signature.getName().contains("save")) {
            return;
        }

        // need to go through target.class as Spring Data repositories will be proxied
        Class<?> domainRepositoryType = AopProxyUtils.proxiedUserInterfaces(joinPoint.getTarget())[0];

        // every Spring JPA repo has two generic parameters - entity type and key type
        Class<?> entityType = (Class<?>) ((ParameterizedType) domainRepositoryType.getGenericInterfaces()[0]).getActualTypeArguments()[0];

        if (!TimestampAware.class.isAssignableFrom(entityType)) {
            return;
        }

        val argBeingSaved = joinPoint.getArgs()[0];

        // save (single object or iteralbe of objects)
        if (argBeingSaved instanceof Iterable) {
            timestampAll((Iterable<TimestampAware<?>>) argBeingSaved);
        } else {
            timestampOne((TimestampAware<?>) argBeingSaved);
        }

    }

    private void timestampAll(Iterable<TimestampAware<?>> entities) {
        entities.forEach(this::timestampOne);
    }

    private void timestampOne(TimestampAware<?> withCreatedAt) {
        val now = ZonedDateTime.now();

        // don't override creation date once it's been set
        if (withCreatedAt.getId() == null) {
            withCreatedAt.setCreatedAt(now);
        }
        withCreatedAt.setUpdatedAt(now);
    }

}
