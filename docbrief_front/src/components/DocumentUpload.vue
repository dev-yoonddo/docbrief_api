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
          @click="switchMode('file')"
        >
          파일 업로드
        </button>

        <button
          :class="{ active: mode === 'url' }"
          @click="switchMode('url')"
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
          :disabled="!file || isLoading"
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
        <button
          class="primary"
          @click="loadAndParse"
          :disabled="!url || isLoading"
        >
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
        <section v-if="summaryResult" class="summary-section">
            <h3>요약 결과</h3>
            <pre class="result-box">
                <p v-html="highlightedSummary"></p>
            </pre>
        </section>
    </transition>
  </div>

  <!-- 로딩 오버레이 컨테이너 -->
  <section v-if="summaryResult || isLoading" class="summary-wrapper">

    <!-- 로딩 오버레이 -->
    <transition name="fade">
      <div v-if="isLoading" class="loading-overlay">
        <div class="loading-content">
          <div class="loading-box">
            <div class="loading-bar"></div>
          </div>
          <p class="loading-text">
            {{ loadingText }}
          </p>
        </div>
      </div>
    </transition>

  </section>
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
const summaryResult = ref(null);
const loadingStage = ref(null);



/**
 * 결과 존재 여부 (input 영역 compact 처리용)
 */
const hasResult = computed(() => !!summaryResult.value);

/*
* 로딩 완료 여부/**
* ANALYZE: 문서 분석 중
* SUMMARY: 요약 생성 중
*/
const isLoading = computed(() => loadingStage.value !== null);

/**
 * 요약 결과 키워드 추출
 */
const highlightedSummary = computed(() => {
  if (!summaryResult.value) return "";

  let text = summaryResult.value.summaryText;

  const keywords = summaryResult.value.highlights
    .filter(h => h.type === "KEYWORD")
    .map(h => h.value);

  keywords.forEach(keyword => {
    const escaped = keyword.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
    const regex = new RegExp(`(${escaped})`, "g");

    text = text.replace(
      regex,
      `<span class="keyword-highlight">$1</span>`
    );
  });

  return text;
});

/**
 * 로딩 텍스트
 */
const loadingText = computed(() => {
  if (loadingStage.value === "ANALYZE") {
    return "문서를 분석 중입니다…";
  }
  if (loadingStage.value === "SUMMARY") {
    return "문서를 요약 중입니다…";
  }
  return "";
});

/**
 * 파일 선택
 */
function onFileChange(e) {
  file.value = e.target.files[0];
  resetSummary();
}

/**
 * [파일 기준] 업로드 → 파싱 → 요약
 */
async function uploadAndParse() {
  isLoading.value = true;

  try {
    // 에러메시지 초기화
    errorMessage.value = null;

    //상태 변경
    loadingStage.value = "ANALYZE";

    // 1. 파일 업로드 → documentId 발급
    documentId.value = await uploadDocument(file.value);

    // 2. 문서 파싱 (/documents/process)
    const parseDto = await processDocument(documentId.value, file.value);

    loadingStage.value = "SUMMARY";
    // 3. 요약 요청 (/{documentId}/summary)
    const summary = await summarizeDocument(
    documentId.value,
    parseDto
    );

    // 4. 결과 표시
    summaryResult.value = summary;
  }catch(e){
    handleError(e);
  } finally {
    isLoading.value = false;
  }
}

/**
 * [URL 기준]
 */
async function loadAndParse() {
    isLoading.value = true;

    try{
        // 에러메시지 초기화
        errorMessage.value = null;

        //상태 변경
        loadingStage.value = "ANALYZE";

        // 1. documentId 발급
        documentId.value = await uploadUrl(url.value);

        // 2. URL 내부 HTML 파싱 (/url/process)
        const parseDto = await processUrl(documentId.value, url.value);

        loadingStage.value = "SUMMARY";
        // 3. 요약 요청 (/{documentId}/summary)
        const summaryDto = await summarizeDocument(
          documentId.value,
          parseDto
        );

        // 4. 결과 표시
        summaryResult.value = summaryDto;
    }catch(e){
        handleError(e);
    } finally {
        isLoading.value = false;
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

/**
 * 파일업로드,URL 버튼 클릭이벤트
 */
function switchMode(nextMode) {
  if (mode.value !== nextMode) {
    mode.value = nextMode;

    // 입력값도 같이 초기화
    file.value = null;
    url.value = null;

    resetSummary();
  }
}

/**
 * 요약 내용 초기화
 */
function resetSummary() {
  summaryResult.value = null;
  documentId.value = null;
}
</script>
