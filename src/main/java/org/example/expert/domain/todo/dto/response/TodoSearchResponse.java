package org.example.expert.domain.todo.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class TodoSearchResponse {
    private final String title;
    private final long totalManagers;
    private final long totalComments;

    @QueryProjection
    public TodoSearchResponse(String title, long totalManagers, long totalComments) {
        this.title = title;
        this.totalManagers = totalManagers;
        this.totalComments = totalComments;
    }
}
