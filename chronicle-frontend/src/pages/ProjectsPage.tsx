import { useCallback, useEffect, useState } from 'react';
import { type GetProjectsResponse, type Project, getProjects } from '../api/projects';
import CreateProjectModal from '../components/CreateProjectModal';

const PAGE_SIZE = 10;

export default function ProjectsPage() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [totalCount, setTotalCount] = useState(0);
  const [page, setPage] = useState(0);
  const [query, setQuery] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [showModal, setShowModal] = useState(false);

  const totalPages = Math.max(1, Math.ceil(totalCount / PAGE_SIZE));

  const fetchProjects = useCallback(async () => {
    setLoading(true);
    try {
      const res = (await getProjects({ query, page, size: PAGE_SIZE })) as GetProjectsResponse;
      setProjects(res.projects);
      setTotalCount(res.totalCount);
    } catch {
      setProjects([]);
      setTotalCount(0);
    } finally {
      setLoading(false);
    }
  }, [query, page]);

  useEffect(() => {
    fetchProjects();
  }, [fetchProjects]);

  function handleSearch() {
    setQuery(searchInput);
    setPage(0);
  }

  function handleProjectCreated() {
    setShowModal(false);
    setQuery('');
    setSearchInput('');
    setPage(0);
    fetchProjects();
  }

  return (
    <div className="projects-wrapper">
      <div className="projects-panel">
        <div className="panel-sidebar">
          <h1 className="panel-title">Chronicle</h1>
          <p className="panel-subtitle">Projects</p>
        </div>

        <div className="panel-main">
          <div className="panel-toolbar">
            <div className="projects-search">
              <input
                type="text"
                placeholder="프로젝트 검색..."
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
              />
            </div>
            <button className="btn-primary" onClick={() => setShowModal(true)}>
              + 새 프로젝트
            </button>
          </div>

          <div className="panel-content">
            {loading ? (
              <p className="projects-empty">불러오는 중...</p>
            ) : projects.length === 0 ? (
              <p className="projects-empty">
                {query ? '검색 결과가 없습니다.' : '프로젝트가 없습니다. 새 프로젝트를 생성해보세요.'}
              </p>
            ) : (
              <ul className="projects-list">
                {projects.map((project) => (
                  <li key={project.id} className="project-card">
                    <h3 className="project-name">{project.name}</h3>
                    <p className="project-description">{project.description || '설명 없음'}</p>
                  </li>
                ))}
              </ul>
            )}
          </div>

          {totalPages > 1 && (
            <div className="pagination">
              <button
                className="btn-page"
                disabled={page === 0}
                onClick={() => setPage((p) => p - 1)}
              >
                이전
              </button>
              <span className="page-info">
                {page + 1} / {totalPages}
              </span>
              <button
                className="btn-page"
                disabled={page >= totalPages - 1}
                onClick={() => setPage((p) => p + 1)}
              >
                다음
              </button>
            </div>
          )}
        </div>
      </div>

      {showModal && (
        <CreateProjectModal
          onClose={() => setShowModal(false)}
          onCreated={handleProjectCreated}
        />
      )}
    </div>
  );
}
