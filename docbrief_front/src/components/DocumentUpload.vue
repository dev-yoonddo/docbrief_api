<template>
  <div class="doc-brief">

    <!-- 입력 영역 -->
    <section
      class="input-section"
      :class="{ compact: hasResult }"
    >
      <h2 class="title">DOC BRIEF</h2>

      <!-- 입력 방식 탭 -->
      <div class="input-tabs">
        <button
          :class="{ active: mode === 'file' }"
          @click="mode = 'file'"
        >
          파일 업로드
        </button>
        <button
          :class="{ active: mode === 'url' }"
          @click="mode = 'url'"
        >
          URL 입력
        </button>
      </div>

      <!-- 파일 업로드 -->
      <div v-if="mode === 'file'" class="input-box">
        <input type="file" @change="onFileChange" />
        <button
          class="primary"
          @click="uploadAndParse"
          :disabled="!file"
        >
          업로드
        </button>
      </div>

      <!-- URL 입력 -->
      <div v-if="mode === 'url'" class="input-box">
        <input
          type="text"
          v-model="url"
          placeholder="https://example.com/document"
        />
        <button class="primary" @click="loadAndParse">
          불러오기
        </button>
      </div>
    </section>

    <!-- 요약 결과 -->
    <transition name="fade-slide">
      <section v-if="parseResult" class="summary-section">
        <h3>요약 결과</h3>
        <pre class="result-box">
{{ parseResult }}
        </pre>
      </section>
    </transition>

  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import {
  uploadDocument,
  processDocument,
  summarizeDocument,
  uploadUrl,
  processUrl
} from "../api/documentApi";

/**
 * UI 상태
 */
const mode = ref("file"); // 'file' | 'url'
const file = ref(null);
const url = ref(null);
const documentId = ref(null);
const parseResult = ref(null);

/**
 * 결과 존재 여부 (input 영역 compact 처리용)
 */
const hasResult = computed(() => !!parseResult.value);

/**
 * 파일 선택
 */
function onFileChange(e) {
  file.value = e.target.files[0];
}

/**
 * [파일 기준] 업로드 → 파싱 → 요약
 */
async function uploadAndParse() {
  // 1. 파일 업로드 → documentId 발급
  documentId.value = await uploadDocument(file.value);

  // 2. 문서 파싱 (/documents/process)
  const parseDto = await processDocument(documentId.value, file.value);

  // 3. 요약 요청 (/{documentId}/summary)
  const summary = await summarizeDocument(
    documentId.value,
    parseDto
  );

  // 4. 결과 표시
  parseResult.value = summary;
}

/**
 * [URL 기준]
 */
async function loadAndParse() {
    // 1. documentId 발급
    documentId.value = await uploadUrl(url.value);

    // 2. URL 내부 HTML 파싱 (/url/process)
    const parseDto = await processUrl(documentId.value, url.value);

    // 3. 요약 요청 (/{documentId}/summary)
    const summary = await summarizeDocument(
      documentId.value,
      parseDto
    );

    // 4. 결과 표시
    parseResult.value = summary;
}
</script>
