package io.github.daegwonkim.chronicle.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.daegwonkim.chronicle.repository.condition.SearchProjectsCondition;
import io.github.daegwonkim.chronicle.repository.result.SearchProjectsResult;
import io.github.daegwonkim.chronicle.vo.ProjectVo;
import lombok.RequiredArgsConstructor;

import static io.github.daegwonkim.chronicle.entity.QProject.project;

@RequiredArgsConstructor
public class CustomProjectRepositoryImpl implements CustomProjectRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public SearchProjectsResult search(SearchProjectsCondition condition) {
        BooleanBuilder searchCondition = buildSearchCondition(condition);

        var projects = queryFactory
                .select(Projections.constructor(ProjectVo.class,
                        project.id,
                        project.name,
                        project.description
                ))
                .from(project)
                .where(searchCondition)
                .orderBy(project.createdAt.desc())
                .offset((long) condition.page() * condition.size())
                .limit(condition.size())
                .fetch();

        Long totalCount = queryFactory
                .select(project.count())
                .from(project)
                .where(searchCondition)
                .fetchOne();

        return new SearchProjectsResult(projects, totalCount != null ? totalCount : 0L);
    }

    private BooleanBuilder buildSearchCondition(SearchProjectsCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(project.adminId.eq(condition.adminId()));
        if (condition.query() != null && !condition.query().isBlank()) {
            builder.and(project.name.like("%" + condition.query() + "%"));
        }
        builder.and(project.deleted.eq(false));
        return builder;
    }
}
