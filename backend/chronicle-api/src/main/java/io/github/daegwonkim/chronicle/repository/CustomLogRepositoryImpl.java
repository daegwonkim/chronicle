package io.github.daegwonkim.chronicle.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.github.daegwonkim.chronicle.enumerate.LogLevel;
import io.github.daegwonkim.chronicle.repository.condition.SearchLogsCondition;
import io.github.daegwonkim.chronicle.repository.result.SearchLogsResult;
import io.github.daegwonkim.chronicle.vo.LogVo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static io.github.daegwonkim.chronicle.entity.QLog.log;
import static io.github.daegwonkim.chronicle.entity.QApplication.application;

@RequiredArgsConstructor
public class CustomLogRepositoryImpl implements CustomLogRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Override
    public SearchLogsResult search(SearchLogsCondition condition) {
        BooleanBuilder searchCondition = buildSearchCondition(condition);

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
                .orderBy(log.loggedAt.desc())
                .limit(condition.size() + 1)
                .fetch();

        boolean hasNext = fetched.size() > condition.size();
        List<LogVo> logs = hasNext ? fetched.subList(0, condition.size()) : fetched;

        return new SearchLogsResult(logs, hasNext);
    }

    @Override
    public long countWithLimit(SearchLogsCondition condition, int limitSize) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM (SELECT 1 FROM logs WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (condition.appIds() != null && !condition.appIds().isEmpty()) {
            sql.append(" AND app_id IN (?");
            for (int i = 1; i < condition.appIds().size(); i++) {
                sql.append(", ?");
            }
            sql.append(")");
            params.addAll(condition.appIds());
        }

        if (condition.logLevels() != null && !condition.logLevels().isEmpty()) {
            sql.append(" AND level IN (?");
            for (int i = 1; i < condition.logLevels().size(); i++) {
                sql.append(", ?");
            }
            sql.append(")");
            for (LogLevel level : condition.logLevels()) {
                params.add(level.name());
            }
        }

        if (condition.timeRange() != null) {
            if (condition.timeRange().from() != null) {
                sql.append(" AND logged_at >= ?");
                params.add(condition.timeRange().from());
            }
            if (condition.timeRange().to() != null) {
                sql.append(" AND logged_at <= ?");
                params.add(condition.timeRange().to());
            }
        }

        if (condition.query() != null && !condition.query().isBlank()) {
            sql.append(" AND message LIKE ?");
            params.add("%" + condition.query() + "%");
        }

        sql.append(" LIMIT ?) AS temp");
        params.add(limitSize);

        Query query = entityManager.createNativeQuery(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            query.setParameter(i + 1, params.get(i));
        }

        return ((Number) query.getSingleResult()).longValue();
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
