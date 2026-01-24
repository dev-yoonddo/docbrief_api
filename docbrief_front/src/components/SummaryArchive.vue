<template>
  <div class="archive-container p-4">
    <h2 class="text-2xl font-bold mb-4">요약 보관함</h2>

    <div v-if="summaryList.length === 0" class="text-gray-500">
      보관된 요약이 없습니다.
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <div
        v-for="item in summaryList"
        :key="item.jobId"
        class="summary-card p-4 border rounded-lg shadow hover:shadow-md transition"
      >
        <div class="header flex justify-between items-center mb-2">
          <span class="job-id text-sm text-gray-500">Job ID: {{ item.jobId }}</span>
          <span class="session-id text-xs text-gray-400">Session: {{ item.sessionId }}</span>
        </div>

        <p class="summary-text mb-2">{{ item.summaryResponse.summaryText }}</p>

        <div class="highlights flex flex-wrap gap-2">
          <span
            v-for="(highlight, index) in item.summaryResponse.highlights"
            :key="index"
            :class="highlight.type === 'KEYWORD' ? 'keyword bg-yellow-100 text-yellow-800 px-2 py-1 rounded' : 'sentence bg-blue-100 text-blue-800 px-2 py-1 rounded'"
          >
            {{ highlight.value }}
          </span>
        </div>

        <div class="violation mt-2 text-sm text-gray-600">
          {{ item.summaryResponse.violationReason }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const summaryList = ref([]);
const sessionId = ref("");

// 페이지 로드 시 현재 세션ID 가져오기
onMounted(async () => {
  const res = await axios.get('/summary/session/init');
  sessionId.value = res.data;

  // 세션 기준으로 저장된 요약 기록 가져오기
  const archiveRes = await axios.get(`/summary/archive?sessionId=${sessionId.value}`);
  summaryList.value = archiveRes.data; // 서버에서 SummarySessionResponse[] 반환
});
</script>

<style scoped>
.keyword {
  font-weight: 500;
}
.sentence {
  font-weight: 400;
}
.summary-card {
  background-color: white;
}
</style>