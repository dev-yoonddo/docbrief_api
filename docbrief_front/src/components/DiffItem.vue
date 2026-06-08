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

        <!-- 원문 보기 버튼 -->
        <button
          class="view-original-btn"
          @click="openOriginalViewer"
          :disabled="!canViewOriginal"
        >
          📄 원문 보기
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
   * 원문 보기 버튼 클릭 이벤트 핸들러
   * @param {Object} item - 항목
   */
  onViewOriginal: {
    type: Function,
    default: null,
  },
});

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
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  border-left: 4px solid #cbd5e1;
  overflow: hidden;
  transition: all 0.25s ease;
  margin-bottom: 12px;
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
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

/* 항목 헤더 */
.diff-item-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  cursor: pointer;
  transition: background-color 0.25s ease;
}

.diff-item-header:hover {
  background-color: rgba(59, 130, 246, 0.04);
}

/* 타입 배지 */
.type-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
  color: white;
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
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
}

.type-badge.CONFLICT {
  background: linear-gradient(135deg, #8b5cf6, #6d28d9);
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
  color: #1e293b;
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
  padding: 16px;
  border-top: 1px solid #e2e8f0;
  background-color: rgba(241, 245, 249, 0.5);
}

/* 출처 정보 */
.source-info {
  margin-bottom: 12px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 8px;
  border-left: 3px solid #3b82f6;
}

.source-info:last-of-type {
  margin-bottom: 16px;
}

.source-title {
  font-size: 12px;
  font-weight: 700;
  color: #1e293b;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 4px;
}

.source-location {
  font-size: 12px;
  color: #64748b;
  margin-bottom: 6px;
  font-weight: 500;
}

.source-text {
  font-size: 13px;
  color: #334155;
  line-height: 1.5;
  padding: 8px;
  background: rgba(248, 250, 252, 0.8);
  border-radius: 6px;
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 원문 보기 버튼 */
.view-original-btn {
  width: 100%;
  padding: 10px 12px;
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  font-weight: 600;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.25s ease;
}

.view-original-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 12px rgba(59, 130, 246, 0.3);
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
