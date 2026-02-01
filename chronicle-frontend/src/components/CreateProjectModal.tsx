import { useState } from 'react';
import { createProject } from '../api/projects';

interface Props {
  onClose: () => void;
  onCreated: () => void;
}

export default function CreateProjectModal({ onClose, onCreated }: Props) {
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [error, setError] = useState('');
  const [apiKey, setApiKey] = useState('');
  const [loading, setLoading] = useState(false);

  async function handleSubmit() {
    setError('');
    setLoading(true);

    try {
      const res = await createProject({ name, description });
      if (res?.apiKey) {
        setApiKey(res.apiKey);
      } else {
        onCreated();
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : '프로젝트 생성에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        {apiKey ? (
          <>
            <h2 className="modal-title">프로젝트가 생성되었습니다</h2>
            <p className="modal-description">
              아래 API 키를 복사해두세요. 이 키는 다시 확인할 수 없습니다.
            </p>
            <div className="api-key-box">
              <code>{apiKey}</code>
            </div>
            <button className="btn-primary modal-btn" onClick={onCreated}>
              확인
            </button>
          </>
        ) : (
          <>
            <h2 className="modal-title">새 프로젝트</h2>
            <div className="auth-form">
              <div className="form-group">
                <label htmlFor="project-name">프로젝트명</label>
                <input
                  id="project-name"
                  type="text"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="My Project"
                  required
                  autoFocus
                />
              </div>
              <div className="form-group">
                <label htmlFor="project-desc">설명</label>
                <input
                  id="project-desc"
                  type="text"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="프로젝트 설명 (선택)"
                />
              </div>

              {error && <p className="auth-error">{error}</p>}

              <div className="modal-actions">
                <button className="btn-secondary" onClick={onClose}>취소</button>
                <button
                  className="btn-primary"
                  onClick={handleSubmit}
                  disabled={loading || !name.trim()}
                >
                  {loading ? '생성 중...' : '생성'}
                </button>
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
