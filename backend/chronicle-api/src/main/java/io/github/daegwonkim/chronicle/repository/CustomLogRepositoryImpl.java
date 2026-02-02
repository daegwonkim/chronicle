package io.github.daegwonkim.chronicle.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.daegwonkim.chronicle.repository.condition.SearchLogsCondition;
import io.github.daegwonkim.chronicle.repository.result.SearchLogsResult;
import io.github.daegwonkim.chronicle.vo.LogVo;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.github.daegwonkim.chronicle.entity.QLog.log;
import static io.github.daegwonkim.chronicle.entity.QApplication.application;

@RequiredArgsConstructor
public class CustomLogRepositoryImpl implements CustomLogRepository {

    private final JPAQueryFactory queryFactory;

    private static final int ESTIMATED_COUNT_LIMIT = 10_000;

    @Override
    public SearchLogsResult search(SearchLogsCondition condition) {
        BooleanBuilder searchCondition = buildSearchCondition(condition);

        Long estimatedCount = null;
        if (condition.cursorId() == null) {
            estimatedCount = (long) queryFactory
                    .select(log.id)
                    .from(log)
                    .where(searchCondition)
                    .limit(ESTIMATED_COUNT_LIMIT + 1)
                    .fetch()
                    .size();
        }

        if (condition.cursorId() != null) {
            searchCondition.and(log.id.lt(condition.cursorId()));
        }

        var fetched = queryFactory
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
                .orderBy(log.id.desc())
                .limit(condition.size() + 1)
                .fetch();

        boolean hasNext = fetched.size() > condition.size();
        List<LogVo> logs = hasNext ? fetched.subList(0, condition.size()) : fetched;

        return new SearchLogsResult(logs, hasNext, estimatedCount);
    }

    private BooleanBuilder buildSearchCondition(SearchLogsCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();

        if (condition.appIds() != null) {
            builder.and(log.appId.in(condition.appIds()));
        }
        if (condition.logLevels() != null) {
            builder.and(log.level.in(condition.logLevels()));
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
