package msg.flight.manager.persistence.repositories.utils.criteria;

import msg.flight.manager.persistence.repositories.utils.criteria.annotations.BaseCriteria;
import msg.flight.manager.persistence.repositories.utils.criteria.annotations.CompoundCriteria;
import msg.flight.manager.persistence.repositories.utils.criteria.annotations.CompoundCriteriaPart;
import msg.flight.manager.persistence.repositories.utils.criteria.annotations.SimpleCriteria;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Aspect
@Component
public class CriteriaAspect {
    @Pointcut("execution(* *(..)) && within(msg.flight..*) && target( msg.flight.manager.persistence.repositories.utils.criteria.CriteriaBuilder)")
    public void allCriteriaMethods() {
    }

    @Around("allCriteriaMethods()")
    public Criteria methodGenerator(JoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        SimpleCriteria[] simpleCriteriaAnnotations = method.getAnnotationsByType(SimpleCriteria.class);
        CompoundCriteria[] compoundCriteriaAnnotations = method.getAnnotationsByType(CompoundCriteria.class);
        BaseCriteria[] baseCriteriaAnnotation = method.getAnnotationsByType(BaseCriteria.class);
        Map<String, CriteriaArgument> criteriaParameters = generateParameterMap(method.getParameters(), joinPoint.getArgs());
        if (simpleCriteriaAnnotations.length + compoundCriteriaAnnotations.length == 1) {
            if (simpleCriteriaAnnotations.length == 1) {
                String criteriaName = simpleCriteriaAnnotations[0].name();
                SimpleCriteriaBuilder criteriaBuilder = criteriaParameters.get(criteriaName).getSimpleCriteriaArgument();
                return criteriaBuilder.createCriteria();
            } else {
                return createCompoundCriteria(compoundCriteriaAnnotations[0], criteriaParameters);
            }
        } else {
            if (baseCriteriaAnnotation.length != 1) {
                throw new InvalidPropertiesFormatException("Invalid configuration for " + method.getName() + " method");
            }
            List<Criteria> criteriaParts = new ArrayList<>();
            for (CompoundCriteria compoundCriteria : compoundCriteriaAnnotations) {
                criteriaParts.add(createCompoundCriteria(compoundCriteria, criteriaParameters));
            }
            for (SimpleCriteria simpleCriteria : simpleCriteriaAnnotations) {
                String criteriaName = simpleCriteria.name();
                SimpleCriteriaBuilder criteriaBuilder = criteriaParameters.get(criteriaName).getSimpleCriteriaArgument();
                criteriaParts.add(criteriaBuilder.createCriteria());
            }
            return createBaseCriteria(criteriaParts, baseCriteriaAnnotation[0].operator());
        }
    }

    private Criteria createCompoundCriteria(CompoundCriteria criteria, Map<String, CriteriaArgument> criteriaPrams) {
        SimpleCriteria[] simpleCriterias = criteria.simpleCriterias();
        CompoundCriteriaPart[] compoundCriterias = criteria.compoundCriterias();
        String multi = criteria.multi();
        List<Criteria> criteriaParts = getSimpleCriteriaList(criteriaPrams, simpleCriterias);
        for (CompoundCriteriaPart compoundCriteria : compoundCriterias) {
            criteriaParts.addAll(getSimpleCriteriaList(criteriaPrams, compoundCriteria.simpleCriterias()));
        }
        if (!multi.isBlank()) {
            CriteriaArgument argument = criteriaPrams.getOrDefault(multi, null);
            if (argument == null) {
                throw new RuntimeException("Invalid criteria configuration");
            }
            argument.getArrayCriteriaArgument().stream().map(SimpleCriteriaBuilder::createCriteria).forEach(criteriaParts::add);
        }
        return createBaseCriteria(criteriaParts, criteria.operator());
    }

    private static List<Criteria> getSimpleCriteriaList(Map<String, CriteriaArgument> criteriaPrams, SimpleCriteria[] simpleCriterias) {
        List<Criteria> criteriaParts = new ArrayList<>();
        for (SimpleCriteria simpleCriteria : simpleCriterias) {
            String criteriaName = simpleCriteria.name();
            CriteriaArgument criteriaBuilder = criteriaPrams.getOrDefault(criteriaName, null);
            if (criteriaBuilder == null) {
                throw new RuntimeException("Invalid  criteriaConfiguration");
            }
            criteriaParts.add(criteriaBuilder.getSimpleCriteriaArgument().createCriteria());
        }
        return criteriaParts;
    }

    private Map<String, CriteriaArgument> generateParameterMap(Parameter[] parameters, Object[] args) {
        Map<String, CriteriaArgument> criteriaMap = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String name = parameter.getName();
            criteriaMap.put(name, new CriteriaArgument(args[i]));
        }
        return criteriaMap;
    }

    private Criteria createBaseCriteria(List<Criteria> criteriaParts, String operator) {
        Criteria criteria = new Criteria();
        return switch (operator) {
            case "and" -> criteria.andOperator(criteriaParts);
            case "or" -> criteria.orOperator(criteriaParts);
            default -> criteria;
        };
    }


}
