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
