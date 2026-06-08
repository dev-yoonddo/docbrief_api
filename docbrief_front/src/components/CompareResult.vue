<template>
  <div class="compare-result">
    <!-- 헤더 -->
    <header class="result-header">
      <div class="header-content">
        <h1 class="result-title">비교분석 결과</h1>
        <router-link to="/compare" class="back-link">← 새로운 비교</router-link>
      </div>
    </header>

    <!-- 로딩 상태 -->
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

    <!-- 에러 상태 -->
    <transition name="fade-slide">
      <div v-if="hasFailed" class="error-section">
        <div class="error-content">
          <h2>비교분석 실패</h2>
          <p>{{ errorMessage }}</p>
          <router-link to="/compare" class="primary">
            다시 시도
          </router-link>
        </div>
      </div>
    </transition>

    <!-- 결과 표시 -->
    <transition name="fade-slide">
      <div v-if="comparisonData && !isLoading && !hasFailed" class="result-container">
        <!-- 종합 요약 섹션 -->
        <OverallSummary
          v-if="comparisonData.overallSummary"
          :summary="comparisonData.overallSummary"
        />

        <!-- 비교 결과 탭 -->
        <div class="result-tabs">
          <button
            :class="{ active: activeTab === 'agreements' }"
            @click="activeTab = 'agreements'"
          >
            일치 사항
            <span v-if="comparisonData.agreements?.length" class="tab-count">
              {{ comparisonData.agreements.length }}
            </span>
          </button>
          <button
            :class="{ active: activeTab === 'differences' }"
            @click="activeTab = 'differences'"
          >
            차이점
            <span v-if="comparisonData.differences?.length" class="tab-count">
              {{ comparisonData.differences.length }}
            </span>
          </button>
          <button
            :class="{ active: activeTab === 'conflicts' }"
            @click="activeTab = 'conflicts'"
          >
            충돌
            <span v-if="comparisonData.conflicts?.length" class="tab-count">
              {{ comparisonData.conflicts.length }}
            </span>
          </button>
        </div>

        <!-- 일치 사항 섹션 -->
        <div v-if="activeTab === 'agreements'" class="result-content">
          <h2>일치 사항</h2>
          <div
            v-if="comparisonData.agreements?.length"
            class="diff-sections"
          >
            <DiffItem
              v-for="(agreement, index) in comparisonData.agreements"
              :key="index"
              :item="agreement"
              @view-original="openOriginalModal"
            />
          </div>
          <div v-else class="empty-state">
            <p>일치하는 내용이 없습니다.</p>
          </div>
        </div>

        <!-- 차이점 섹션 -->
        <div v-if="activeTab === 'differences'" class="result-content">
          <h2>차이점</h2>
          <div
            v-if="comparisonData.differences?.length"
            class="diff-sections"
          >
            <DiffItem
              v-for="(difference, index) in comparisonData.differences"
              :key="index"
              :item="difference"
              @view-original="openOriginalModal"
            />
          </div>
          <div v-else class="empty-state">
            <p>차이점이 없습니다.</p>
          </div>
        </div>

        <!-- 충돌 섹션 -->
        <div v-if="activeTab === 'conflicts'" class="result-content">
          <h2>충돌</h2>
          <div
            v-if="comparisonData.conflicts?.length"
            class="diff-sections"
          >
            <DiffItem
              v-for="(conflict, index) in comparisonData.conflicts"
              :key="index"
              :item="conflict"
              @view-original="openOriginalModal"
            />
          </div>
          <div v-else class="empty-state">
            <p>충돌하는 내용이 없습니다.</p>
          </div>
        </div>

        <!-- 다시 비교하기 버튼 -->
        <div class="action-footer">
          <router-link to="/compare" class="primary large-btn">
            새로운 비교 시작
          </router-link>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import { getComparisonResult, getComparisonStatus } from "../api/ComparisonApi";
import OverallSummary from "./OverallSummary.vue";
import DiffItem from "./DiffItem.vue";

const route = useRoute();

// ========== 상태 관리 ==========
const comparisonId = ref(route.params.id);
const comparisonData = ref(null);
const activeTab = ref("agreements");
const isLoading = ref(true);
const hasFailed = ref(false);
const errorMessage = ref("");
const pollCount = ref(0);
const maxPolls = ref(60); // 최대 60번 폴링 (약 2분)

// ========== 계산된 속성 ==========
const loadingText = computed(() => {
  if (pollCount.value < 10) {
    return "비교분석 준비 중입니다…";
  } else if (pollCount.value < 30) {
    return "문서를 분석 중입니다…";
  } else {
    return "최종 결과를 생성 중입니다…";
  }
});

// ========== 라이프사이클 ==========
onMounted(async () => {
  try {
    await pollComparisonStatus();
  } catch (e) {
    hasFailed.value = true;
    errorMessage.value = e?.message || "비교분석 결과를 불러올 수 없습니다.";
    isLoading.value = false;
  }
});

/**
 * 비교분석 상태 폴링
 * 주기적으로 상태를 확인하여 완료 여부를 판단합니다
 */
async function pollComparisonStatus() {
  while (pollCount.value < maxPolls.value) {
    try {
      const statusData = await getComparisonStatus(comparisonId.value);

      // 상태 확인
      if (statusData.status === "COMPLETED") {
        // 최종 결과 조회
        comparisonData.value = await getComparisonResult(comparisonId.value);
        isLoading.value = false;
        break;
      } else if (statusData.status === "FAILED") {
        hasFailed.value = true;
        errorMessage.value = statusData.errorMessage || "비교분석 처리 중 오류가 발생했습니다.";
        isLoading.value = false;
        break;
      }

      // 아직 진행 중이면 대기 후 다시 조회
      pollCount.value++;
      await new Promise(resolve => setTimeout(resolve, 2000)); // 2초 대기
    } catch (e) {
      // 첫 시도에서 실패할 수 있으므로, 몇 번 재시도
      if (pollCount.value < 3) {
        pollCount.value++;
        await new Promise(resolve => setTimeout(resolve, 2000));
      } else {
        throw e;
      }
    }
  }

  if (pollCount.value >= maxPolls.value && !comparisonData.value) {
    hasFailed.value = true;
    errorMessage.value = "비교분석 시간이 초과되었습니다. 다시 시도해주세요.";
    isLoading.value = false;
  }
}

/**
 * 원문 보기 모달 열기
 * @param {Object} item - 비교 항목
 */
function openOriginalModal(item) {
  // 추후 구현: OriginalViewer 모달 연결
  console.log("원문 보기:", item);
}
</script>

<style scoped>
.compare-result {
  min-height: 100vh;
  padding: 40px 20px;
  background: linear-gradient(135deg, #f0f7ff 0%, #f5f3ff 100%);
}

/* 헤더 */
.result-header {
  margin-bottom: 40px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.result-title {
  font-size: 36px;
  font-weight: 800;
  background: linear-gradient(90deg, #2563eb, #7c3aed);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.back-link {
  padding: 10px 16px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.6);
  color: #4338ca;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.2s ease;
}

.back-link:hover {
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 4px 12px rgba(80, 70, 180, 0.15);
}

/* 결과 컨테이너 */
.result-container {
  max-width: 1200px;
  margin: 0 auto;
}

/* 결과 탭 */
.result-tabs {
  display: flex;
  gap: 8px;
  margin: 32px 0 24px;
  border-bottom: 2px solid rgba(255, 255, 255, 0.4);
}

.result-tabs button {
  padding: 14px 24px;
  border: none;
  background: transparent;
  color: #64748b;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.25s ease;
  border-bottom: 3px solid transparent;
  margin-bottom: -2px;
  position: relative;
}

.result-tabs button:hover {
  color: #4338ca;
}

.result-tabs button.active {
  color: #4338ca;
  border-bottom-color: #6366f1;
}

.tab-count {
  display: inline-block;
  margin-left: 6px;
  padding: 2px 8px;
  border-radius: 12px;
  background: rgba(99, 102, 241, 0.2);
  font-size: 13px;
  font-weight: 700;
}

/* 결과 콘텐츠 */
.result-content {
  padding: 32px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(6px);
  box-shadow:
    0 20px 40px rgba(80, 70, 180, 0.1),
    0 8px 16px rgba(80, 70, 180, 0.06);
  margin-bottom: 32px;
}

.result-content h2 {
  font-size: 22px;
  font-weight: 700;
  color: #4338ca;
  margin-bottom: 24px;
}

.diff-sections {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 빈 상태 */
.empty-state {
  text-align: center;
  padding: 48px 24px;
  color: #94a3b8;
  font-size: 16px;
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

/* 에러 섹션 */
.error-section {
  max-width: 600px;
  margin: 60px auto;
  padding: 40px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(6px);
  box-shadow:
    0 20px 40px rgba(220, 38, 38, 0.15),
    0 8px 16px rgba(220, 38, 38, 0.1);
  text-align: center;
}

.error-content h2 {
  font-size: 24px;
  font-weight: 700;
  color: #dc2626;
  margin-bottom: 16px;
}

.error-content p {
  font-size: 15px;
  color: #64748b;
  margin-bottom: 24px;
  line-height: 1.6;
}

/* 액션 푸터 */
.action-footer {
  display: flex;
  justify-content: center;
  margin: 48px 0 40px;
}

.primary {
  padding: 14px 48px;
  border-radius: 12px;
  border: none;
  font-weight: 700;
  font-size: 16px;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6);
  color: white;
  cursor: pointer;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s ease;
  box-shadow: 0 12px 24px rgba(99, 102, 241, 0.35);
}

.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 32px rgba(99, 102, 241, 0.45);
}

.primary.large-btn {
  padding: 16px 56px;
  font-size: 17px;
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
  .header-content {
    flex-direction: column;
    gap: 16px;
  }

  .result-title {
    font-size: 28px;
  }

  .result-tabs {
    flex-wrap: wrap;
  }

  .result-tabs button {
    padding: 12px 16px;
    font-size: 14px;
  }

  .result-content {
    padding: 20px;
  }

  .result-content h2 {
    font-size: 18px;
  }

  .primary {
    padding: 12px 32px;
    font-size: 14px;
  }

  .primary.large-btn {
    padding: 12px 32px;
    font-size: 14px;
  }
}
</style>
