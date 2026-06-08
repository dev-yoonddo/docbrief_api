<template>
  <!-- 비교 상세 모달 -->
  <transition name="modal-fade">
    <div v-if="isVisible" class="comparison-detail-overlay" @click.self="closeModal">
      <!-- 모달 컨테이너 -->
      <div class="comparison-detail-modal">
        <!-- 모달 헤더 -->
        <div class="detail-header">
          <div class="header-title">
            <h3>{{ categoryLabel }} - 상세 비교</h3>
            <span class="item-type" :class="item?.type?.toLowerCase()">
              {{ typeLabelMap[item?.type] || item?.type }}
            </span>
          </div>
          <button class="close-btn" @click="closeModal">✕</button>
        </div>

        <!-- 문서 정보 -->
        <div class="document-info">
          <div class="doc-info-item">
            <span class="doc-icon">📄</span>
            <span class="doc-name">{{ documentA?.name || '문서 A' }}</span>
          </div>
          <span class="divider-text">vs</span>
          <div class="doc-info-item">
            <span class="doc-icon">📄</span>
            <span class="doc-name">{{ documentB?.name || '문서 B' }}</span>
          </div>
        </div>

        <!-- 비교 콘텐츠 -->
        <div class="detail-content">
          <!-- 문서 A -->
          <div class="document-panel panel-a">
            <div class="panel-header">
              <h4>문서 A</h4>
              <span v-if="item?.sourceA" class="panel-meta">
                문단 {{ item.sourceA.paragraphOrder }} / 문장 {{ item.sourceA.sentenceOrder }}
              </span>
            </div>
            <div class="panel-body">
              <div v-if="item?.sourceA?.originalText" class="text-content">
                <p class="original-text">{{ item.sourceA.originalText }}</p>
              </div>
              <p v-else class="empty-text">원문이 없습니다</p>
            </div>
          </div>

          <!-- 문서 B -->
          <div class="document-panel panel-b">
            <div class="panel-header">
              <h4>문서 B</h4>
              <span v-if="item?.sourceB" class="panel-meta">
                문단 {{ item.sourceB.paragraphOrder }} / 문장 {{ item.sourceB.sentenceOrder }}
              </span>
            </div>
            <div class="panel-body">
              <div v-if="item?.sourceB?.originalText" class="text-content">
                <p class="original-text">{{ item.sourceB.originalText }}</p>
              </div>
              <p v-else class="empty-text">원문이 없습니다</p>
            </div>
          </div>
        </div>

        <!-- 모달 푸터 -->
        <div class="detail-footer">
          <button class="close-modal-btn" @click="closeModal">닫기</button>
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
 * - item: 비교 항목 (sourceA, sourceB 포함)
 * - category: 카테고리 (agreements, differences)
 * - documentA: 문서 A 정보
 * - documentB: 문서 B 정보
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
  category: {
    type: String,
    default: 'agreements',
  },
  documentA: {
    type: Object,
    default: () => ({ name: '문서 A' }),
  },
  documentB: {
    type: Object,
    default: () => ({ name: '문서 B' }),
  },
  onClose: {
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

/**
 * 카테고리별 레이블
 */
const categoryLabel = computed(() => {
  const labels = {
    agreements: "일치 사항",
    differences: "차이점",
    conflicts: "충돌",
  };
  return labels[props.category] || props.category;
});

/**
 * 모달 닫기
 */
function closeModal() {
  if (props.onClose) {
    props.onClose();
  }
}
</script>

<style scoped>
/* 모달 오버레이 */
.comparison-detail-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.6);
  backdrop-filter: blur(6px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;
  padding: 16px;
}

/* 모달 컨테이너 */
.comparison-detail-modal {
  background: white;
  border-radius: 16px;
  box-shadow:
    0 30px 60px rgba(0, 0, 0, 0.25),
    0 12px 24px rgba(0, 0, 0, 0.15);
  max-width: 1000px;
  width: 100%;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 모달 헤더 */
.detail-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px;
  border-bottom: 2px solid #e2e8f0;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  flex-shrink: 0;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-header h3 {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.item-type {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  color: white;
}

.item-type.addition {
  background: linear-gradient(135deg, #10b981, #059669);
}

.item-type.deletion {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.item-type.modification {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.item-type.agreement {
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
}

.item-type.conflict {
  background: linear-gradient(135deg, #8b5cf6, #6d28d9);
}

.close-btn {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 8px;
  background: rgba(0, 0, 0, 0.08);
  color: #1e293b;
  font-size: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.close-btn:hover {
  background: rgba(0, 0, 0, 0.15);
}

/* 문서 정보 영역 */
.document-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 16px 24px;
  background: rgba(241, 245, 249, 0.6);
  border-bottom: 1px solid #e2e8f0;
}

.doc-info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #475569;
}

.doc-icon {
  font-size: 18px;
}

.doc-name {
  max-width: 250px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.divider-text {
  color: #cbd5e1;
  font-weight: 600;
  font-size: 12px;
}

/* 비교 콘텐츠 */
.detail-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2px;
  flex: 1;
  overflow: hidden;
}

/* 문서 패널 */
.document-panel {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  border-right: 1px solid #e2e8f0;
}

.panel-b {
  border-right: none;
}

.panel-header {
  padding: 16px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.05), rgba(139, 92, 246, 0.05));
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

/* 패널 본문 */
.panel-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: rgba(255, 255, 255, 0.95);
}

.text-content {
  width: 100%;
}

.original-text {
  font-size: 14px;
  color: #334155;
  line-height: 1.8;
  white-space: pre-wrap;
  word-break: break-word;
  margin: 0;
  background: rgba(248, 250, 252, 0.7);
  padding: 12px;
  border-radius: 6px;
  border-left: 3px solid #3b82f6;
}

.empty-text {
  color: #94a3b8;
  font-style: italic;
  text-align: center;
  padding: 32px 16px;
  margin: 0;
}

/* 모달 푸터 */
.detail-footer {
  display: flex;
  justify-content: flex-end;
  padding: 16px 24px;
  border-top: 1px solid #e2e8f0;
  background: rgba(248, 250, 252, 0.8);
  flex-shrink: 0;
}

.close-modal-btn {
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.close-modal-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 16px rgba(59, 130, 246, 0.3);
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
  .comparison-detail-modal {
    max-height: 90vh;
  }

  .detail-header {
    padding: 16px;
    flex-wrap: wrap;
    gap: 12px;
  }

  .header-title {
    width: 100%;
  }

  .detail-header h3 {
    font-size: 18px;
  }

  .item-type {
    padding: 3px 10px;
    font-size: 11px;
  }

  .close-btn {
    width: 32px;
    height: 32px;
    font-size: 18px;
  }

  .document-info {
    padding: 12px 16px;
    gap: 8px;
    flex-wrap: wrap;
  }

  .doc-info-item {
    font-size: 13px;
    gap: 6px;
  }

  .doc-name {
    max-width: 150px;
  }

  /* 모바일에서 1열 레이아웃 */
  .detail-content {
    grid-template-columns: 1fr;
    gap: 0;
  }

  .document-panel {
    border-right: none;
    border-bottom: 1px solid #e2e8f0;
  }

  .panel-b {
    border-right: none;
    border-bottom: none;
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

  .panel-body {
    padding: 12px;
  }

  .original-text {
    font-size: 13px;
    padding: 10px;
  }

  .detail-footer {
    padding: 12px 16px;
  }

  .close-modal-btn {
    width: 100%;
    padding: 10px 16px;
    font-size: 13px;
  }
}

/* 초소형 화면 */
@media (max-width: 480px) {
  .comparison-detail-modal {
    border-radius: 12px;
    max-width: 100%;
  }

  .detail-header {
    padding: 12px 16px;
  }

  .detail-header h3 {
    font-size: 16px;
  }

  .document-info {
    padding: 10px 12px;
    gap: 6px;
    font-size: 12px;
  }

  .doc-info-item {
    font-size: 12px;
  }

  .doc-name {
    max-width: 100px;
    font-size: 12px;
  }

  .panel-header {
    padding: 10px;
  }

  .panel-header h4 {
    font-size: 11px;
  }

  .panel-meta {
    font-size: 10px;
  }

  .panel-body {
    padding: 10px;
  }

  .original-text {
    font-size: 12px;
    padding: 8px;
  }
}
</style>
