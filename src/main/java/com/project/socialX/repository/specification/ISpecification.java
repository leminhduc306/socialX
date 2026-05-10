package com.project.socialX.repository.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ISpecification<T> {

    protected static final String FIELD_ID = "id";

    private final List<Specification<T>> specifications = new ArrayList<>();

    public Specification<T> build() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(specifications.stream()
                .filter(Objects::nonNull)
                .map(s -> s.toPredicate(root, query, criteriaBuilder)).toArray(Predicate[]::new));
    }

    protected void add(Specification<T> spec) {
        this.specifications.add(spec);
    }
}

