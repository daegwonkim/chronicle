package io.github.daegwonkim.chronicle.repository;

import io.github.daegwonkim.chronicle.repository.condition.SearchLogsCondition;
import io.github.daegwonkim.chronicle.repository.result.SearchLogsResult;

public interface CustomLogRepository {
     SearchLogsResult search(SearchLogsCondition condition);
     long countWithLimit(SearchLogsCondition condition, int limitSize);
}
