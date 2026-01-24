import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

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

export async function parseDocument(documentId, file) {
    const formData = new FormData();
    formData.append("file", file);

    const res = await api.post(`/documents/${documentId}/process`, formData, {
        headers: {
            "Content-Type": "multipart/form-data",
        },
    });
    return res.data.fullText; // parsed DTO
}
