package com.project.socialX.common.utils;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SpecificationUtil {

    public String like(final String value) {
        return "%" + value.toUpperCase() + "%";
    }

    public Predicate like(CriteriaBuilder cb, Path<String> field, final String value) {
        return cb.like(cb.upper(field), like(value));
    }

    public Predicate equal(CriteriaBuilder cb, Path<?> field, final Object value) {
        return cb.equal(field, value);
    }

}
