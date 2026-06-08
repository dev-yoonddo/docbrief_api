<template>
  <!-- 원문 뷰어 모달 -->
  <transition name="modal-fade">
    <div v-if="isVisible" class="original-viewer-overlay">
      <!-- 모달 컨테이너 -->
      <div class="original-viewer-modal">
        <!-- 모달 헤더 -->
        <div class="viewer-header">
          <h3>원문 비교</h3>
          <button class="close-btn" @click="closeViewer">✕</button>
        </div>

        <!-- 본문 컨테이너 -->
        <div class="viewer-body">
          <!-- 왼쪽 패널 (문서 A) -->
          <div class="document-panel">
            <div class="panel-header">
              <h4>문서 A</h4>
              <span class="panel-meta">
                문단 {{ sourceA?.paragraphOrder }}, 문장 {{ sourceA?.sentenceOrder }}
              </span>
            </div>
            <div class="panel-content">
              <div v-if="sourceA" class="text-content">
                <!-- 하이라이트된 원문 -->
                <p v-html="highlightedSourceA"></p>
              </div>
              <p v-else class="empty-text">원문이 없습니다</p>
            </div>
          </div>

          <!-- 구분선 -->
          <div class="divider"></div>

          <!-- 오른쪽 패널 (문서 B) -->
          <div class="document-panel">
            <div class="panel-header">
              <h4>문서 B</h4>
              <span class="panel-meta">
                문단 {{ sourceB?.paragraphOrder }}, 문장 {{ sourceB?.sentenceOrder }}
              </span>
            </div>
            <div class="panel-content">
              <div v-if="sourceB" class="text-content">
                <!-- 하이라이트된 원문 -->
                <p v-html="highlightedSourceB"></p>
              </div>
              <p v-else class="empty-text">원문이 없습니다</p>
            </div>
          </div>
        </div>

        <!-- 모달 푸터 -->
        <div class="viewer-footer">
          <label class="sync-checkbox">
            <input
              v-model="syncScroll"
              type="checkbox"
              :disabled="true"
            />
            <span>스크롤 동기화 (개발 중)</span>
          </label>
          <button class="close-modal-btn" @click="closeViewer">닫기</button>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, computed } from "vue";

/**
 * Props 정의
 * - isVisible: 모달 표시 여부
 * - item: 비교 항목
 *   - sourceA: { paragraphOrder, sentenceOrder, originalText }
 *   - sourceB: { paragraphOrder, sentenceOrder, originalText }
 * - onClose: 모달 닫기 콜백
 */
const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false,
  },
  item: {
    type: Object,
    default: null,
  },
  onClose: {
    type: Function,
    default: null,
  },
});

// 스크롤 동기화 (현재 미지원)
const syncScroll = ref(false);

/**
 * 문서 A의 원문 정보
 */
const sourceA = computed(() => props.item?.sourceA || null);

/**
 * 문서 B의 원문 정보
 */
const sourceB = computed(() => props.item?.sourceB || null);

/**
 * 문서 A의 하이라이트된 텍스트
 * 원문을 <mark> 태그로 감싸서 렌더링
 */
const highlightedSourceA = computed(() => {
  if (!sourceA.value?.originalText) return "";
  // 간단한 하이라이트: 전체 텍스트를 마크 태그로 감싸기
  // 필요시 더 복잡한 하이라이트 로직 추가 가능
  return `<mark class="highlight">${escapeHtml(sourceA.value.originalText)}</mark>`;
});

/**
 * 문서 B의 하이라이트된 텍스트
 */
const highlightedSourceB = computed(() => {
  if (!sourceB.value?.originalText) return "";
  return `<mark class="highlight">${escapeHtml(sourceB.value.originalText)}</mark>`;
});

/**
 * HTML 특수 문자 이스케이프
 * @param {string} text - 이스케이프할 텍스트
 * @returns {string} 이스케이프된 텍스트
 */
function escapeHtml(text) {
  const div = document.createElement("div");
  div.textContent = text;
  return div.innerHTML;
}

/**
 * 모달 닫기
 */
function closeViewer() {
  if (props.onClose) {
    props.onClose();
  }
}
</script>

<style scoped>
/* 모달 오버레이 */
.original-viewer-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 16px;
}

/* 모달 컨테이너 */
.original-viewer-modal {
  background: white;
  border-radius: 16px;
  box-shadow:
    0 30px 60px rgba(0, 0, 0, 0.2),
    0 12px 24px rgba(0, 0, 0, 0.14);
  max-width: 1200px;
  width: 100%;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 모달 헤더 */
.viewer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

.viewer-header h3 {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 8px;
  background: rgba(0, 0, 0, 0.06);
  color: #1e293b;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  background: rgba(0, 0, 0, 0.12);
}

/* 본문 컨테이너 */
.viewer-body {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 0;
  flex: 1;
  overflow: hidden;
}

/* 문서 패널 */
.document-panel {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.panel-header {
  padding: 16px;
  background: rgba(59, 130, 246, 0.04);
  border-bottom: 1px solid #e2e8f0;
  flex-shrink: 0;
}

.panel-header h4 {
  font-size: 14px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 6px 0;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.panel-meta {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

/* 패널 콘텐츠 */
.panel-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: rgba(255, 255, 255, 0.9);
}

.text-content {
  font-size: 14px;
  color: #334155;
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
}

.text-content p {
  margin: 0;
}

/* 하이라이트 */
.text-content mark.highlight {
  background: linear-gradient(120deg, rgba(59, 130, 246, 0.3), rgba(139, 92, 246, 0.3));
  padding: 2px 4px;
  border-radius: 4px;
  color: inherit;
}

.empty-text {
  color: #94a3b8;
  font-style: italic;
  text-align: center;
  padding: 32px 16px;
  margin: 0;
}

/* 구분선 */
.divider {
  width: 2px;
  background: #e2e8f0;
  flex-shrink: 0;
}

/* 모달 푸터 */
.viewer-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  border-top: 1px solid #e2e8f0;
  background: rgba(248, 250, 252, 0.8);
  flex-shrink: 0;
}

.sync-checkbox {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #64748b;
  font-weight: 500;
  cursor: pointer;
}

.sync-checkbox input {
  cursor: pointer;
  width: 16px;
  height: 16px;
}

.sync-checkbox input:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.close-modal-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  font-weight: 600;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.close-modal-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 12px rgba(59, 130, 246, 0.3);
}

/* 트랜지션 */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: all 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

.modal-fade-enter-to,
.modal-fade-leave-from {
  opacity: 1;
  transform: scale(1);
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .original-viewer-modal {
    max-height: 90vh;
  }

  .viewer-body {
    grid-template-columns: 1fr;
    gap: 0;
  }

  .divider {
    height: 2px;
    width: 100%;
    margin: 0;
  }

  .viewer-header h3 {
    font-size: 16px;
  }

  .panel-header {
    padding: 12px;
  }

  .panel-header h4 {
    font-size: 12px;
    margin-bottom: 4px;
  }

  .panel-meta {
    font-size: 11px;
  }

  .panel-content {
    padding: 12px;
  }

  .text-content {
    font-size: 13px;
  }

  .viewer-footer {
    flex-direction: column;
    gap: 12px;
    padding: 12px 16px;
  }

  .sync-checkbox {
    width: 100%;
    font-size: 12px;
  }

  .close-modal-btn {
    width: 100%;
  }
}
</style>
