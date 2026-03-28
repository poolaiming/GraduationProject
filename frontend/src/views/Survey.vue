<template>
  <div class="survey-page">
    <div class="app-card">
      <div class="app-page-title">问卷调查</div>
      <div class="app-page-subtitle">参与平台已发布问卷，帮助优化内容与功能体验</div>

      <div class="toolbar">
        <el-input
            v-model="keyword"
            clearable
            placeholder="按问卷标题搜索"
            style="width: 260px"
            @keyup.enter="loadData"
        />
        <el-button type="primary" @click="onSearch">搜索</el-button>
        <el-button @click="onReset">重置</el-button>
      </div>

      <el-skeleton v-if="loading" :rows="8" animated style="margin-bottom: 10px" />
      <el-table v-else :data="tableData" size="small">
        <el-table-column prop="title" label="问卷标题" min-width="260" show-overflow-tooltip />
        <el-table-column prop="description" label="问卷简介" min-width="300" show-overflow-tooltip />
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="viewDetail(row)">查看详情</el-button>
            <el-button link type="success" size="small" @click="participate(row)">参与答题</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          class="pager"
          background
          layout="prev, pager, next"
          :current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          @current-change="onPageChange"
      />
    </div>

    <el-dialog v-model="detailVisible" title="问卷详情" width="700px">
      <div v-if="currentSurvey">
        <div class="detail-meta">
          <div class="detail-meta-row">
            <span class="detail-label">问卷标题</span>
            <span class="detail-value">{{ currentSurvey.title }}</span>
          </div>
          <div class="detail-meta-row">
            <span class="detail-label">问卷简介</span>
            <span class="detail-value">{{ currentSurvey.description || '无' }}</span>
          </div>
          <div class="detail-meta-row">
            <span class="detail-label">开始时间</span>
            <span class="detail-value">{{ currentSurvey.startTime || '无' }}</span>
          </div>
          <div class="detail-meta-row">
            <span class="detail-label">结束时间</span>
            <span class="detail-value">{{ currentSurvey.endTime || '无' }}</span>
          </div>
        </div>
        <div class="detail-section-title">题目列表</div>
        <div v-for="(q, idx) in currentSurvey.questions" :key="q.id" class="question-item">
          <div class="question-title">
            {{ idx + 1 }}. {{ q.title }}
            <el-tag size="small" type="info" style="margin-left: 8px">{{ getQuestionType(q.questionType) }}</el-tag>
          </div>
          <div v-for="opt in q.options" :key="opt.id" class="option-item">
            <span class="opt-label">{{ opt.optionLabel }}</span>{{ opt.optionContent }}
          </div>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="answerVisible" title="参与答题" width="700px">
      <div v-if="currentSurvey">
        <div class="survey-header">
          <div class="survey-header-title">{{ currentSurvey.title }}</div>
          <div v-if="currentSurvey.description" class="survey-header-desc">{{ currentSurvey.description }}</div>
          <div class="survey-header-time">
            <span v-if="currentSurvey.startTime"><el-icon><Calendar /></el-icon> 开始：{{ currentSurvey.startTime }}</span>
            <span v-if="currentSurvey.endTime"><el-icon><Calendar /></el-icon> 截止：{{ currentSurvey.endTime }}</span>
          </div>
        </div>
        <el-form :model="answerForm" label-position="top">
          <div v-for="(q, idx) in currentSurvey.questions" :key="q.id" class="answer-question">
            <div class="question-title">
              <span class="q-index">{{ idx + 1 }}</span>
              {{ q.title }}
              <el-tag v-if="q.required" size="small" type="danger" style="margin-left: 6px">必答</el-tag>
              <span class="question-type">{{ getQuestionType(q.questionType) }}</span>
            </div>
            <el-radio-group v-if="q.questionType === 1" v-model="answerForm.answers[q.id]" class="option-group">
              <el-radio v-for="opt in q.options" :key="opt.id" :label="opt.id" class="option-radio">
                <span class="opt-label">{{ opt.optionLabel }}</span>{{ opt.optionContent }}
              </el-radio>
            </el-radio-group>
            <el-checkbox-group v-else-if="q.questionType === 2" v-model="answerForm.answers[q.id]" class="option-group">
              <el-checkbox v-for="opt in q.options" :key="opt.id" :label="opt.id" class="option-radio">
                <span class="opt-label">{{ opt.optionLabel }}</span>{{ opt.optionContent }}
              </el-checkbox>
            </el-checkbox-group>
            <el-radio-group v-else-if="q.questionType === 3" v-model="answerForm.answers[q.id]" class="option-group">
              <el-radio v-for="opt in q.options" :key="opt.id" :label="opt.id" class="option-radio">
                {{ opt.optionContent }}
              </el-radio>
            </el-radio-group>
          </div>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="answerVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAnswer" :loading="submitting">提交答卷</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { Calendar } from '@element-plus/icons-vue';
import { useUserStore } from '../store/user';
import { fetchSurveyPage, fetchSurveyDetail, checkSurveySubmitted, submitSurveyAnswer } from '../api/survey';

const userStore = useUserStore();
const currentUser = computed(() => userStore.user);
const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const keyword = ref('');
const pageNum = ref(1);
const pageSize = ref(10);

const detailVisible = ref(false);
const answerVisible = ref(false);
const currentSurvey = ref(null);
const submitting = ref(false);

const answerForm = reactive({
  answers: {}
});

const loadData = async () => {
  loading.value = true;
  try {
    const page = await fetchSurveyPage({
      keyword: keyword.value || undefined,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    });
    tableData.value = page.records || [];
    total.value = page.total || 0;
  } catch (error) {
    void error;
    tableData.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

const onSearch = () => {
  pageNum.value = 1;
  loadData();
};

const onReset = () => {
  keyword.value = '';
  onSearch();
};

const onPageChange = (p) => {
  pageNum.value = p;
  loadData();
};

// 题目类型映射（1：单选题，2：多选题，3：评分题）
const getQuestionType = (type) => {
  const map = {
    1: '单选题',
    2: '多选题',
    3: '评分题'
  };
  return map[type] || '未知类型';
};

const viewDetail = async (row) => {
  try {
    const data = await fetchSurveyDetail(row.id);
    currentSurvey.value = data;
    detailVisible.value = true;
  } catch (error) {
    void error;
  }
};

const participate = async (row) => {
  if (!currentUser.value?.id) {
    ElMessage.warning('请先登录');
    return;
  }

  try {
    const check = await checkSurveySubmitted(row.id, currentUser.value.id);
    if (check.submitted) {
      ElMessage.warning('您已提交过该问卷，无法重复提交');
      return;
    }

    const data = await fetchSurveyDetail(row.id);
    currentSurvey.value = data;
    answerForm.answers = {};
    answerVisible.value = true;
  } catch (error) {
    void error;
  }
};

const submitAnswer = async () => {
  if (!currentUser.value?.id) {
    ElMessage.warning('请先登录');
    return;
  }

  // 验证必答题
  for (const q of currentSurvey.value.questions) {
    if (q.required) {
      const answer = answerForm.answers[q.id];
      if (!answer || (Array.isArray(answer) && answer.length === 0)) {
        ElMessage.warning(`请回答必答题：${q.title}`);
        return;
      }
    }
  }

  // 构造提交参数
  const answers = [];
  for (const q of currentSurvey.value.questions) {
    const answer = answerForm.answers[q.id];
    if (!answer) continue;

    if (Array.isArray(answer)) {
      // 多选题
      answers.push({
        questionId: q.id,
        optionIds: answer,
        answerText: null
      });
    } else {
      // 单选题/评分题
      answers.push({
        questionId: q.id,
        optionIds: [answer],
        answerText: null
      });
    }
  }

  submitting.value = true;
  try {
    await submitSurveyAnswer(currentSurvey.value.id, {
      userId: currentUser.value.id,
      answers
    });
    ElMessage.success('提交答卷成功');
    answerVisible.value = false;
  } catch (error) {
    void error;
  } finally {
    submitting.value = false;
  }
};

onMounted(loadData);
</script>

<style scoped>
.survey-page {
  max-width: 1240px;
  margin: 0 auto;
}

.toolbar {
  margin-bottom: 14px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  padding: 12px;
  border: 1px solid var(--app-panel-border);
  border-radius: 12px;
  background: var(--app-panel-bg-soft);
}

.pager {
  margin-top: 16px;
}

/* 详情弹窗样式 */
.detail-meta {
  background: var(--app-panel-bg-soft);
  border: 1px solid var(--app-panel-border);
  border-radius: 10px;
  padding: 14px 18px;
  margin-bottom: 18px;
}

.detail-meta-row {
  display: flex;
  align-items: baseline;
  padding: 6px 0;
  border-bottom: 1px dashed #e6edf7;
  line-height: 1.6;
}

.detail-meta-row:last-child {
  border-bottom: none;
}

.detail-label {
  flex-shrink: 0;
  width: 72px;
  color: var(--app-panel-text-muted);
  font-size: 13px;
}

.detail-value {
  color: var(--app-panel-text-main);
  font-size: 14px;
}

.detail-section-title {
  font-weight: 600;
  color: var(--app-panel-text);
  margin-bottom: 10px;
  font-size: 14px;
}

.question-item {
  margin-top: 10px;
  padding: 12px 14px;
  background: var(--app-panel-bg-soft);
  border-radius: 10px;
  border: 1px solid var(--app-panel-border);
}

.question-title {
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--app-panel-text-main);
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 4px;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 6px;
  color: var(--app-panel-text-sub);
  font-size: 13px;
}

.opt-label {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: var(--app-panel-accent-soft);
  color: var(--app-panel-accent-text);
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}

/* 答题弹窗样式 */
.survey-header {
  margin-bottom: 20px;
  padding: 14px 16px;
  border: 1px solid var(--app-panel-border);
  border-radius: 10px;
  background: var(--app-panel-bg-soft);
}

.survey-header-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--app-panel-text-main);
  margin-bottom: 6px;
}

.survey-header-desc {
  color: var(--app-panel-text-muted);
  font-size: 13px;
  margin-bottom: 8px;
}

.survey-header-time {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: var(--app-panel-text-faint);
}

.survey-header-time span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.answer-question {
  margin-bottom: 14px;
  padding: 14px 16px;
  background: var(--app-panel-bg-soft);
  border-radius: 10px;
  border: 1px solid var(--app-panel-border);
}

.answer-question .question-title {
  font-weight: 600;
  margin-bottom: 12px;
  color: var(--app-panel-text-main);
}

.q-index {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: var(--app-panel-accent-strong);
  color: var(--app-panel-accent-contrast);
  font-size: 12px;
  font-weight: 700;
  margin-right: 6px;
  flex-shrink: 0;
}

.question-type {
  color: var(--app-panel-text-faint);
  font-size: 12px;
  margin-left: 6px;
  font-weight: 400;
}

.option-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-left: 4px;
  width: 100%;
}

.option-radio {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 10px 14px;
  border-radius: 8px;
  border: 1px solid var(--app-panel-border);
  background: var(--app-panel-bg);
  transition: border-color 0.2s, background 0.2s;
  height: auto;
  line-height: 1.5;
  margin-right: 0 !important;
  box-sizing: border-box;
}

.option-radio:hover {
  border-color: var(--app-panel-border-strong);
  background: var(--app-panel-bg-soft);
}

.option-radio .opt-label {
  margin-right: 8px;
}

/* 修复el-radio/el-checkbox 组件label 换行问题 */
.option-radio :deep(.el-radio__label),
.option-radio :deep(.el-checkbox__label) {
  display: flex;
  align-items: center;
  flex: 1;
  white-space: normal;
  line-height: 1.5;
}
</style>
