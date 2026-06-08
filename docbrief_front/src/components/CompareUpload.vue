<template>
  <div class="compare-upload">
    <!-- 헤더 -->
    <header class="compare-header">
      <h1 class="compare-title">문서 비교분석</h1>
      <p class="compare-subtitle">두 문서를 업로드하여 내용을 비교분석합니다</p>
    </header>

    <!-- 에러 모달 -->
    <transition name="fade-slide">
      <div v-if="errorMessage" class="error-overlay">
        <div class="error-modal">
          <h4>비교분석 중 오류가 발생했습니다</h4>
          <p>{{ errorMessage }}</p>
          <button class="primary" @click="errorMessage = null">
            확인
          </button>
        </div>
      </div>
    </transition>

    <!-- 문서 업로드 영역 -->
    <div class="document-containers">
      <!-- 문서 A -->
      <div class="document-upload-section">
        <h2>문서 A</h2>
        <div class="input-tabs">
          <button
            :class="{ active: modeA === 'file' }"
            @click="switchModeA('file')"
          >
            파일
          </button>
          <button
            :class="{ active: modeA === 'url' }"
            @click="switchModeA('url')"
          >
            URL
          </button>
          <button
            :class="{ active: modeA === 'text' }"
            @click="switchModeA('text')"
          >
            텍스트
          </button>
        </div>

        <!-- 파일 업로드 -->
        <div v-if="modeA === 'file'" class="input-box">
          <input type="file" @change="onFileChangeA" />
          <button
            class="primary"
            @click="uploadDocumentA"
            :disabled="!fileA || isLoadingA"
          >
            업로드
          </button>
        </div>

        <!-- URL 입력 -->
        <div v-if="modeA === 'url'" class="input-box">
          <input
            type="text"
            v-model="urlA"
            placeholder="https://example.com/document"
          />
          <button
            class="primary"
            @click="uploadDocumentA"
            :disabled="!urlA || isLoadingA"
          >
            불러오기
          </button>
        </div>

        <!-- 텍스트 직접 입력 -->
        <div v-if="modeA === 'text'" class="input-box text-input-box">
          <textarea
            v-model="textA"
            placeholder="비교할 텍스트를 입력하세요..."
            rows="6"
          />
          <button
            class="primary"
            @click="uploadDocumentA"
            :disabled="!textA.trim() || isLoadingA"
          >
            입력 완료
          </button>
        </div>

        <!-- 업로드 완료 표시 -->
        <div v-if="documentIdA" class="upload-success">
          <span class="success-icon">✓</span>
          <span>문서 A 준비 완료</span>
        </div>

        <!-- 로딩 표시 A -->
        <div v-if="isLoadingA" class="loading-indicator">
          <span class="spinner"></span>
          <span>업로드 중...</span>
        </div>
      </div>

      <!-- 문서 B -->
      <div class="document-upload-section">
        <h2>문서 B</h2>
        <div class="input-tabs">
          <button
            :class="{ active: modeB === 'file' }"
            @click="switchModeB('file')"
          >
            파일
          </button>
          <button
            :class="{ active: modeB === 'url' }"
            @click="switchModeB('url')"
          >
            URL
          </button>
          <button
            :class="{ active: modeB === 'text' }"
            @click="switchModeB('text')"
          >
            텍스트
          </button>
        </div>

        <!-- 파일 업로드 -->
        <div v-if="modeB === 'file'" class="input-box">
          <input type="file" @change="onFileChangeB" />
          <button
            class="primary"
            @click="uploadDocumentB"
            :disabled="!fileB || isLoadingB"
          >
            업로드
          </button>
        </div>

        <!-- URL 입력 -->
        <div v-if="modeB === 'url'" class="input-box">
          <input
            type="text"
            v-model="urlB"
            placeholder="https://example.com/document"
          />
          <button
            class="primary"
            @click="uploadDocumentB"
            :disabled="!urlB || isLoadingB"
          >
            불러오기
          </button>
        </div>

        <!-- 텍스트 직접 입력 -->
        <div v-if="modeB === 'text'" class="input-box text-input-box">
          <textarea
            v-model="textB"
            placeholder="비교할 텍스트를 입력하세요..."
            rows="6"
          />
          <button
            class="primary"
            @click="uploadDocumentB"
            :disabled="!textB.trim() || isLoadingB"
          >
            입력 완료
          </button>
        </div>

        <!-- 업로드 완료 표시 -->
        <div v-if="documentIdB" class="upload-success">
          <span class="success-icon">✓</span>
          <span>문서 B 준비 완료</span>
        </div>

        <!-- 로딩 표시 B -->
        <div v-if="isLoadingB" class="loading-indicator">
          <span class="spinner"></span>
          <span>업로드 중...</span>
        </div>
      </div>
    </div>

    <!-- 비교 모드 선택 -->
    <div v-if="documentIdA && documentIdB" class="compare-options">
      <div class="mode-selection">
        <h3>비교 모드 선택</h3>
        <div class="radio-group">
          <label class="radio-option">
            <input
              type="radio"
              v-model="compareMode"
              value="full"
            />
            <span class="radio-label">
              <span class="radio-title">전체 비교</span>
              <span class="radio-description">문서 전체 내용을 비교분석합니다</span>
            </span>
          </label>
          <label class="radio-option">
            <input
              type="radio"
              v-model="compareMode"
              value="section"
            />
            <span class="radio-label">
              <span class="radio-title">섹션별 비교</span>
              <span class="radio-description">문서의 섹션별로 세분화하여 비교합니다</span>
            </span>
          </label>
        </div>
      </div>
    </div>

    <!-- 비교 시작 버튼 -->
    <div v-if="documentIdA && documentIdB" class="action-footer">
      <button
        class="compare-start-btn"
        @click="startComparison"
        :disabled="isComparing"
      >
        {{ isComparing ? '비교 중...' : '비교 시작' }}
      </button>
    </div>

    <!-- 로딩 오버레이 (비교 진행 중) -->
    <transition name="fade">
      <div v-if="isComparing" class="loading-overlay">
        <div class="loading-content">
          <div class="loading-box">
            <div class="loading-bar"></div>
          </div>
          <p class="loading-text">
            문서를 비교분석 중입니다…
          </p>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import {
  uploadDocument,
  processDocument,
  processComparison,
} from "../api/ComparisonApi";

const router = useRouter();

// ========== 문서 A 관련 상태 ==========
const modeA = ref("file");
const fileA = ref(null);
const urlA = ref("");
const textA = ref("");
const documentIdA = ref(null);
const isLoadingA = ref(false);

// ========== 문서 B 관련 상태 ==========
const modeB = ref("file");
const fileB = ref(null);
const urlB = ref("");
const textB = ref("");
const documentIdB = ref(null);
const isLoadingB = ref(false);

// ========== 비교 관련 상태 ==========
const compareMode = ref("full"); // 'full' 또는 'section'
const isComparing = ref(false);
const errorMessage = ref(null);

// ========== 문서 A 핸들러 ==========
/**
 * 문서 A 파일 선택
 */
function onFileChangeA(e) {
  fileA.value = e.target.files[0];
}

/**
 * 문서 A 모드 전환
 */
function switchModeA(nextMode) {
  if (modeA.value !== nextMode) {
    modeA.value = nextMode;
    fileA.value = null;
    urlA.value = "";
    textA.value = "";
  }
}

/**
 * 문서 A 업로드
 */
async function uploadDocumentA() {
  try {
    errorMessage.value = null;
    isLoadingA.value = true;

    documentIdA.value = await uploadDocument(
      modeA.value,
      fileA.value,
      urlA.value || null,
      textA.value || null
    );

    // 문서 파싱
    await processDocument(
      modeA.value,
      documentIdA.value,
      fileA.value,
      urlA.value || null,
      textA.value || null
    );
  } catch (e) {
    handleError(e, "문서 A");
    documentIdA.value = null;
  } finally {
    isLoadingA.value = false;
  }
}

// ========== 문서 B 핸들러 ==========
/**
 * 문서 B 파일 선택
 */
function onFileChangeB(e) {
  fileB.value = e.target.files[0];
}

/**
 * 문서 B 모드 전환
 */
function switchModeB(nextMode) {
  if (modeB.value !== nextMode) {
    modeB.value = nextMode;
    fileB.value = null;
    urlB.value = "";
    textB.value = "";
  }
}

/**
 * 문서 B 업로드
 */
async function uploadDocumentB() {
  try {
    errorMessage.value = null;
    isLoadingB.value = true;

    documentIdB.value = await uploadDocument(
      modeB.value,
      fileB.value,
      urlB.value || null,
      textB.value || null
    );

    // 문서 파싱
    await processDocument(
      modeB.value,
      documentIdB.value,
      fileB.value,
      urlB.value || null,
      textB.value || null
    );
  } catch (e) {
    handleError(e, "문서 B");
    documentIdB.value = null;
  } finally {
    isLoadingB.value = false;
  }
}

// ========== 비교 요청 ==========
/**
 * 비교 시작
 */
async function startComparison() {
  if (!documentIdA.value || !documentIdB.value) {
    errorMessage.value = "두 문서를 모두 업로드해주세요.";
    return;
  }

  try {
    errorMessage.value = null;
    isComparing.value = true;

    // 비교분석 처리 요청
    // processComparison이 ComparisonJob 생성 + 비교 처리를 함께 수행
    const result = await processComparison(
      documentIdA.value,
      documentIdB.value,
      compareMode.value
    );

    const comparisonId = result.jobId;

    // 비교 결과 페이지로 이동
    router.push({
      name: "CompareResult",
      params: { id: comparisonId },
    });
  } catch (e) {
    handleError(e, "비교분석");
  } finally {
    isComparing.value = false;
  }
}

/**
 * 에러 처리
 */
function handleError(e, context = "") {
  const error = e?.response?.data;

  if (error?.message) {
    errorMessage.value = `${context} 처리 중 오류: ${error.message}`;
  } else {
    errorMessage.value = `${context} 처리 중 알 수 없는 오류가 발생했습니다.`;
  }
}
</script>

<style scoped>
.compare-upload {
  min-height: 100vh;
  padding: 40px 20px;
  background: linear-gradient(135deg, #f0f7ff 0%, #f5f3ff 100%);
}

/* 헤더 */
.compare-header {
  text-align: center;
  margin-bottom: 48px;
}

.compare-title {
  font-size: 36px;
  font-weight: 800;
  background: linear-gradient(90deg, #2563eb, #7c3aed);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 12px;
}

.compare-subtitle {
  font-size: 16px;
  color: #64748b;
}

/* 문서 업로드 컨테이너 (좌우 분할) */
.document-containers {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32px;
  max-width: 1200px;
  margin: 0 auto 40px;
}

.document-upload-section {
  padding: 32px;
  border-radius: 18px;
  background: white;
  backdrop-filter: blur(8px);
  box-shadow:
    0 10px 25px rgba(80, 70, 180, 0.08),
    0 4px 12px rgba(80, 70, 180, 0.05);
}

.document-upload-section h2 {
  font-size: 20px;
  font-weight: 700;
  color: #3b82f6;
  margin-bottom: 24px;
  text-align: center;
}

/* 입력 탭 (기존 SummaryView 스타일) */
.input-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.input-tabs button {
  flex: 1;
  padding: 10px;
  border-radius: 10px;
  border: none;
  background: #e0e7ff;
  color: #3730a3;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
}

.input-tabs button.active {
  background: linear-gradient(90deg, #3b82f6, #8b5cf6);
  color: #fff;
  box-shadow: 0 8px 16px rgba(99, 102, 241, 0.35);
}

/* 입력 박스 */
.input-box {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

.input-box input[type="text"],
.input-box input[type="file"] {
  flex: 1;
  padding: 12px 14px;
  border-radius: 10px;
  border: 1px solid #c7d2fe;
  font-size: 14px;
  background: #ffffff;
}

.input-box.text-input-box {
  flex-direction: column;
}

.input-box textarea {
  width: 100%;
  padding: 12px 14px;
  border-radius: 10px;
  border: 1px solid #c7d2fe;
  font-size: 14px;
  font-family: inherit;
  background: #ffffff;
  resize: vertical;
  box-sizing: border-box;
  line-height: 1.6;
  color: #1e293b;
}

.input-box textarea:focus {
  outline: none;
  border-color: #818cf8;
  box-shadow: 0 0 0 3px rgba(129, 140, 248, 0.2);
}

.text-input-box button.primary {
  align-self: flex-end;
}

/* 버튼 공통 */
button.primary {
  padding: 12px 18px;
  border-radius: 10px;
  border: none;
  font-weight: 600;
  background: linear-gradient(90deg, #38bdf8, #6366f1);
  color: white;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

button.primary:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 10px 20px rgba(99, 102, 241, 0.4);
}

button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 업로드 완료 표시 */
.upload-success {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  border-radius: 10px;
  background: #dbeafe;
  color: #0369a1;
  font-weight: 600;
  margin-top: 12px;
}

.success-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #0369a1;
  color: white;
  font-weight: 700;
}

/* 로딩 표시 */
.loading-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  border-radius: 10px;
  background: #f0e7ff;
  color: #7c3aed;
  font-weight: 600;
  margin-top: 12px;
}

.spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid #d8b4fe;
  border-top-color: #7c3aed;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 비교 옵션 */
.compare-options {
  max-width: 1200px;
  margin: 0 auto 40px;
  padding: 32px;
  border-radius: 18px;
  background: white;
  backdrop-filter: blur(8px);
  box-shadow:
    0 10px 25px rgba(80, 70, 180, 0.08),
    0 4px 12px rgba(80, 70, 180, 0.05);
}

.mode-selection h3 {
  font-size: 18px;
  font-weight: 700;
  color: #3b82f6;
  margin-bottom: 24px;
}

.radio-group {
  display: flex;
  gap: 20px;
}

.radio-option {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  cursor: pointer;
  padding: 16px;
  border-radius: 12px;
  border: 2px solid #e0e7ff;
  transition: all 0.2s ease;
}

.radio-option:hover {
  border-color: #818cf8;
  background: rgba(129, 140, 248, 0.05);
}

.radio-option input[type="radio"] {
  cursor: pointer;
  width: 20px;
  height: 20px;
}

.radio-label {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.radio-title {
  font-weight: 700;
  color: #1e293b;
}

.radio-description {
  font-size: 14px;
  color: #64748b;
  font-weight: 400;
}

/* 액션 푸터 */
.action-footer {
  display: flex;
  justify-content: center;
  margin: 40px 0;
}

.compare-start-btn {
  padding: 14px 48px;
  border-radius: 12px;
  border: none;
  font-weight: 700;
  font-size: 16px;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6);
  color: white;
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 0 12px 24px rgba(99, 102, 241, 0.35);
}

.compare-start-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 16px 32px rgba(99, 102, 241, 0.45);
}

.compare-start-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 에러 모달 */
.error-overlay {
  position: fixed;
  inset: 0;
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(15, 23, 42, 0.18);
  backdrop-filter: blur(2px);
}

.error-modal {
  width: 90%;
  max-width: 420px;
  padding: 28px;
  border-radius: 18px;
  background: #ffffff;
  text-align: center;
  box-shadow:
    0 20px 40px rgba(30, 64, 175, 0.22),
    0 8px 16px rgba(30, 64, 175, 0.16);
}

.error-modal h4 {
  font-size: 18px;
  font-weight: 800;
  margin-bottom: 12px;
  color: #dc2626;
}

.error-modal p {
  font-size: 14px;
  line-height: 1.6;
  color: #334155;
  margin-bottom: 20px;
}

.error-modal button {
  width: 100%;
  padding: 12px;
  border-radius: 10px;
  border: none;
  font-weight: 600;
  background: linear-gradient(90deg, #f87171, #ef4444);
  color: white;
  cursor: pointer;
}

/* 로딩 오버레이 */
.loading-overlay {
  position: fixed;
  inset: 0;
  background: rgba(255, 255, 255, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
  backdrop-filter: blur(2px);
}

.loading-content {
  text-align: center;
}

.loading-box {
  width: 220px;
  height: 10px;
  background: rgba(30, 41, 59, 0.15);
  border-radius: 8px;
  overflow: hidden;
  margin: 20px 0;
}

.loading-bar {
  height: 100%;
  width: 30%;
  background: linear-gradient(90deg, #1e293b, #334155, #475569);
  border-radius: 8px;
  animation: loading-move 1.4s ease-in-out infinite;
}

.loading-text {
  margin-top: 12px;
  font-size: 0.85rem;
  color: #334155;
}

@keyframes loading-move {
  0% {
    transform: translateX(-100%);
  }
  50% {
    transform: translateX(200%);
  }
  100% {
    transform: translateX(200%);
  }
}

/* 트랜지션 */
.fade-slide-enter-active {
  transition: all 0.45s ease;
}

.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .document-containers {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .compare-title {
    font-size: 28px;
  }

  .compare-subtitle {
    font-size: 14px;
  }

  .radio-group {
    flex-direction: column;
  }

  .compare-start-btn {
    padding: 12px 32px;
    font-size: 14px;
  }
}
</style>
