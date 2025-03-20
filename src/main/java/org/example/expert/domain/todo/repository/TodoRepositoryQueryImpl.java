package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.QTodoSearchResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;
import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;

@RequiredArgsConstructor
public class TodoRepositoryQueryImpl implements TodoRepositoryQuery{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(todo)
                        .leftJoin(todo.user, user).fetchJoin()
                        .where(todo.id.eq(todoId))
                        .fetchOne()
        );
    }

    @Override
    public Page<TodoSearchResponse> searchTodos(
            String title, String nickname, LocalDateTime start, LocalDateTime end, Pageable pageable
    ) {
        List<TodoSearchResponse> list = jpaQueryFactory
                .select(new QTodoSearchResponse(
                        todo.title,
                        JPAExpressions.select(manager.count())
                                .from(manager)
                                .where(manager.todo.eq(todo)),
                        JPAExpressions.select(comment.count())
                                .from(comment)
                                .where(comment.todo.eq(todo))
                ))
                .from(todo)
                .where(
                        titleContains(title),
                        managerContains(nickname),
                        createdAtBetween(start, end)
                )
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(jpaQueryFactory
                .select(todo.count())
                .from(todo)
                .where(
                        titleContains(title),
                        managerContains(nickname),
                        createdAtBetween(start, end)
                )
                .fetchOne()).orElse(0L);

        return new PageImpl<>(list, pageable, total);
    }

    private BooleanExpression titleContains(String title) {
        return (title != null && !title.isBlank()) ? todo.title.containsIgnoreCase(title) : null;
    }

    private BooleanExpression managerContains(String nickname) {
        return (nickname != null && !nickname.isBlank())
                ? todo.managers.any().user.nickname.containsIgnoreCase(nickname) : null;
    }

    private BooleanExpression createdAtBetween(LocalDateTime start, LocalDateTime end) {
        if(start != null & end != null) {
            return todo.createdAt.between(start, end);
        } else if (start != null) {
            return todo.createdAt.goe(start);
        } else if (end != null) {
            return todo.createdAt.loe(end);
        }
        return null;
    }
}
