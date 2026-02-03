import { useCallback, useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  type Application,
  type GetProjectResponse,
  getProject,
} from '../api/projects';
import {
  type Log,
  type LogLevel,
  type SearchLogsResponse,
  searchLogs,
} from '../api/logs';

const PAGE_SIZE = 20;
const LOG_LEVELS: LogLevel[] = ['TRACE', 'DEBUG', 'INFO', 'WARN', 'ERROR'];

export default function ProjectDashboardPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [project, setProject] = useState<GetProjectResponse | null>(null);
  const [applications, setApplications] = useState<Application[]>([]);
  const [selectedAppIds, setSelectedAppIds] = useState<number[]>([]);
  const [appDropdownOpen, setAppDropdownOpen] = useState(false);
  const appDropdownRef = useRef<HTMLDivElement>(null);

  const [logs, setLogs] = useState<Log[]>([]);
  const [cursorHistory, setCursorHistory] = useState<(number | null)[]>([null]);
  const [currentPage, setCurrentPage] = useState(0);
  const [hasNext, setHasNext] = useState(false);
  const [estimatedCount, setEstimatedCount] = useState<number | null>(null);

  const [selectedLogLevels, setSelectedLogLevels] = useState<LogLevel[]>([]);
  const [logLevelDropdownOpen, setLogLevelDropdownOpen] = useState(false);
  const logLevelDropdownRef = useRef<HTMLDivElement>(null);

  const [searchInput, setSearchInput] = useState('');
  const [query, setQuery] = useState('');
  const [fromTime, setFromTime] = useState('');
  const [toTime, setToTime] = useState('');

  const [projectLoading, setProjectLoading] = useState(true);
  const [logsLoading, setLogsLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!id) return;
    setProjectLoading(true);
    getProject(Number(id))
      .then((res) => {
        if (!res) throw new Error('프로젝트를 찾을 수 없습니다.');
        setProject(res);
        setApplications(res.applications);
        setSelectedAppIds(res.applications.map((app) => app.id));
      })
      .catch((err) => {
        setError(err instanceof Error ? err.message : '프로젝트 조회 실패');
      })
      .finally(() => setProjectLoading(false));
  }, [id]);

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (appDropdownRef.current && !appDropdownRef.current.contains(e.target as Node)) {
        setAppDropdownOpen(false);
      }
      if (logLevelDropdownRef.current && !logLevelDropdownRef.current.contains(e.target as Node)) {
        setLogLevelDropdownOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const fetchLogs = useCallback(async (cursorId: number | null, isFirstPage: boolean) => {
    if (selectedAppIds.length === 0) {
      setLogs([]);
      setHasNext(false);
      if (isFirstPage) setEstimatedCount(null);
      return;
    }
    setLogsLoading(true);
    try {
      const res = (await searchLogs({
        appIds: selectedAppIds,
        from: fromTime ? new Date(fromTime).toISOString() : undefined,
        to: toTime ? new Date(toTime).toISOString() : undefined,
        logLevels: selectedLogLevels.length > 0 ? selectedLogLevels : undefined,
        query: query || undefined,
        cursorId,
        size: PAGE_SIZE,
      })) as SearchLogsResponse;
      setLogs(res.logs);
      setHasNext(res.hasNext);
      if (isFirstPage && res.estimatedCount != null) {
        setEstimatedCount(res.estimatedCount);
      }
    } catch {
      setLogs([]);
      setHasNext(false);
    } finally {
      setLogsLoading(false);
    }
  }, [selectedAppIds, fromTime, toTime, selectedLogLevels, query]);

  useEffect(() => {
    if (!projectLoading && applications.length > 0) {
      const cursorId = cursorHistory[currentPage] ?? null;
      fetchLogs(cursorId, currentPage === 0);
    }
  }, [fetchLogs, projectLoading, applications.length, currentPage, cursorHistory]);

  function resetPagination() {
    setCursorHistory([null]);
    setCurrentPage(0);
    setEstimatedCount(null);
  }

  function toggleApp(appId: number) {
    setSelectedAppIds((prev) =>
      prev.includes(appId) ? prev.filter((i) => i !== appId) : [...prev, appId],
    );
    resetPagination();
  }

  function toggleLogLevel(level: LogLevel) {
    setSelectedLogLevels((prev) =>
      prev.includes(level) ? prev.filter((l) => l !== level) : [...prev, level],
    );
    resetPagination();
  }

  function handleSearch() {
    setQuery(searchInput);
    resetPagination();
  }

  function handleResetFilters() {
    setSelectedAppIds(applications.map((app) => app.id));
    setSelectedLogLevels([]);
    setSearchInput('');
    setQuery('');
    setFromTime('');
    setToTime('');
    resetPagination();
  }

  function handleNextPage() {
    if (!hasNext || logs.length === 0) return;
    const lastLogId = logs[logs.length - 1].id;
    const newPage = currentPage + 1;
    if (cursorHistory.length <= newPage) {
      setCursorHistory((prev) => [...prev, lastLogId]);
    }
    setCurrentPage(newPage);
  }

  function handlePrevPage() {
    if (currentPage <= 0) return;
    setCurrentPage((p) => p - 1);
  }

  function formatTime(isoString: string) {
    const date = new Date(isoString);
    return date.toLocaleString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false,
    });
  }

  function getAppDropdownLabel() {
    if (selectedAppIds.length === 0) return '선택 없음';
    if (selectedAppIds.length === applications.length) return '전체';
    if (selectedAppIds.length === 1) {
      const app = applications.find((a) => a.id === selectedAppIds[0]);
      return app?.name ?? '1개 선택';
    }
    return `${selectedAppIds.length}개 선택`;
  }

  function getLogLevelDropdownLabel() {
    if (selectedLogLevels.length === 0) return '전체';
    if (selectedLogLevels.length === 1) return selectedLogLevels[0];
    return `${selectedLogLevels.length}개 선택`;
  }

  function getEstimatedCountDisplay() {
    if (estimatedCount == null) return '';
    if (estimatedCount > 10000) return '10,000+';
    return estimatedCount.toLocaleString();
  }

  if (projectLoading) {
    return (
      <div className="dashboard-wrapper">
        <div className="dashboard-panel">
          <p className="dashboard-empty">불러오는 중...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="dashboard-wrapper">
        <div className="dashboard-panel">
          <p className="dashboard-empty">{error}</p>
          <div style={{ textAlign: 'center' }}>
            <button className="btn-secondary" onClick={() => navigate('/projects')}>
              프로젝트 목록으로
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-wrapper">
      <div className="dashboard-panel">
        <div className="dashboard-header">
          <button className="btn-back" onClick={() => navigate('/projects')}>
            &larr;
          </button>
          <div>
            <h1 className="dashboard-title">{project?.name}</h1>
            <p className="dashboard-description">{project?.description || '설명 없음'}</p>
          </div>
        </div>

        <div className="dashboard-filters">
          <div className="filter-row">
            <div className="filter-section" ref={appDropdownRef}>
              <label className="filter-label">애플리케이션</label>
              <div className="dropdown-wrapper">
                <button
                  className="filter-select dropdown-trigger"
                  onClick={() => setAppDropdownOpen((v) => !v)}
                >
                  {getAppDropdownLabel()}
                  <span className="dropdown-arrow">{appDropdownOpen ? '▲' : '▼'}</span>
                </button>
                {appDropdownOpen && (
                  <div className="dropdown-menu">
                    <label className="dropdown-item">
                      <input
                        type="checkbox"
                        checked={selectedAppIds.length === applications.length}
                        onChange={() => {
                          if (selectedAppIds.length === applications.length) {
                            setSelectedAppIds([]);
                          } else {
                            setSelectedAppIds(applications.map((a) => a.id));
                          }
                          resetPagination();
                        }}
                      />
                      전체
                    </label>
                    {applications.map((app) => (
                      <label key={app.id} className="dropdown-item">
                        <input
                          type="checkbox"
                          checked={selectedAppIds.includes(app.id)}
                          onChange={() => toggleApp(app.id)}
                        />
                        {app.name}
                      </label>
                    ))}
                    {applications.length === 0 && (
                      <span className="dropdown-item dropdown-hint">등록된 앱 없음</span>
                    )}
                  </div>
                )}
              </div>
            </div>

            <div className="filter-section filter-grow">
              <label className="filter-label">시간 범위</label>
              <div className="time-range">
                <input
                  type="datetime-local"
                  value={fromTime}
                  onChange={(e) => {
                    setFromTime(e.target.value);
                    resetPagination();
                  }}
                />
                <span className="time-sep">~</span>
                <input
                  type="datetime-local"
                  value={toTime}
                  onChange={(e) => {
                    setToTime(e.target.value);
                    resetPagination();
                  }}
                />
              </div>
            </div>

            <div className="filter-section" ref={logLevelDropdownRef}>
              <label className="filter-label">로그 레벨</label>
              <div className="dropdown-wrapper">
                <button
                  className="filter-select dropdown-trigger"
                  onClick={() => setLogLevelDropdownOpen((v) => !v)}
                >
                  {getLogLevelDropdownLabel()}
                  <span className="dropdown-arrow">{logLevelDropdownOpen ? '▲' : '▼'}</span>
                </button>
                {logLevelDropdownOpen && (
                  <div className="dropdown-menu">
                    <label className="dropdown-item">
                      <input
                        type="checkbox"
                        checked={selectedLogLevels.length === 0}
                        onChange={() => {
                          setSelectedLogLevels([]);
                          resetPagination();
                        }}
                      />
                      전체
                    </label>
                    {LOG_LEVELS.map((level) => (
                      <label key={level} className="dropdown-item">
                        <input
                          type="checkbox"
                          checked={selectedLogLevels.includes(level)}
                          onChange={() => toggleLogLevel(level)}
                        />
                        {level}
                      </label>
                    ))}
                  </div>
                )}
              </div>
            </div>

            <div className="filter-section filter-grow">
              <label className="filter-label">검색어</label>
              <div className="search-row">
                <input
                  type="text"
                  className="filter-input"
                  placeholder="메시지 검색..."
                  value={searchInput}
                  onChange={(e) => setSearchInput(e.target.value)}
                  onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
                />
                <button className="btn-primary btn-sm" onClick={handleSearch}>검색</button>
              </div>
            </div>

            <div className="filter-section filter-reset">
              <button className="btn-secondary btn-sm" onClick={handleResetFilters}>초기화</button>
            </div>
          </div>
        </div>

        <div className="dashboard-content">
          {logsLoading ? (
            <p className="dashboard-empty">로그를 불러오는 중...</p>
          ) : logs.length === 0 ? (
            <p className="dashboard-empty">
              {selectedAppIds.length === 0
                ? '애플리케이션을 선택해주세요.'
                : '조건에 맞는 로그가 없습니다.'}
            </p>
          ) : (
            <table className="log-table">
              <thead>
                <tr>
                  <th className="col-time">시간</th>
                  <th className="col-app">애플리케이션</th>
                  <th className="col-level">레벨</th>
                  <th className="col-logger">로거</th>
                  <th className="col-message">메시지</th>
                </tr>
              </thead>
              <tbody>
                {logs.map((log) => (
                  <tr key={log.id}>
                    <td className="col-time">{formatTime(log.loggedAt)}</td>
                    <td className="col-app">{log.appName}</td>
                    <td className="col-level">
                      <span className={`log-level level-${log.level.toLowerCase()}`}>
                        {log.level}
                      </span>
                    </td>
                    <td className="col-logger">{log.logger}</td>
                    <td className="col-message">{log.message}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        <div className="pagination">
          <button
            className="btn-page"
            disabled={currentPage === 0}
            onClick={handlePrevPage}
          >
            이전
          </button>
          <span className="page-info">
            {currentPage + 1} 페이지
            {estimatedCount != null && ` (총 ${getEstimatedCountDisplay()}건)`}
          </span>
          <button
            className="btn-page"
            disabled={!hasNext}
            onClick={handleNextPage}
          >
            다음
          </button>
        </div>
      </div>
    </div>
  );
}
