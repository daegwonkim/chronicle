package io.github.daegwonkim.chronicle.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.daegwonkim.chronicle.repository.condition.SearchLogsCondition;
import io.github.daegwonkim.chronicle.repository.result.SearchLogsResult;
import io.github.daegwonkim.chronicle.vo.LogVo;
import lombok.RequiredArgsConstructor;

import static io.github.daegwonkim.chronicle.entity.QLog.log;
import static io.github.daegwonkim.chronicle.entity.QApplication.application;

@RequiredArgsConstructor
public class CustomLogRepositoryImpl implements CustomLogRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public SearchLogsResult search(SearchLogsCondition condition) {
        BooleanBuilder searchCondition = buildSearchCondition(condition);

        var logs = queryFactory
                .select(Projections.constructor(LogVo.class,
                        log.id,
                        application.name,
                        log.level,
                        log.message,
                        log.logger,
                        log.loggedAt
                ))
                .from(log)
                .join(application).on(log.appId.eq(application.id))
                .where(searchCondition)
                .orderBy(log.loggedAt.desc())
                .offset((long) condition.page() * condition.size())
                .limit(condition.size())
                .fetch();

        Long totalCount = queryFactory
                .select(log.count())
                .from(log)
                .where(searchCondition)
                .fetchOne();

        return new SearchLogsResult(logs, totalCount != null ? totalCount : 0L);
    }

    private BooleanBuilder buildSearchCondition(SearchLogsCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(log.appId.eq(condition.appId()));

        if (condition.logLevel() != null) {
            builder.and(log.level.eq(condition.logLevel()));
        }
        if (condition.timeRange() != null) {
            if (condition.timeRange().from() != null) {
                builder.and(log.loggedAt.goe(condition.timeRange().from()));
            }
            if (condition.timeRange().to() != null) {
                builder.and(log.loggedAt.loe(condition.timeRange().to()));
            }
        }
        if (condition.query() != null && !condition.query().isBlank()) {
            builder.and(log.message.contains(condition.query()));
        }

        return builder;
    }
}
