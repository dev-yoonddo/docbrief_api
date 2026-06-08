<template>
  <div class="overall-summary-card">
    <div class="card-header">
      <span class="card-icon">📋</span>
      <h2>종합 요약</h2>
    </div>

    <p class="summary-text">
      {{ summary }}
    </p>
  </div>
</template>

<script setup>
/**
 * Props
 * @param {string} summary - 종합 요약 텍스트
 */
defineProps({
  summary: {
    type: String,
    required: true
  }
});
</script>

<style scoped>
.overall-summary-card {
  margin-bottom: 32px;
  padding: 32px;
  border-radius: 20px;
  background: linear-gradient(135deg, #dbeafe 0%, #ede9fe 100%);
  backdrop-filter: blur(6px);
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow:
    0 20px 40px rgba(59, 130, 246, 0.15),
    0 8px 16px rgba(59, 130, 246, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.card-icon {
  font-size: 28px;
}

.card-header h2 {
  font-size: 20px;
  font-weight: 700;
  color: #1e40af;
  margin: 0;
}

.summary-text {
  font-size: 15px;
  line-height: 1.8;
  color: #1e293b;
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  overflow-wrap: break-word;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .overall-summary-card {
    padding: 20px;
    margin-bottom: 24px;
  }

  .card-header h2 {
    font-size: 18px;
  }

  .card-icon {
    font-size: 24px;
  }

  .summary-text {
    font-size: 14px;
  }
}
</style>
