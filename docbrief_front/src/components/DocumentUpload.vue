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

const mode = ref("file");
const file = ref(null);
const parseResult = ref(null);

const hasResult = computed(() => !!parseResult.value);

function onFileChange(e) {
  file.value = e.target.files[0];
}

/* 파일 업로드 → 바로 요약 */
async function uploadAndParse() {
  await mockSummary();
}

/* URL 불러오기 → 바로 요약 */
async function loadAndParse() {
  await mockSummary();
}

/* Mock 요약 데이터  */
async function mockSummary() {
  parseResult.value = null;

  await new Promise((r) => setTimeout(r, 500));

  parseResult.value = `
📄 문서 요약 결과

• 이 요약은 UI 테스트를 위한 예시 데이터입니다.
• 업로드 또는 URL 입력 후 즉시 요약이 생성됩니다.
• 입력 영역은 위로 이동하고 결과는 부드럽게 표시됩니다.

✅ 핵심 키워드
- Vue 3
- UX Transition
- AI Document Summary

📌 결론
사용자는 별도의 중간 단계 없이
즉시 요약 결과를 확인할 수 있습니다.
사용자는 별도의 중간 단계 없이
즉시 요약 결과를 확인할 수 있습니다.
사용자는 별도의 중간 단계 없이
즉시 요약 결과를 확인할 수 있습니다.
사용자는 별도의 중간 단계 없이
즉시 요약 결과를 확인할 수 있습니다.
사용자는 별도의 중간 단계 없이
즉시 요약 결과를 확인할 수 있습니다.
사용자는 별도의 중간 단계 없이
즉시 요약 결과를 확인할 수 있습니다.
사용자는 별도의 중간 단계 없이
즉시 요약 결과를 확인할 수 있습니다.
`.trim();
}
</script>

<style scoped>
.doc-brief {
  max-width: 680px;
  margin: 60px auto;
  padding: 34px;
  border-radius: 20px;

  background: linear-gradient(
    145deg,
    #f0f7ff,
    #f5f3ff
  );
  box-shadow:
    0 40px 80px rgba(80, 70, 180, 0.35),
    0 18px 36px rgba(80, 70, 180, 0.28),
    0 6px 14px rgba(80, 70, 180, 0.22);
}

/* 제목 */
.title {
  text-align: center;
  font-size: 30px;
  font-weight: 800;
  letter-spacing: 0.5px;
  margin-bottom: 28px;
  background: linear-gradient(
    90deg,
    #2563eb,
    #7c3aed
  );
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 탭 */
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
  background: linear-gradient(
    90deg,
    #3b82f6,
    #8b5cf6
  );
  color: #fff;
  box-shadow: 0 8px 16px rgba(99, 102, 241, 0.35);
}

/* 입력 박스 */
.input-box {
  display: flex;
  gap: 10px;
  margin-bottom: 24px;
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

/* 버튼 공통 */
button.primary {
  padding: 12px 18px;
  border-radius: 10px;
  border: none;
  font-weight: 600;
  background: linear-gradient(
    90deg,
    #38bdf8,
    #6366f1
  );
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

/* 파싱 액션 영역 */
.action-box {
  padding: 16px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.8);
  margin-bottom: 20px;
}

button.secondary {
  margin-top: 10px;
  width: 100%;
  padding: 12px;
  border-radius: 10px;
  border: none;
  font-weight: 600;
  background: linear-gradient(
    90deg,
    #a5b4fc,
    #c084fc
  );
  color: #312e81;
  cursor: pointer;
}

/* 요약 결과 텍스트 영역 */
.result-box {
  margin-top: 14px;
  padding-right: 6px;

  background: transparent;
  box-shadow: none;

  color: #1e293b;
  font-size: 15px;
  line-height: 1.8;

  /* 높이 고정 */
  min-height: 260px;
  max-height: 360px;

  /* 스크롤 제어 */
  overflow-y: auto;
  overflow-x: hidden;

  /* 긴 텍스트 대응 */
  white-space: pre-wrap;
  word-break: break-word;
  overflow-wrap: anywhere;
}

/* 입력 섹션 */
.input-section {
  transition: all 0.5s ease;
}

.input-section.compact {
  margin-bottom: 24px;
}

/* 요약 결과 카드 */
.summary-section {
  margin-top: 24px;
  padding: 32px;
  border-radius: 20px;

  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(6px);

  /* 카드 경계*/
  box-shadow:
    0 28px 56px rgba(0, 0, 0, 0.12),
    0 12px 24px rgba(0, 0, 0, 0.08);

  border: 1px solid rgba(255, 255, 255, 0.6);
}

.summary-section h3 {
  margin-bottom: 14px;
  font-size: 18px;
  font-weight: 700;
  color: #4338ca;
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

.fade-slide-enter-to {
  opacity: 1;
  transform: translateY(0);
}
</style>