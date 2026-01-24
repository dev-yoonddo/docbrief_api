import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

/**
 * 문서 업로드
 * POST /documents
 */
export async function uploadDocument(file) {
  const formData = new FormData();
  formData.append("file", file);

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
export async function processDocument(documentId, file) {
  const formData = new FormData();
  formData.append("file", file);

  const res = await api.post(
    `/documents/${documentId}/process`,
    formData,
    {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    }
  );

  return res.data; // 👉 parsed DTO 전체
}

/**
 * 문서 요약
 * POST /{documentId}/summary
 */
export async function summarizeDocument(documentId, parseDto, type = "document") {
  const res = await api.post(
    `/${documentId}/summary`,
    parseDto,
    {
      params: {
        type,
      },
      headers: {
        "Content-Type": "application/json",
      },
    }
  );

  return res.data; // 👉 String 요약 결과
}

