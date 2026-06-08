# 문서 비교분석 기능 개발 플랜

> 작성일: 2026-05-31  
> 대상 프로젝트: docbrief (Spring Boot 3.5 + Vue 3 + PostgreSQL)

---

## 1. 현재 프로젝트 구조 분석

### 백엔드 구조

```
com.docbrief
├── document
│   ├── controller/   DocumentController          (POST /documents, POST /documents/{id}/process, GET /documents/{id}/status)
│   ├── domain/       Document, DocumentContent, DocumentParagraph, DocumentSentence, DocumentStatus, DocumentType
│   ├── dto/
│   │   ├── api/      DocumentCreateRequest/Response, DocumentStatusResponse
│   │   └── internal/ SummaryInternalRequest (documentId, title, fullText, paragraphs[])
│   ├── parser/       DocumentParser (인터페이스), PdfDocumentParser, DocxDocumentParser, TxtDocumentParser, PlainTextDocumentParser, ParserFactory
│   ├── repository/   DocumentRepository, DocumentContentRepository, DocumentParagraphRepository, DocumentSentenceRepository
│   ├── service/      DocumentService, DocumentParsingService, DocumentStatusService, SummaryRequestService
│   └── text/         SentenceSplitter, TextNormalizer
├── summary
│   ├── ai/           LlmClient, GeminiClient, OpenAiClient, PromptBuilder, PromptProvider, PromptConfig
│   ├── controller/   SummaryController            (POST /{id}/summary)
│   ├── domain/       SummaryJob, SummaryResult, SummaryResponse (summaryText, highlights[], violationReason), SummarySessionResponse
│   ├── repository/   SummaryJobRepository, SummaryResultRepository
│   └── service/      SummaryProcessor, SummaryJobService, SummaryResultService, UrlDocumentParser
├── common/           GlobalExceptionHandler, 각종 Exception, ErrorCode, ErrorResponse
└── web/controller/   MainController               (SPA fallback: GET /{path})
```

**기술 스택:** Spring Boot 3.5, Java 17, JPA + PostgreSQL, Gemini 2.5 Flash (primary) / GPT-4o-mini (fallback), Apache POI (docx), PDFBox (pdf), Jsoup (url)

### 프론트엔드 구조

```
docbrief_front/
├── index.html          (Vue 3 마운트 포인트: #app)
├── vite.config.js      (Vite 7 빌드 설정)
├── package.json        (vue ^3.5.24, @vitejs/plugin-vue)
└── src/
    └── main.js         (Vue 앱 진입점 — 빌드 후 static/assets/에 배포)
```

빌드 결과물: `src/main/resources/static/` (Spring Boot가 서빙)  
Spring `MainController`가 SPA 라우팅 fallback 처리

### 현재 요약 기능 흐름

```
[클라이언트]
  │
  ├─ POST /documents {mode, file|url|text}
  │     → DocumentService.create()
  │     → Document 저장 (status: CREATED)
  │     ← documentId
  │
  ├─ POST /documents/{id}/process {mode, file|url|text}
  │     → DocumentService.processParsing()
  │     → DocumentParsingService (status: EXTRACTING)
  │         파서 선택(ParserFactory) → ParsedText(문단+문장) 파싱
  │         DocumentContent / DocumentParagraph / DocumentSentence 저장
  │     → status: EXTRACTED
  │     ← SummaryInternalRequest (documentId, title, fullText, paragraphs)
  │
  └─ POST /{id}/summary {type}  body: SummaryInternalRequest
        → SummaryProcessor.startSummaryEngine()
        → status: SUMMARIZING
        → PromptBuilder로 프롬프트 구성
        → GeminiClient.summarize() (429 시 OpenAiClient fallback)
        → SummaryResult 저장 (status: SUMMARIZED)
        ← SummarySessionResponse { sessionId, title, summaryText, highlights[], violationReason }
```

---

## 2. 문서 비교분석 기능 정의

### 기능 개요

두 개의 문서(또는 같은 문서의 두 버전)를 입력받아 AI가 **공통점 / 차이점 / 충돌 지점**을 구조적으로 분석하고, 원문의 구체적인 위치(문단·문장 순서)를 근거로 제시하는 기능.

### 입력 (Input)

| 항목 | 설명 |
|------|------|
| 문서 A | 기존 요약과 동일하게 파일(pdf/docx/txt) / URL / 직접 텍스트 입력 |
| 문서 B | 동일 방식으로 두 번째 문서 입력 |
| 비교 모드 (선택) | `full` (전체 비교) / `section` (지정 문단 범위만 비교) |

### 처리 (Process)

1. 문서 A, B 각각을 기존 파싱 파이프라인으로 처리 (DocumentParsingService 재사용)
2. `ComparisonJob` 생성 (두 documentId 연결)
3. 두 `SummaryInternalRequest`를 JSON 직렬화 후 비교 전용 프롬프트에 주입
4. AI(Gemini → OpenAI fallback)가 비교 분석 수행
5. 분석 결과를 `ComparisonResult`로 저장

### 출력 (Output)

```json
{
  "comparisonId": 1,
  "documentATitle": "계약서 v1",
  "documentBTitle": "계약서 v2",
  "overallSummary": "두 문서의 전체적인 관계 요약",
  "agreements": [
    {
      "description": "두 문서에서 공통으로 명시된 내용",
      "sourceA": { "paragraphOrder": 1, "sentenceOrder": 2 },
      "sourceB": { "paragraphOrder": 1, "sentenceOrder": 1 }
    }
  ],
  "differences": [
    {
      "description": "문서 A와 B의 차이 설명",
      "type": "ADDITION | DELETION | MODIFICATION",
      "sourceA": { "paragraphOrder": 2, "sentenceOrder": 1 },
      "sourceB": { "paragraphOrder": 2, "sentenceOrder": 3 }
    }
  ],
  "conflicts": [
    {
      "description": "내용이 서로 모순되거나 충돌하는 지점",
      "severity": "HIGH | MEDIUM | LOW",
      "sourceA": { "paragraphOrder": 3, "sentenceOrder": 1 },
      "sourceB": { "paragraphOrder": 4, "sentenceOrder": 2 }
    }
  ],
  "violationReason": null
}
```

---

## 3. 팀별 개발 플랜

### Phase 0 — 킥오프 & 설계 (기획자 주도)

**담당:** 기획자  
**기간:** 1주

#### 기획자 작업 항목

- [ ] **요구사항 문서 작성:** 비교분석 기능의 사용 시나리오(Use Case) 정의 — 계약서 비교, 논문 초안 비교, 버전 비교 등
- [ ] **UX 플로우 설계:** 두 문서 업로드 → 비교 실행 → 결과 뷰 화면 흐름 (와이어프레임)
- [ ] **비교 결과 화면 설계:** 좌우 분할(diff) 뷰 vs 통합 결과 뷰 방식 결정
- [ ] **비교 모드 정의:** 전체 비교 / 섹션 비교 / 키워드 중심 비교 중 MVP 범위 확정
- [ ] **API 명세 초안 작성:** 백엔드 팀과 협의할 엔드포인트 명세 (입력/출력 JSON 구조)
- [ ] **AI 출력 형식 협의:** 백엔드 팀과 함께 프롬프트 출력 스키마 최종 확정
- [ ] **검열자(QA)와 테스트 시나리오 사전 협의:** 어떤 케이스를 검증할지 목록 작성

**산출물:** 요구사항 정의서, 화면 와이어프레임, API 명세 초안

---

### Phase 1 — 백엔드 개발 (기획자 명세 기반)

**담당:** 백엔드 개발자  
**기간:** 2~3주  
**선행 조건:** Phase 0 완료 (API 명세 확정)

#### 백엔드 작업 항목

**① 도메인 / 엔티티**
- [ ] `ComparisonJob` 엔티티 생성: `comparisonId`, `documentAId`, `documentBId`, `status`, `createdAt`
- [ ] `ComparisonResult` 엔티티 생성: `jobId`, `content` (JSON), `createdAt`
- [ ] `ComparisonStatus` enum 추가: `CREATED`, `PROCESSING`, `COMPLETED`, `FAILED`
- [ ] PostgreSQL 테이블 DDL 작성 및 마이그레이션

**② Repository**
- [ ] `ComparisonJobRepository` 작성 (JPA)
- [ ] `ComparisonResultRepository` 작성 (JPA)

**③ DTO**
- [ ] `ComparisonCreateRequest`: `documentAId`, `documentBId`, `mode` (full/section)
- [ ] `ComparisonResponse`: agreements[], differences[], conflicts[], overallSummary, violationReason
- [ ] `ComparisonInternalRequest`: 두 `SummaryInternalRequest`를 포함하는 래퍼

**④ AI 프롬프트**
- [ ] `application.yml`에 비교 전용 프롬프트 섹션 추가 (role, instruction, constraint, output-format)
- [ ] `PromptSection` enum에 비교 관련 항목 추가: `COMPARE_ROLE`, `COMPARE_INSTRUCTION`, `COMPARE_OUTPUT_FORMAT` 등
- [ ] `PromptBuilder`에 `buildComparisonPrompt(String docAJson, String docBJson)` 메서드 추가

**⑤ Service**
- [ ] `ComparisonService` 작성:
  - 두 documentId 유효성 검증 (존재 여부, EXTRACTED 이상 상태 확인)
  - `ComparisonJob` 생성
  - 두 문서의 `SummaryInternalRequest` 조회 (기존 `SummaryRequestService.buildSummaryInternalRequest()` 재사용)
  - `ComparisonProcessor`에 위임
- [ ] `ComparisonProcessor` 작성 (기존 `SummaryProcessor` 패턴 참고):
  - 두 문서 JSON 직렬화
  - `buildComparisonPrompt()` 호출
  - Gemini → OpenAI fallback 호출
  - `ComparisonResult` 저장
  - Job 상태 관리 (PROCESSING → COMPLETED / FAILED)

**⑥ Controller**
- [ ] `ComparisonController` 작성:
  - `POST /comparisons` — 비교 작업 생성 및 실행: body `{ documentAId, documentBId, mode }`
  - `GET /comparisons/{id}` — 비교 결과 조회
  - `GET /comparisons/{id}/status` — 비교 작업 상태 조회

**⑦ 예외 처리**
- [ ] `ComparisonProcessingException` 추가
- [ ] `GlobalExceptionHandler`에 비교 관련 예외 핸들러 추가

**협업 포인트:**
- 기획자와 AI 출력 JSON 스키마 최종 합의 필요 (Phase 0에서 협의한 명세 기준)
- 프론트 팀과 CORS 설정 확인 (`localhost:5173` 기준 현재 설정 유지)
- 비교 결과가 클 경우 응답 지연 이슈 → 프론트와 polling / SSE 방식 사전 합의

---

### Phase 2 — 프론트엔드 개발 (백엔드 API 기반)

**담당:** 프론트엔드 개발자  
**기간:** 2주  
**선행 조건:** Phase 1 백엔드 API 완료 (또는 mock API 활용)

#### 프론트엔드 작업 항목

**① 라우팅**
- [ ] `/compare` 경로 추가 (Vue Router 설정)
- [ ] `/compare/result/:id` 경로 추가 (결과 뷰)

**② 컴포넌트**
- [ ] `CompareUpload.vue` — 문서 A / 문서 B 업로드 UI (기존 업로드 컴포넌트 재활용)
  - 각 문서별로 파일 / URL / 텍스트 입력 지원
  - 비교 모드 선택 (전체 / 섹션)
- [ ] `CompareResult.vue` — 비교 결과 전체 레이아웃
- [ ] `OverallSummary.vue` — 전체 요약 카드
- [ ] `DiffSection.vue` — agreements / differences / conflicts 탭 또는 섹션
  - `DiffItem.vue`: 각 항목 (설명 + 원문 출처 링크)
- [ ] `OriginalViewer.vue` — 원문 하이라이트 뷰어 (클릭 시 해당 문단·문장으로 스크롤)
- [ ] `CompareLoading.vue` — 비교 처리 중 로딩 상태 표시

**③ API 연동**
- [ ] `compareApi.js` 작성:
  - `createComparison(documentAId, documentBId, mode)` → `POST /comparisons`
  - `getComparisonResult(id)` → `GET /comparisons/{id}`
  - `getComparisonStatus(id)` → `GET /comparisons/{id}/status`
- [ ] 기존 문서 업로드 API 재사용 (각 문서별로 순서대로 create → process 호출)
- [ ] 처리 시간이 긴 경우 polling 또는 SSE 방식 적용 (백엔드와 합의한 방식)

**④ 상태 관리**
- [ ] 비교 작업 상태(PROCESSING / COMPLETED / FAILED) 관리
- [ ] 결과 데이터 구조 타입 정의

**⑤ UX**
- [ ] 기획자 와이어프레임 기반 좌우 diff 뷰 구현
- [ ] 원문 위치(paragraphOrder, sentenceOrder) 클릭 시 해당 원문 하이라이트 표시
- [ ] 모바일 반응형 대응

**협업 포인트:**
- 백엔드 API 미완성 시 mock 데이터로 선행 개발 후 연결
- 기획자와 결과 화면 UI 중간 점검 (와이어프레임 → 실제 구현 비교)
- 응답 지연 처리 방식 백엔드 팀과 최종 확인

---

### Phase 3 — 검증 및 QA (검열자 주도)

**담당:** 검열자 (QA/리뷰어)  
**기간:** 1~2주  
**선행 조건:** Phase 1 + Phase 2 기능 통합 완료

#### 검열자 작업 항목

**① API 테스트**
- [ ] `POST /comparisons` — 정상 케이스: 파일/URL/텍스트 조합별 비교 실행
- [ ] `GET /comparisons/{id}` — 결과 조회 응답 구조 검증 (agreements, differences, conflicts 필드 존재 확인)
- [ ] 잘못된 documentId 입력 시 적절한 에러 응답(404/400) 반환 여부
- [ ] 문서 파싱 전 (CREATED 상태) 문서로 비교 요청 시 에러 처리 확인
- [ ] Gemini quota 초과 시 OpenAI fallback 동작 확인

**② AI 출력 품질 검토**
- [ ] 동일 문서 두 개 비교 시 differences/conflicts가 비어있는지 확인
- [ ] 명백히 다른 두 문서 비교 시 differences 항목이 올바르게 추출되는지 확인
- [ ] `violationReason` 필드가 규칙 위반 시에만 채워지는지 확인
- [ ] `sourceA`, `sourceB`의 paragraphOrder/sentenceOrder가 실제 원문 위치와 일치하는지 확인

**③ 프론트엔드 기능 테스트**
- [ ] 문서 A / 문서 B 업로드 → 비교 실행 전체 플로우 E2E 테스트
- [ ] 로딩 상태(PROCESSING) 중 UI 정상 표시 여부
- [ ] 실패(FAILED) 상태 발생 시 에러 메시지 노출 여부
- [ ] 원문 위치 클릭 → 하이라이트 이동 동작 확인
- [ ] 다양한 브라우저 / 화면 크기에서의 렌더링 확인

**④ 코드 리뷰**
- [ ] `ComparisonProcessor`가 기존 `SummaryProcessor` 패턴을 일관되게 따르는지 확인
- [ ] 트랜잭션 경계 및 예외 처리 적절성 검토
- [ ] 프롬프트 출력 파싱 실패 케이스(JSON 형식 불일치) 핸들링 확인
- [ ] CORS 설정이 신규 엔드포인트에도 적용되어 있는지 확인

**⑤ 회귀 테스트**
- [ ] 기존 요약 기능(`POST /{id}/summary`) 정상 동작 유지 여부 확인
- [ ] Document 상태 머신(CREATED → EXTRACTED → SUMMARIZED)이 비교 기능 추가 후에도 정상 동작하는지 확인

---

## 4. 팀 간 의존성 및 협업 포인트

```
기획자 ──────→ 백엔드                프론트
   │         API 명세 기반 개발      │
   │              │                  │
   │         ComparisonController    │
   │              │                  │
   └──────────────┼──────────────────┘
                  │ API 연동 / mock 협의
                  │
                검열자
           (통합 완료 후 검증)
```

| 협업 포인트 | 관련 팀 | 시점 |
|------------|---------|------|
| 비교 결과 JSON 스키마 확정 | 기획자 ↔ 백엔드 | Phase 0 말 |
| 응답 지연 처리 방식 (polling vs SSE) | 백엔드 ↔ 프론트 | Phase 1 초반 |
| Mock API 제공 | 백엔드 → 프론트 | Phase 1 중반 (프론트 선행 개발용) |
| 중간 UI 점검 | 기획자 ↔ 프론트 | Phase 2 중반 |
| 통합 테스트 환경 구성 | 백엔드 ↔ 검열자 | Phase 3 시작 전 |
| AI 출력 품질 기준 확정 | 기획자 ↔ 검열자 | Phase 0 말 |

---

## 5. 개발 우선순위 및 예상 일정

| 순서 | 항목 | 담당 | 비고 |
|------|------|------|------|
| 1 | 요구사항 정의 / API 명세 / 와이어프레임 | 기획자 | 전체 블로커 |
| 2 | ComparisonJob/Result 도메인 + DB | 백엔드 | 병렬 가능 (명세 확정 후) |
| 3 | 비교 전용 AI 프롬프트 작성 | 백엔드 | 2번과 병렬 |
| 4 | ComparisonService + ComparisonProcessor | 백엔드 | 2, 3번 완료 후 |
| 5 | ComparisonController REST API | 백엔드 | 4번 완료 후 |
| 6 | 프론트 업로드 UI + API 연동 (mock) | 프론트 | 3번과 병렬 가능 |
| 7 | 프론트 결과 뷰 컴포넌트 구현 | 프론트 | 6번 완료 후 |
| 8 | 백엔드 ↔ 프론트 통합 | 백엔드 + 프론트 | 5, 7번 완료 후 |
| 9 | QA 전체 검증 + 코드 리뷰 | 검열자 | 8번 완료 후 |
| 10 | 수정 반영 및 배포 | 전체 | 9번 완료 후 |

**예상 총 기간:** 6~7주 (팀 규모 및 병렬 진행 여부에 따라 단축 가능)

---

## 6. 재사용 가능한 기존 코드

| 기존 코드 | 비교분석에서 재사용 방식 |
|-----------|------------------------|
| `DocumentParsingService` | 문서 A, B 각각 파싱 그대로 사용 |
| `SummaryRequestService.buildSummaryInternalRequest()` | 파싱된 문서를 `SummaryInternalRequest`로 변환 — 그대로 재사용 |
| `ParserFactory` + 각 파서 | 파일 타입별 파서 선택 로직 재사용 |
| `GeminiClient`, `OpenAiClient` | fallback 패턴 그대로 재사용 |
| `PromptBuilder` | `buildComparisonPrompt()` 메서드만 추가 |
| `SummaryJobService` 패턴 | `ComparisonJobService`를 동일 패턴으로 작성 |
| `DocumentController` CORS 설정 | `ComparisonController`에 동일하게 적용 |
