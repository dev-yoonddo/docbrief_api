<template>
  <div class="logo-container">
    <!-- DOC BRIEF 글씨를 귀엽게 스타일링 -->
    <span class="logo-text">
      DOC <span class="highlight">BRIEF</span>
    </span>
  </div>
</template>

<script setup>
// 특별한 로직 없음
</script>

<style scoped>
.logo-container {
  display: flex;
  justify-content: center; /* 가로 가운데 */
  align-items: center;     /* 세로 가운데 */
  height: 60px;            /* 필요하면 조정 */
}

.logo-text {
  font-size: 2rem;        /* 글씨 크기 */
  font-weight: bold;
  color: inherit;         /* 기존 색상 유지 */
  letter-spacing: 2px;    /* 귀여운 느낌을 위해 약간 띄움 */
}

.logo-text .highlight {
  font-size: 2.2rem;      /* "BRIEF"만 살짝 강조 */
  animation: bounce 1.5s infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}
</style>