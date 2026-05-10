package com.project.socialX.repository.specification;

import com.project.socialX.common.utils.SpecificationUtil;
import com.project.socialX.domain.User;
import jakarta.persistence.criteria.JoinType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSpecification extends ISpecification<User> {

    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_USER_DETAILS = "userDetails";
    private static final String FIELD_BIO = "bio";

    public static UserSpecification builder() {
        return new UserSpecification();
    }

    public UserSpecification withSearch(String query) {
        if (!ObjectUtils.isEmpty(query)) {
            this.add((root, criteriaQuery, cb) ->
                    cb.or(
                            SpecificationUtil.like(cb, root.get(FIELD_USERNAME), query),
                            SpecificationUtil.like(cb, root.get(FIELD_EMAIL), query)
                    )
            );
        }
        return this;
    }

    public UserSpecification withBio(String bio) {
        if (!ObjectUtils.isEmpty(bio)) {
            this.add((root, criteriaQuery, cb) ->
                    SpecificationUtil.like(cb, root.join(FIELD_USER_DETAILS, JoinType.LEFT).get(FIELD_BIO), bio)
            );
        }
        return this;
    }

    public UserSpecification withoutId(Long id) {
        if (id != null) {
            this.add((root, criteriaQuery, cb) ->
                    cb.notEqual(root.get(FIELD_ID), id)
            );
        }
        return this;
    }
}
