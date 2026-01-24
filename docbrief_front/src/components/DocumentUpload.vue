<template>
  <div>
    <h2>DOC BRIEF</h2>

    <input type="file" @change="onFileChange" />
    <button @click="upload" :disabled="!file">
      업로드
    </button>

    <div v-if="documentId">
      <p>documentId: {{ documentId }}</p>
      <button @click="parse">파싱 요청</button>
    </div>

    <pre v-if="parseResult">
{{ parseResult }}
    </pre>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { uploadDocument, parseDocument } from "../api/documentApi";

const file = ref(null);
const documentId = ref(null);
const parseResult = ref(null);

function onFileChange(e) {
  file.value = e.target.files[0];
}

async function upload() {
    try{
        documentId.value = await uploadDocument(file.value);
    }catch(e){
        handleError(e);
    }
}

async function parse() {
    try{
        parseResult.value = await parseDocument(documentId.value, file.value);
    }catch(e){
        handleError(e);
    }
}
function handleError(e) {
  const error = e.response?.data;

  if (!error) {
    alert("알 수 없는 오류가 발생했습니다.");
    return;
  }

  alert(error.message);
}
</script>
