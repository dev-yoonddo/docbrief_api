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

     <!-- 에러 모달 -->
    <transition name="fade-slide">
      <div v-if="errorMessage" class="error-overlay">
        <div class="error-modal">
          <h4>요약 처리 중 오류가 발생했습니다</h4>
          <p>{{ errorMessage }}</p>
          <button class="primary" @click="errorMessage = null">
            확인
          </button>
        </div>
      </div>
    </transition>

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
const errorMessage = ref(null);

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
  try{
      // 에러메시지 초기화
      errorMessage.value = null;

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
  }catch(e){
    handleError(e);
  }
}

/**
 * [URL 기준]
 */
async function loadAndParse() {
    try{
        // 에러메시지 초기화
          errorMessage.value = null;

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
    }catch(e){
        handleError(e);
    }
}

/**
 * 에러 발생
 */
function handleError(e) {
  const error = e?.response?.data;

  if (error?.message) {
    errorMessage.value = error.message;
  } else {
    errorMessage.value = "알 수 없는 오류가 발생했습니다.";
  }
}
</script>
