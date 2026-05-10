package com.project.socialX.service.dto.User;

import com.project.socialX.domain.User;
import com.project.socialX.dto.page.FilterRequest;
import com.project.socialX.repository.specification.UserSpecification;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.Specification;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserFilterRequest extends FilterRequest<User> {
    private String query;
    private Long excludedId;

    @Override
    public Specification<User> specification() {
        return UserSpecification.builder()
                .withSearch(query)
                .withoutId(excludedId)
                .build();
    }
}
