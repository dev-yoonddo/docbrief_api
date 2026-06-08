import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true
});

/**
 * 여러 파일을 동시 업로드하여 비교분석 Job 생성
 * POST /comparisons
 * @param {File[]} files - 업로드할 파일 배열
 * @returns {{ jobId: number, documentIds: number[] }}
 */
export async function createComparisonJob(files) {
  const formData = new FormData();
  files.forEach((file) => {
    formData.append("files", file);
  });

  const res = await api.post("/comparisons", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });

  return res.data; // { jobId, documentIds }
}

/**
 * 비교분석 결과 조회
 * GET /comparisons/{id}
 * @param {number} comparisonId - 비교분석 Job ID
 * @returns {{ agreements, differences, conflicts }}
 */
export async function getComparisonResult(comparisonId) {
  try {
    const res = await api.get(`/comparisons/${comparisonId}`);
    return res.data;
  } catch (error) {
    handleApiError(error);
  }
}

/**
 * 비교분석 상태 조회
 * GET /comparisons/{id}/status
 * @param {number} comparisonId - 비교분석 Job ID
 * @returns {{ status: string, progress: number }}
 */
export async function getComparisonStatus(comparisonId) {
  try {
    const res = await api.get(`/comparisons/${comparisonId}/status`);
    return res.data;
  } catch (error) {
    handleApiError(error);
  }
}

/**
 * 비교분석 처리 요청
 * POST /comparisons/{id}/process
 * @param {number} comparisonId - 비교분석 Job ID
 * @param {string} mode - 처리 모드 (선택사항)
 * @returns {{ comparisonId, status }}
 */
export async function processComparison(comparisonId, mode = null) {
  try {
    const params = mode ? { mode } : {};
    const res = await api.post(`/comparisons/${comparisonId}/process`, {}, { params });
    return res.data;
  } catch (error) {
    handleApiError(error);
  }
}

/**
 * 문서 업로드 (비교용)
 * POST /documents
 */
export async function uploadDocument(mode, file, url, text) {
  const formData = new FormData();
  formData.append("mode", mode);
  if (file) formData.append("file", file);
  if (url) formData.append("url", url);
  if (text) formData.append("text", text);

  const res = await api.post("/documents", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });

  return res.data.documentId;
}

/**
 * 문서 파싱
 * POST /documents/{documentId}/process
 */
export async function processDocument(mode, documentId, file, url, text) {
  const formData = new FormData();
  formData.append("mode", mode);
  if (file) formData.append("file", file);
  if (url) formData.append("url", url);
  if (text) formData.append("text", text);

  const res = await api.post(
    `/documents/${documentId}/process`,
    formData,
    {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    }
  );

  return res.data;
}

/**
 * API 에러 처리 헬퍼 함수
 * HTTP 상태 코드별로 에러를 처리합니다
 */
function handleApiError(error) {
  if (error.response) {
    // 서버가 응답했지만 상태 코드가 2xx가 아닌 경우
    const status = error.response.status;
    const data = error.response.data;

    const errorMessage = data?.message || `API 요청 실패 (상태: ${status})`;

    if (status === 400) {
      throw new Error(`잘못된 요청: ${errorMessage}`);
    } else if (status === 401) {
      throw new Error("인증이 필요합니다");
    } else if (status === 403) {
      throw new Error("접근 권한이 없습니다");
    } else if (status === 404) {
      throw new Error("요청한 리소스를 찾을 수 없습니다");
    } else if (status === 500) {
      throw new Error(`서버 오류: ${errorMessage}`);
    } else {
      throw new Error(errorMessage);
    }
  } else if (error.request) {
    // 요청을 했지만 응답이 없는 경우
    throw new Error("서버에 연결할 수 없습니다");
  } else {
    // 요청 생성 중 에러
    throw new Error(`요청 오류: ${error.message}`);
  }
}
