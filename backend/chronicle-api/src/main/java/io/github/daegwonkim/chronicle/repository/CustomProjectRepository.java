package io.github.daegwonkim.chronicle.repository;

import io.github.daegwonkim.chronicle.repository.condition.SearchProjectsCondition;
import io.github.daegwonkim.chronicle.repository.result.SearchProjectsResult;

public interface CustomProjectRepository {
    SearchProjectsResult search(SearchProjectsCondition condition);
}
