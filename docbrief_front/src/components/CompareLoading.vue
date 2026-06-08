<template>
  <!-- 비교분석 로딩 오버레이 -->
  <transition name="fade">
    <div v-if="isVisible" class="compare-loading-overlay">
      <div class="compare-loading-content">
        <!-- 로딩 스피너 -->
        <div class="compare-loading-spinner">
          <div class="spinner-ring"></div>
          <div class="spinner-ring"></div>
          <div class="spinner-ring"></div>
        </div>

        <!-- 로딩 텍스트 -->
        <p class="compare-loading-text">{{ message }}</p>

        <!-- 진행률 (선택사항) -->
        <div v-if="progress" class="compare-progress-bar">
          <div class="progress-fill" :style="{ width: progress + '%' }"></div>
        </div>
        <p v-if="progress" class="compare-progress-text">{{ progress }}%</p>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { defineProps } from "vue";

/**
 * Props 정의
 * - isVisible: 로딩 표시 여부
 * - message: 로딩 메시지 (기본값: "비교 분석 중...")
 * - progress: 진행률 (0-100, 선택사항)
 */
const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false,
  },
  message: {
    type: String,
    default: "비교 분석 중…",
  },
  progress: {
    type: Number,
    default: null,
  },
});
</script>

<style scoped>
/* 로딩 오버레이 */
.compare-loading-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(3px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

/* 로딩 콘텐츠 */
.compare-loading-content {
  text-align: center;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow:
    0 20px 60px rgba(0, 0, 0, 0.16),
    0 8px 24px rgba(0, 0, 0, 0.12);
  backdrop-filter: blur(10px);
}

/* 스피너 애니메이션 (3개 링 방식) */
.compare-loading-spinner {
  position: relative;
  width: 60px;
  height: 60px;
  margin: 0 auto 24px;
}

.spinner-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 4px solid transparent;
  border-radius: 50%;
  border-top-color: #3b82f6;
  border-right-color: #8b5cf6;
  animation: spin 1.2s linear infinite;
}

.spinner-ring:nth-child(2) {
  width: 80%;
  height: 80%;
  top: 10%;
  left: 10%;
  border-top-color: #8b5cf6;
  border-right-color: #ec4899;
  animation-duration: 1.8s;
  animation-direction: reverse;
}

.spinner-ring:nth-child(3) {
  width: 60%;
  height: 60%;
  top: 20%;
  left: 20%;
  border-top-color: #ec4899;
  border-right-color: #3b82f6;
  animation-duration: 2.4s;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 로딩 텍스트 */
.compare-loading-text {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 16px;
  letter-spacing: 0.5px;
}

/* 진행률 바 */
.compare-progress-bar {
  width: 200px;
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  overflow: hidden;
  margin: 0 auto 8px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6);
  transition: width 0.3s ease;
  border-radius: 3px;
}

/* 진행률 텍스트 */
.compare-progress-text {
  font-size: 12px;
  color: #64748b;
  margin-top: 4px;
  font-weight: 500;
}

/* 페이드 트랜지션 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .compare-loading-content {
    padding: 32px 24px;
    border-radius: 12px;
  }

  .compare-loading-text {
    font-size: 14px;
  }

  .compare-progress-bar {
    width: 160px;
  }
}
</style>
