<template>
    <!-- 보관함 버튼 -->
    <button
      class="archive-toggle fixed top-4 right-4 text-2xl"
      @click="toggleArchive"
    >
      {{ showArchive ? '✖️️' : '📂' }}
      <span
          v-if="archiveCount > 0 && !showArchive"
          class="archive-badge"
        >
          {{ archiveCount }}
      </span>
    </button>
  <div v-if="!showArchive" class="doc-brief">
    <!-- 입력 영역 -->
    <section
      class="input-section"
      :class="{ compact: hasResult }"
    >
      <h2 class="title text-5xl font-extrabold bg-gradient-to-r from-pink-400 to-yellow-300 text-white rounded-full px-6 py-3 shadow-lg animate-bounce">
        DOC <span class="text-white/90">BRIEF</span> ✨
      </h2>
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
  <transition name="archive-slide">
    <!-- 보관함 영역 -->
    <section v-show="showArchive" class="archive-section">

      <h2 class="archive-title">요약 보관함</h2>

      <div v-if="summaryResultList.length === 0" class="empty-text">
        📭 아직 보관된 요약이 없어요.<p />
        파일이나 URL을 업로드해서 첫 요약을 만들어보세요!
      </div>

      <transition-group name="fade-slide" tag="div" class="archive-cards">
        <div
          v-for="item in summaryResultList"
          :key="item.jobId"
          class="summary-card"
          :class="{  }"
          @click="item.selected = !item.selected"
        >
          <input type="checkbox" v-model="item.selected" />
          <div class="card-header">
            <span class="job-id">Job ID: {{ item.jobId }}</span>
            <span class="session-id">Session: {{ item.sessionId }}</span>
          </div>

          <p class="summary-title">{{ item.title }}</p>
          <p class="summary-text">{{ item.summaryResponse.summaryText }}</p>

          <div class="highlights">
            <span
              v-for="(highlight, index) in item.summaryResponse.highlights"
              :key="index"
              :class="highlight.type === 'KEYWORD' ? 'keyword' : 'sentence'"
            >
              {{ highlight.value }}
            </span>
          </div>

          <div class="violation">
            {{ item.summaryResponse.violationReason }}
          </div>
        </div>
      </transition-group>
      <div class="archive-actions" v-if="summaryResultList.length > 0">
          <button
           class="download-btn"
           :disabled="selectedSummaries.length === 0"
           @click="downloadSelected"
         >
           선택한 요약본 다운로드
         </button>
      </div>
    </section>
  </transition>
</template>

<script setup>
import { ref, computed, watch, onMounted } from "vue";
import {
  uploadDocument,
  processDocument,
  summarizeDocument,
  initSession
} from "../api/SummaryApi";

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

/*
* 보관함 영역
*/
const showArchive = ref(false);
const sessionId = ref(null);
const summaryResultList = ref([]); // 세션별 요약 결과 저장
const archiveCount = computed(() => summaryResultList.value.length);
const selectedSummaries = computed(() =>
    summaryResultList.value.filter(item => item.selected)
);


/**
 * 결과 존재 여부 (input 영역 compact 처리용)
 */
const hasResult = computed(() => !!summaryResult.value);

/**
 * 페이지 로드 시 세션 초기화
 */
onMounted(async () => {
    try {
      sessionId.value = await initSession();
    } catch (e) {
      console.warn("session init 실패", e);
    }
    // 새로고침 시에도 보관함 유지
    const saved = localStorage.getItem("summaryArchive");
     if (saved) {
      try {
         summaryResultList.value = JSON.parse(saved);
      } catch {
         localStorage.removeItem("summaryArchive");
      }
     }
});

watch(
  summaryResultList,
  (val) => {
    if (!val || val.length === 0) {
      localStorage.removeItem("summaryArchive");
      return;
    }
    localStorage.setItem(
      "summaryArchive",
      JSON.stringify(val)
    );
  },
  { deep: true }
);


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

  try {
    // 에러메시지 초기화
    errorMessage.value = null;

    //상태 변경
    loadingStage.value = "ANALYZE";

    // 1. 파일 업로드 → documentId 발급
    documentId.value = await uploadDocument(mode.value, file.value, url.value);

    // 2. 문서 파싱 (/documents/process)
    const parseDto = await processDocument(mode.value, documentId.value, file.value, url.value);

    loadingStage.value = "SUMMARY";
    // 3. 요약 요청 (/{documentId}/summary)
    const summary = await summarizeDocument(
        documentId.value,
        parseDto,
        mode.value
    );

    // summary.sessionId와 현재 프론트 세션 ID 비교
    if (summary.sessionId === sessionId.value) {
      summaryResultList.value.push({
            ...summary
              ,selected: false
      }); // 동일 세션이면 리스트에 저장
    }
    console.log("summaryResultList")
    console.log(summaryResultList)
    // 4. 결과 표시
    summaryResult.value = summary.summaryResponse;
  }catch(e){
    handleError(e);
  } finally {
    loadingStage.value = null;
  }
}

/**
 * [URL 기준]
 */
async function loadAndParse() {

    try{
        // 에러메시지 초기화
        errorMessage.value = null;

        //상태 변경
        loadingStage.value = "ANALYZE";

        // 1. documentId 발급
        documentId.value = await uploadDocument(mode.value, file.value, url.value);

        // 2. URL 내부 HTML 파싱 (/url/process)
        const parseDto = await processDocument(mode.value, documentId.value, file.value, url.value);

        loadingStage.value = "SUMMARY";
        // 3. 요약 요청 (/{documentId}/summary)
        const summary = await summarizeDocument(
          documentId.value,
          parseDto,
          mode.value
        );

        // summary.sessionId와 현재 프론트 세션 ID 비교
        if (summary.sessionId === sessionId.value) {
          summaryResultList.value.push(summary); // 동일 세션이면 리스트에 저장
        }
        console.log("summaryResultList")
        console.log(summaryResultList)
        // 4. 결과 표시
        summaryResult.value = summary.summaryResponse;
    }catch(e){
    console.log('오류왜남')
    console.log(e)
        handleError(e);
    } finally {
        loadingStage.value = null;
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

/**
 * 보관함 이동
 */
function toggleArchive() {
    console.log(showArchive)
    showArchive.value = !showArchive.value;
}

watch(showArchive, (isArchive) => {
  // 보관함 이동 시, selected 초기화
  if (isArchive) {
    resetSelected();
  }
  if (!isArchive) {
    resetSelected();
  }
});

function resetSelected() {
  summaryResultList.value.forEach(item => {
    item.selected = false;
  });
}

/**
 * 선택한 요약 다운로드
 */
function downloadSelected() {
  if (selectedSummaries.value.length === 0) return;

  const content = buildDownloadText(selectedSummaries.value);

  const blob = new Blob([content], { type: "text/plain;charset=utf-8" });
  const url = URL.createObjectURL(blob);

  const a = document.createElement("a");
  a.href = url;
  a.download = `summary_${Date.now()}.txt`;
  a.click();

  URL.revokeObjectURL(url);
}

function buildDownloadText(items) {
  return items.map((item, index) => {
    const { title, summaryResponse } = item;
    return `
[${index+1}. ${title ?? "untitled"}]
${summaryResponse.summaryText}

--------------------`;
  }).join("\n");
}
</script>
