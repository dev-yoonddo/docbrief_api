# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**DocBrief** is a document summarization application that parses various document formats (PDF, DOCX, plain text, URLs) and generates AI-powered summaries using Google Gemini API. The backend is a Spring Boot REST API, and the frontend is a Vue 3 + Vite single-page application.

## 언어 및 커뮤니케이션 규칙

- **기본 응답 언어**: 한국어 - Claude Code는 모든 응답과 설명을 한국어로 제공합니다
- **코드 주석**: 한국어로 작성 - Java/JavaScript 코드 내 주석은 한국어로 작성합니다
- **커밋 메시지**: 한국어로 작성 - `git commit`은 한국어로 진행합니다
- **문서화**: 한국어로 작성 - CLAUDE.md, README 등 프로젝트 문서는 한국어로 작성합니다
- **변수명/함수명**: 영어 사용 - 코드 표준 준수를 위해 변수, 함수, 클래스명은 영어로 유지합니다

## Architecture

### Backend (Spring Boot + Java 17)
- **Framework**: Spring Boot 3.5.9 with Gradle build system
- **Database**: PostgreSQL with Spring Data JPA
- **Key Packages**:
  - `document.*`: Document upload, parsing, and storage (controllers, services, repositories)
  - `summary.*`: AI summarization engine, prompt configuration, and Gemini/OpenAI client integration
  - `common.*`: Shared exception handling, error responses, business exceptions

### Frontend (Vue 3 + Vite)
- **Location**: `docbrief_front/`
- **Build Tool**: Vite with Vue 3 plugin
- **Dev Server**: Vite dev server on port 5173 with proxy to backend on port 8080
- **Key Components**:
  - `SummaryView.vue`: Main document input and summary display component
  - `SummaryArchive.vue`: Archive/history of summaries
  - `SummaryApi.js`: API client for communicating with backend
- **CORS**: Configured to allow localhost:5173

### Document Processing Pipeline
1. **Upload**: File/URL/text input via `DocumentController` endpoints
2. **Parsing**: Document type detection and format-specific parsers (PDF, DOCX, plain text, URL)
3. **Storage**: Structured as paragraphs → sentences in the database
4. **Summarization**: AI processing via `SummaryController` with configurable prompts
5. **Response**: JSON with summary text and highlights (keywords/sentences)

## Build & Run

### Backend
```bash
# Build (compiles Java and packages static assets from frontend build)
./gradlew build

# Run locally
./gradlew bootRun

# Run single test
./gradlew test --tests com.docbrief.SomeTest

# All tests
./gradlew test
```

Backend runs on `http://localhost:8080` by default.

### Frontend
```bash
# Install dependencies
cd docbrief_front && npm install

# Dev server (auto-proxies /documents/* to backend)
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

Frontend dev server runs on `http://localhost:5173` and proxies API calls to the backend.

## API Endpoints

### Documents
- `POST /documents` - Create/upload a document (mode: "file", "url", or "text")
- `GET /documents/{id}/status` - Get document parsing status
- `POST /documents/{id}/process` - Process document for summarization

### Summary
- `POST /{id}/summary` - Request AI summary with parsed document content

## Key Configuration

### Prompt Configuration (`application.yml`)
The summarization prompt is defined in `prompt.summary` section with:
- **role**: AI behavior definition
- **instruction**: Task description (Korean)
- **constraint**: Rules for summary output (length, format, etc.)
- **highlight-rule**: Guidelines for keyword/sentence highlights (max 7 keywords, 2 sentences, 8 total)
- **output-format**: Expected JSON structure with summaryText, highlights array, violationReason
- **violation-reasons**: When/how to report rule violations

### Database
PostgreSQL connection configured in `application.yml`. Entities use standard JPA annotations with relationships for Document → Paragraphs → Sentences.

## Code Patterns

### Exception Handling
Custom exception hierarchy in `common.*` package:
- `BusinessException`: Base exception with error codes/messages
- `DocumentParsingException`, `SummaryProcessingException`: Domain-specific exceptions
- `GlobalExceptionHandler`: Centralized exception-to-response mapping

### AI Integration
- `LlmClient`: Abstract base for LLM providers
- `GeminiClient`, `OpenAiClient`: Concrete implementations
- `PromptBuilder`: Constructs prompts from configuration
- `GeminiRequest/Response`, `OpenAiRequest/Response`: Data transfer objects

### Document Parsing
- `DocumentTypeResolver`: Detects file type from extension
- `ParserFactory`: Creates appropriate parser based on type
- `DocumentParser`: Interface; implementations: `PdfDocumentParser`, `DocxDocumentParser`, `TxtDocumentParser`, `UrlDocumentParser`, `PlainTextDocumentParser`

## Frontend Development Notes

- Vue 3 Composition API with `<script setup>` syntax
- CSS in `assets/main.css` (shared styles) and component scoped styles
- API calls via `SummaryApi.js` (axios-based client)
- Session management via `HttpSession` on backend; client receives session ID in responses

## Testing

- Minimal test coverage; main test: `DocbriefApplicationTests.java`
- No UI tests; manual testing required for Vue components
- Backend integration tests should target the document parsing and summarization pipeline

## Dependencies to Note

- **Document Parsing**: Apache POI (DOCX), PDFBox (PDF), JSoup (URL)
- **AI**: Google Gemini SDK, OpenAI SDK (fallback)
- **HTTP**: Spring WebFlux for async calls to external APIs
- **Utility**: Lombok for annotation-based boilerplate reduction

## Git Workflow

- **Branch naming**: Descriptive names (e.g., `flow-summary`)
- **Recent commits**: CSS fixes and refactoring; focus on reducing console logs and improving error handling
- **Commit messages**: Korean language in recent history
