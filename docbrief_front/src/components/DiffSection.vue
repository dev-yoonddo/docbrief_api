<template>
  <div class="diff-section">
    <!-- 비교분석 결과 카테고리 탭 -->
    <div class="category-tabs">
      <button
        v-for="(category, key) in categories"
        :key="key"
        :class="{ active: activeCategory === key }"
        @click="switchCategory(key)"
        class="category-tab"
      >
        <span class="tab-icon">{{ category.icon }}</span>
        <span class="tab-label">{{ category.label }}</span>
        <span class="tab-count">{{ getCategoryCount(key) }}</span>
      </button>
    </div>

    <!-- 탭 콘텐츠 -->
    <div class="category-content">
      <!-- 로딩 상태 -->
      <div v-if="isLoading" class="loading-state">
        <div class="spinner"></div>
        <p>비교분석 결과를 로드 중입니다…</p>
      </div>

      <!-- 데이터 없음 -->
      <div v-else-if="!activeItems || activeItems.length === 0" class="empty-state">
        <p>비교 항목이 없습니다.</p>
      </div>

      <!-- 항목 목록 -->
      <transition-group v-else name="fade-slide" tag="div" class="items-container">
        <DiffItem
          v-for="(item, index) in activeItems"
          :key="`${activeCategory}-${index}`"
          :item="item"
          :category="activeCategory"
          @show-detail="onShowDetail"
        />
      </transition-group>
    </div>

    <!-- 원문 뷰어 모달 -->
    <OriginalViewer
      :isVisible="showOriginalViewer"
      :item="selectedItem"
      :onClose="closeOriginalViewer"
    />
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import DiffItem from "./DiffItem.vue";
import OriginalViewer from "./OriginalViewer.vue";

/**
 * Props 정의
 * - data: 비교분석 결과
 *   - agreements: 일치 항목 배열
 *   - differences: 차이점 항목 배열
 *   - conflicts: 충돌 항목 배열
 * - isLoading: 로딩 상태
 */
const props = defineProps({
  data: {
    type: Object,
    default: () => ({
      agreements: [],
      differences: [],
      conflicts: [],
    }),
  },
  isLoading: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['comparison-select']);

/**
 * 카테고리 정의
 * conflicts 탭 제거: 일치, 차이점 두 가지만 제공
 */
const categories = {
  agreements: {
    label: "일치",
    icon: "✓",
    items: () => props.data.agreements || [],
  },
  differences: {
    label: "차이점",
    icon: "≠",
    items: () => props.data.differences || [],
  },
};

// 활성 카테고리
const activeCategory = ref("agreements");

// 원문 뷰어 모달 상태
const showOriginalViewer = ref(false);
const selectedItem = ref(null);

/**
 * 활성 카테고리의 항목 목록
 */
const activeItems = computed(() => {
  const category = categories[activeCategory.value];
  return category ? category.items() : [];
});

/**
 * 카테고리별 항목 개수
 * @param {string} categoryKey - 카테고리 키
 * @returns {number} 항목 개수
 */
function getCategoryCount(categoryKey) {
  const category = categories[categoryKey];
  return category ? category.items().length : 0;
}

/**
 * 카테고리 전환
 * @param {string} key - 카테고리 키
 */
function switchCategory(key) {
  activeCategory.value = key;
}

/**
 * 상세 비교 항목 선택 이벤트 처리
 * @param {Object} item - 비교 항목
 */
function onShowDetail(item) {
  emit('comparison-select', {
    item,
    category: activeCategory.value,
  });
}

/**
 * 원문 보기 모달 열기
 * @param {Object} item - 비교 항목
 */
function openOriginalViewer(item) {
  selectedItem.value = item;
  showOriginalViewer.value = true;
}

/**
 * 원문 보기 모달 닫기
 */
function closeOriginalViewer() {
  showOriginalViewer.value = false;
  selectedItem.value = null;
}
</script>

<style scoped>
/* 섹션 컨테이너 */
.diff-section {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  box-shadow:
    0 12px 24px rgba(0, 0, 0, 0.08),
    0 6px 12px rgba(0, 0, 0, 0.06);
  padding: 24px;
}

/* 카테고리 탭 */
.category-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
  border-bottom: 2px solid #e2e8f0;
  overflow-x: auto;
  padding-bottom: 0;
}

.category-tabs::-webkit-scrollbar {
  height: 4px;
}

.category-tabs::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 2px;
}

/* 카테고리 탭 버튼 */
.category-tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border: none;
  background: transparent;
  color: #64748b;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.25s ease;
  border-bottom: 3px solid transparent;
  margin-bottom: -2px;
  white-space: nowrap;
  flex-shrink: 0;
}

.category-tab:hover {
  color: #1e293b;
  background: rgba(59, 130, 246, 0.04);
}

.category-tab.active {
  color: #3b82f6;
  border-bottom-color: #3b82f6;
}

/* 탭 아이콘 */
.tab-icon {
  font-size: 16px;
}

/* 탭 레이블 */
.tab-label {
  display: inline-block;
}

/* 탭 카운트 배지 */
.tab-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 24px;
  height: 24px;
  padding: 0 6px;
  background: rgba(59, 130, 246, 0.15);
  color: #3b82f6;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 700;
  margin-left: 4px;
}

.category-tab.active .tab-count {
  background: rgba(59, 130, 246, 0.3);
}

/* 카테고리 콘텐츠 */
.category-content {
  min-height: 200px;
}

/* 로딩 상태 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 60px 24px;
  color: #64748b;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(59, 130, 246, 0.2);
  border-top-color: #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 데이터 없음 상태 */
.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 24px;
  color: #94a3b8;
  font-size: 15px;
  text-align: center;
}

.empty-state p {
  margin: 0;
}

/* 항목 컨테이너 */
.items-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 트랜지션 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-slide-enter-to {
  opacity: 1;
  transform: translateY(0);
}

.fade-slide-leave-from {
  opacity: 1;
  transform: translateY(0);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .diff-section {
    padding: 16px;
  }

  .category-tabs {
    gap: 4px;
    margin-bottom: 16px;
  }

  .category-tab {
    padding: 10px 12px;
    font-size: 13px;
    gap: 6px;
  }

  .tab-icon {
    font-size: 14px;
  }

  .tab-count {
    min-width: 20px;
    height: 20px;
    font-size: 11px;
    padding: 0 4px;
  }

  .items-container {
    gap: 10px;
  }

  .loading-state,
  .empty-state {
    padding: 40px 16px;
  }

  .spinner {
    width: 32px;
    height: 32px;
    border-width: 2px;
  }
}

/* 초소형 화면 */
@media (max-width: 480px) {
  .diff-section {
    padding: 12px;
    border-radius: 12px;
  }

  .category-tabs {
    gap: 2px;
    margin-bottom: 12px;
  }

  .category-tab {
    padding: 8px 10px;
    font-size: 12px;
  }

  .tab-label {
    display: none;
  }

  .tab-count {
    display: none;
  }

  .category-tab:hover {
    background: transparent;
  }
}
</style>
