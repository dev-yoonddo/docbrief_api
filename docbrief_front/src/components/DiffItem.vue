<template>
  <div class="diff-item" :class="[`type-${item.type}`, { expanded: isExpanded }]">
    <!-- 항목 헤더 (클릭 가능) -->
    <div class="diff-item-header" @click="toggleExpand">
      <!-- 타입 배지 -->
      <span class="type-badge" :class="item.type">
        {{ typeLabelMap[item.type] || item.type }}
      </span>

      <!-- 중요도 배지 (있을 경우) -->
      <span v-if="item.importance" class="importance-badge" :class="item.importance">
        {{ item.importance }}
      </span>

      <!-- 제목/설명 -->
      <div class="item-description">
        {{ item.description || item.content || "항목 없음" }}
      </div>

      <!-- 확장/축소 아이콘 -->
      <span class="expand-icon">{{ isExpanded ? "▼" : "▶" }}</span>
    </div>

    <!-- 항목 상세 정보 (전개 시 표시) -->
    <transition name="expand">
      <div v-if="isExpanded" class="diff-item-content">
        <!-- 문서 A 정보 -->
        <div v-if="item.sourceA" class="source-info">
          <h4 class="source-title">문서 A</h4>
          <p class="source-location">
            문단: {{ item.sourceA.paragraphOrder }},
            문장: {{ item.sourceA.sentenceOrder }}
          </p>
          <p v-if="item.sourceA.originalText" class="source-text">
            {{ item.sourceA.originalText }}
          </p>
        </div>

        <!-- 문서 B 정보 -->
        <div v-if="item.sourceB" class="source-info">
          <h4 class="source-title">문서 B</h4>
          <p class="source-location">
            문단: {{ item.sourceB.paragraphOrder }},
            문장: {{ item.sourceB.sentenceOrder }}
          </p>
          <p v-if="item.sourceB.originalText" class="source-text">
            {{ item.sourceB.originalText }}
          </p>
        </div>

        <!-- 상세 비교 버튼 -->
        <button
          class="view-original-btn"
          @click="showDetail"
          :disabled="!canViewOriginal"
        >
          📄 상세 비교
        </button>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";

/**
 * Props 정의
 * - item: 비교 항목 객체
 *   - type: "ADDITION" | "DELETION" | "MODIFICATION" | "AGREEMENT" | "CONFLICT"
 *   - description: 항목 설명
 *   - content: 항목 내용 (description이 없을 때 대체)
 *   - importance: 중요도 (선택사항)
 *   - sourceA: { paragraphOrder, sentenceOrder, originalText }
 *   - sourceB: { paragraphOrder, sentenceOrder, originalText }
 */
const props = defineProps({
  item: {
    type: Object,
    required: true,
  },
  /**
   * 카테고리 정보 (agreements, differences, conflicts)
   */
  category: {
    type: String,
    default: 'agreements',
  },
  /**
   * 원문 보기 버튼 클릭 이벤트 핸들러
   * @param {Object} item - 항목
   */
  onViewOriginal: {
    type: Function,
    default: null,
  },
});

const emit = defineEmits(['show-detail']);

// 타입별 레이블 매핑
const typeLabelMap = {
  ADDITION: "추가됨",
  DELETION: "삭제됨",
  MODIFICATION: "수정됨",
  AGREEMENT: "일치",
  CONFLICT: "충돌",
};

// 전개 상태
const isExpanded = ref(false);

/**
 * 전개/축소 토글
 */
function toggleExpand() {
  isExpanded.value = !isExpanded.value;
}

/**
 * 원문 보기 가능 여부
 * sourceA 또는 sourceB가 있을 때만 가능
 */
const canViewOriginal = computed(() => {
  return !!(props.item.sourceA || props.item.sourceB);
});

/**
 * 상세 비교 버튼 클릭 핸들러
 * parent에 이벤트 emit
 */
function showDetail() {
  emit('show-detail', props.item);
}

/**
 * 원문 보기 버튼 클릭 핸들러
 */
function openOriginalViewer() {
  if (props.onViewOriginal) {
    props.onViewOriginal(props.item);
  }
}
</script>

<style scoped>
/* 항목 컨테이너 */
.diff-item {
  background: white;
  border-radius: 12px;
  border-left: 4px solid #cbd5e1;
  overflow: hidden;
  transition: all 0.25s ease;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 타입별 왼쪽 보더 색상 */
.diff-item.type-ADDITION {
  border-left-color: #10b981;
}

.diff-item.type-DELETION {
  border-left-color: #ef4444;
}

.diff-item.type-MODIFICATION {
  border-left-color: #f59e0b;
}

.diff-item.type-AGREEMENT {
  border-left-color: #3b82f6;
}

.diff-item.type-CONFLICT {
  border-left-color: #8b5cf6;
}

/* 전개 상태 백그라운드 */
.diff-item.expanded {
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.15);
}

/* 항목 헤더 */
.diff-item-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 18px;
  cursor: pointer;
  transition: all 0.25s ease;
}

.diff-item-header:hover {
  background-color: rgba(59, 130, 246, 0.06);
}

/* 타입 배지 */
.type-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 5px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 700;
  white-space: nowrap;
  color: white;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12);
}

.type-badge.ADDITION {
  background: linear-gradient(135deg, #10b981, #059669);
}

.type-badge.DELETION {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.type-badge.MODIFICATION {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.type-badge.AGREEMENT {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

.type-badge.CONFLICT {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
}

/* 중요도 배지 */
.importance-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
}

/* 항목 설명 */
.item-description {
  flex: 1;
  font-size: 14px;
  color: #2d3748;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 확장/축소 아이콘 */
.expand-icon {
  font-size: 12px;
  color: #64748b;
  transition: transform 0.25s ease;
  flex-shrink: 0;
}

.diff-item.expanded .expand-icon {
  transform: rotate(90deg);
}

/* 항목 내용 영역 */
.diff-item-content {
  padding: 18px;
  border-top: 1px solid #e2e8f0;
  background-color: #f8f9fb;
}

/* 출처 정보 */
.source-info {
  margin-bottom: 14px;
  padding: 14px;
  background: white;
  border-radius: 10px;
  border-left: 4px solid #3b82f6;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
}

.source-info:last-of-type {
  margin-bottom: 18px;
}

.source-title {
  font-size: 12px;
  font-weight: 700;
  color: #2d3748;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 6px;
}

.source-location {
  font-size: 0.85rem;
  color: #718096;
  margin-bottom: 8px;
  font-weight: 500;
}

.source-text {
  font-size: 13px;
  color: #2d3748;
  line-height: 1.6;
  padding: 10px;
  background: linear-gradient(135deg, #f8f9fb 0%, #f0f4f9 100%);
  border-radius: 8px;
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 원문 보기 버튼 */
.view-original-btn {
  width: 100%;
  padding: 11px 14px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  font-weight: 600;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
}

.view-original-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(59, 130, 246, 0.3);
}

.view-original-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 트랜지션 */
.expand-enter-active,
.expand-leave-active {
  transition: all 0.25s ease;
}

.expand-enter-from {
  opacity: 0;
  max-height: 0;
}

.expand-enter-to {
  opacity: 1;
  max-height: 500px;
}

.expand-leave-from {
  opacity: 1;
  max-height: 500px;
}

.expand-leave-to {
  opacity: 0;
  max-height: 0;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .diff-item-header {
    gap: 8px;
    padding: 12px 12px;
  }

  .type-badge {
    padding: 3px 8px;
    font-size: 11px;
  }

  .item-description {
    font-size: 13px;
  }

  .source-info {
    padding: 10px;
  }

  .source-text {
    font-size: 12px;
  }

  .view-original-btn {
    padding: 8px 10px;
    font-size: 12px;
  }
}
</style>
