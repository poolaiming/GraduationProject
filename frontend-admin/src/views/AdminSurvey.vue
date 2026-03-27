<template>

  <div class="admin-page">

    <el-card>

      <template #header>

        <span>问卷调查</span>

        <el-button type="primary" style="float: right" @click="openDialog()">新增问卷</el-button>

      </template>

      <el-table :data="tableData" v-loading="loading">

        <el-table-column prop="id" label="ID" width="80" />

        <el-table-column prop="title" label="标题" min-width="200" />

        <el-table-column prop="startTime" label="开始时间" width="170" />

        <el-table-column prop="endTime" label="结束时间" width="170" />

        <el-table-column prop="status" label="状态" width="90">

          <template #default="{ row }">

            <el-tag v-if="row.status === 0" type="info">草稿</el-tag>

            <el-tag v-else-if="row.status === 1" type="success">发布</el-tag>

            <el-tag v-else type="info">已结束</el-tag>

          </template>

        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" width="180" />

        <el-table-column label="操作" width="300" fixed="right">

          <template #default="{ row }">

            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>

            <el-button type="success" link @click="manageQuestions(row)">题目管理</el-button>

            <el-button type="warning" link @click="viewStats(row)">统计</el-button>

            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>

          </template>

        </el-table-column>

      </el-table>

      <el-pagination class="pager" background layout="prev, pager, next" :current-page="pageNum" :page-size="pageSize" :total="total" @current-change="p => { pageNum = p; loadData(); }" />

    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑问卷' : '新增问卷'" width="520">

      <el-form :model="form" label-width="80px">

        <el-form-item label="标题">

          <el-input v-model="form.title" placeholder="问卷标题" />

        </el-form-item>

        <el-form-item label="说明">

          <el-input v-model="form.description" type="textarea" placeholder="问卷说明" />

        </el-form-item>

        <el-form-item label="开始时间">

          <el-date-picker v-model="form.startTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择开始时间" style="width:100%" />

        </el-form-item>

        <el-form-item label="结束时间">

          <el-date-picker v-model="form.endTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择结束时间" style="width:100%" />

        </el-form-item>

        <el-form-item label="状态">

          <el-radio-group v-model="form.status">

            <el-radio :label="0">草稿</el-radio>

            <el-radio :label="1">发布</el-radio>

            <el-radio :label="2">已结束</el-radio>

          </el-radio-group>

        </el-form-item>

      </el-form>

      <template #footer>

        <el-button @click="dialogVisible = false">取消</el-button>

        <el-button type="primary" @click="submit">确定</el-button>

      </template>

    </el-dialog>

    <el-dialog v-model="questionDialogVisible" title="题目管理" width="900px">

      <div style="margin-bottom: 16px">

        <el-button type="primary" @click="openQuestionForm()">新增题目</el-button>

      </div>

      <el-table :data="questions" v-loading="questionLoading">

        <el-table-column prop="orderNo" label="序号" width="80" />

        <el-table-column prop="title" label="题目" min-width="200" />

        <el-table-column prop="questionType" label="类型" width="100">

          <template #default="{ row }">{{ getQuestionType(row.questionType) }}</template>

        </el-table-column>

        <el-table-column prop="required" label="必答" width="80">

          <template #default="{ row }">

            <el-tag v-if="row.required" type="danger" size="small">是</el-tag>

            <el-tag v-else type="info" size="small">否</el-tag>

          </template>

        </el-table-column>

        <el-table-column label="操作" width="200">

          <template #default="{ row }">

            <el-button type="primary" link size="small" @click="openQuestionForm(row)">编辑</el-button>

            <el-button type="success" link size="small" @click="manageOptions(row)">选项</el-button>

            <el-button type="danger" link size="small" @click="deleteQuestion(row)">删除</el-button>

          </template>

        </el-table-column>

      </el-table>

    </el-dialog>

    <el-dialog v-model="questionFormVisible" :title="questionForm.id ? '编辑题目' : '新增题目'" width="600px">

      <el-form :model="questionForm" label-width="100px">

        <el-form-item label="题目标题"><el-input v-model="questionForm.title" placeholder="请输入题目" /></el-form-item>

        <el-form-item label="题目类型">

          <el-radio-group v-model="questionForm.questionType">

            <el-radio :label="1">单选题</el-radio>

            <el-radio :label="2">多选题</el-radio>

            <el-radio :label="3">判断题</el-radio>

          </el-radio-group>

        </el-form-item>

        <el-form-item label="是否必答"><el-switch v-model="questionForm.required" :active-value="1" :inactive-value="0" /></el-form-item>

        <el-form-item label="分数"><el-input-number v-model="questionForm.score" :min="0" /></el-form-item>

        <el-form-item label="排序号"><el-input-number v-model="questionForm.orderNo" :min="0" /></el-form-item>

        <el-form-item label="选项" v-if="!questionForm.id">

          <div class="option-list">

            <div v-for="(opt, idx) in questionForm.options" :key="idx" class="option-row">

              <span class="opt-index">{{ idx + 1 }}</span>

              <el-input v-model="opt.optionLabel" placeholder="标识(A/B/C)" style="width: 90px" />

              <el-input v-model="opt.optionContent" placeholder="选项内容" style="flex: 1" />

              <el-button type="danger" plain size="small" @click="questionForm.options.splice(idx, 1)">删除</el-button>

            </div>

            <el-button size="small" @click="questionForm.options.push({ optionLabel: '', optionContent: '', orderNo: questionForm.options.length })">+ 添加选项</el-button>

          </div>

        </el-form-item>

      </el-form>

      <template #footer>

        <el-button @click="questionFormVisible = false">取消</el-button>

        <el-button type="primary" @click="submitQuestion">确定</el-button>

      </template>

    </el-dialog>

    <el-dialog v-model="optionDialogVisible" title="选项管理" width="700px">

      <div style="margin-bottom: 16px"><el-button type="primary" @click="openOptionForm()">新增选项</el-button></div>

      <el-table :data="options" v-loading="optionLoading">

        <el-table-column prop="optionLabel" label="标识" width="100" />

        <el-table-column prop="optionContent" label="内容" min-width="200" />

        <el-table-column prop="orderNo" label="排序" width="80" />

        <el-table-column label="操作" width="150">

          <template #default="{ row }">

            <el-button type="primary" link size="small" @click="openOptionForm(row)">编辑</el-button>

            <el-button type="danger" link size="small" @click="deleteOption(row)">删除</el-button>

          </template>

        </el-table-column>

      </el-table>

    </el-dialog>

    <el-dialog v-model="optionFormVisible" :title="optionForm.id ? '编辑选项' : '新增选项'" width="500px">

      <el-form :model="optionForm" label-width="100px">

        <el-form-item label="选项标识"><el-input v-model="optionForm.optionLabel" placeholder="如A/B/C" /></el-form-item>

        <el-form-item label="选项内容"><el-input v-model="optionForm.optionContent" placeholder="选项内容" /></el-form-item>

        <el-form-item label="排序号"><el-input-number v-model="optionForm.orderNo" :min="0" /></el-form-item>

      </el-form>

      <template #footer>

        <el-button @click="optionFormVisible = false">取消</el-button>

        <el-button type="primary" @click="submitOption">确定</el-button>

      </template>

    </el-dialog>

    <el-dialog v-model="statsDialogVisible" title="问卷统计" width="900px">

      <div v-if="stats">

        <div style="margin-bottom: 20px">

          <h3>{{ stats.title }}</h3>

          <p>总答卷数：{{ stats.totalAnswers }}</p>

        </div>

        <div v-for="q in stats.questions" :key="q.id" style="margin-bottom: 24px; padding: 16px; background: #f5f7fa; border-radius: 4px;">

          <div style="font-weight: 600; margin-bottom: 12px">{{ q.title }} ({{ getQuestionType(q.questionType) }})</div>

          <el-table :data="q.options" size="small">

            <el-table-column prop="optionLabel" label="选项" width="80" />

            <el-table-column prop="optionContent" label="内容" min-width="200" />

            <el-table-column prop="count" label="选择人数" width="120" />

            <el-table-column prop="percentage" label="占比" width="100" />

          </el-table>

        </div>

      </div>

    </el-dialog>

  </div>

</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { adminSurvey } from '../api/admin';

const loading = ref(false);
const tableData = ref([]);
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const dialogVisible = ref(false);
const form = reactive({ id: null, title: '', description: '', startTime: '', endTime: '', status: 0 });
const questionDialogVisible = ref(false);
const questionLoading = ref(false);
const questions = ref([]);
const currentSurveyId = ref(null);
const questionFormVisible = ref(false);
const questionForm = reactive({ id: null, title: '', questionType: 1, required: 1, score: 0, orderNo: 0, options: [] });
const optionDialogVisible = ref(false);
const optionLoading = ref(false);
const options = ref([]);
const currentQuestionId = ref(null);
const optionFormVisible = ref(false);
const optionForm = reactive({ id: null, optionLabel: '', optionContent: '', orderNo: 0 });
const statsDialogVisible = ref(false);
const stats = ref(null);

const loadData = async () => {
  loading.value = true;
  try {
    const res = await adminSurvey.page({ pageNum: pageNum.value, pageSize: pageSize.value });
    tableData.value = res.records || [];
    total.value = res.total || 0;
  } finally {
    loading.value = false;
  }
};

const openDialog = (row) => {
  if (row) {
    Object.assign(form, {
      id: row.id,
      title: row.title,
      description: row.description || '',
      startTime: row.startTime || '',
      endTime: row.endTime || '',
      status: row.status ?? 0
    });
  } else {
    Object.assign(form, { id: null, title: '', description: '', startTime: '', endTime: '', status: 0 });
  }
  dialogVisible.value = true;
};

const submit = async () => {
  if (!form.title) {
    ElMessage.warning('请输入标题');
    return;
  }
  if (form.startTime && form.endTime && new Date(form.startTime).getTime() >= new Date(form.endTime).getTime()) {
    ElMessage.warning('结束时间必须晚于开始时间');
    return;
  }

  try {
    if (form.id) {
      await adminSurvey.update(form.id, form);
    } else {
      await adminSurvey.create(form);
    }
    ElMessage.success('保存成功');
    dialogVisible.value = false;
    loadData();
  } catch (e) {
    void e;
  }
};

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除吗', '提示', { type: 'warning' });
    await adminSurvey.delete(row.id);
    ElMessage.success('删除成功');
    loadData();
  } catch (e) {
    if (e === 'cancel' || e === 'close') return;
    void e;
  }
};

const getQuestionType = (type) => ({ 1: '单选题', 2: '多选题', 3: '判断题' }[type] || '未知');

const manageQuestions = async (row) => {
  currentSurveyId.value = row.id;
  questionLoading.value = true;
  questionDialogVisible.value = true;
  try {
    questions.value = await adminSurvey.getQuestions(row.id);
  } catch (e) {
    void e;
  } finally {
    questionLoading.value = false;
  }
};

const openQuestionForm = (row) => {
  if (row) {
    Object.assign(questionForm, {
      id: row.id,
      title: row.title,
      questionType: row.questionType,
      required: row.required ?? 1,
      score: row.score ?? 0,
      orderNo: row.orderNo ?? 0,
      options: []
    });
  } else {
    Object.assign(questionForm, { id: null, title: '', questionType: 1, required: 1, score: 0, orderNo: 0, options: [] });
  }
  questionFormVisible.value = true;
};

const submitQuestion = async () => {
  if (!questionForm.title) return ElMessage.warning('请输入题目标题');

  try {
    if (questionForm.id) {
      await adminSurvey.updateQuestion(questionForm.id, questionForm);
    } else {
      await adminSurvey.createQuestion(currentSurveyId.value, questionForm);
    }
    ElMessage.success('保存成功');
    questionFormVisible.value = false;
    manageQuestions({ id: currentSurveyId.value });
  } catch (e) {
    void e;
  }
};

const deleteQuestion = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该题目？', '提示', { type: 'warning' });
    await adminSurvey.deleteQuestion(row.id);
    ElMessage.success('删除成功');
    manageQuestions({ id: currentSurveyId.value });
  } catch (e) {
    if (e === 'cancel' || e === 'close') return;
    void e;
  }
};

const manageOptions = async (row) => {
  currentQuestionId.value = row.id;
  optionLoading.value = true;
  optionDialogVisible.value = true;
  try {
    const data = await adminSurvey.getQuestions(currentSurveyId.value);
    const question = data.find((q) => q.id === row.id);
    options.value = question?.options || [];
  } catch (e) {
    void e;
  } finally {
    optionLoading.value = false;
  }
};

const openOptionForm = (row) => {
  if (row) {
    Object.assign(optionForm, {
      id: row.id,
      optionLabel: row.optionLabel || '',
      optionContent: row.optionContent || '',
      orderNo: row.orderNo ?? 0
    });
  } else {
    Object.assign(optionForm, { id: null, optionLabel: '', optionContent: '', orderNo: 0 });
  }
  optionFormVisible.value = true;
};

const submitOption = async () => {
  if (!optionForm.optionContent) return ElMessage.warning('请输入选项内容');

  try {
    if (optionForm.id) {
      await adminSurvey.updateOption(optionForm.id, optionForm);
    } else {
      await adminSurvey.createOption(currentQuestionId.value, optionForm);
    }
    ElMessage.success('保存成功');
    optionFormVisible.value = false;
    const question = questions.value.find((q) => q.id === currentQuestionId.value);
    if (question) manageOptions(question);
  } catch (e) {
    void e;
  }
};

const deleteOption = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该选项吗', '提示', { type: 'warning' });
    await adminSurvey.deleteOption(row.id);
    ElMessage.success('删除成功');
    const question = questions.value.find((q) => q.id === currentQuestionId.value);
    if (question) manageOptions(question);
  } catch (e) {
    if (e === 'cancel' || e === 'close') return;
    void e;
  }
};

const viewStats = async (row) => {
  try {
    stats.value = await adminSurvey.getStats(row.id);
    statsDialogVisible.value = true;
  } catch (e) {
    void e;
  }
};

onMounted(loadData);
</script>

<style scoped>

.pager { margin-top: 16px; }

.option-list {

  display: flex;

  flex-direction: column;

  gap: 8px;

  width: 100%;

}

.option-row {

  display: flex;

  align-items: center;

  gap: 8px;

  padding: 6px 10px;

  background: var(--app-surface-raised);

  border: 1px solid var(--app-border);

  border-radius: 8px;

}

.opt-index {

  display: inline-flex;

  align-items: center;

  justify-content: center;

  width: 22px;

  height: 22px;

  border-radius: 50%;

  background: rgba(147, 176, 234, 0.14);

  color: var(--app-accent);

  font-size: 12px;

  font-weight: 600;

  flex-shrink: 0;

}

</style>
