<template>
  <div class="admin-page">
    <el-card>
      <template #header>
        <div class="header-row">
          <span>新闻管理</span>
          <div>
            <el-button type="primary" @click="openCreate">发布新闻</el-button>
            <el-button @click="openImport">导入新闻</el-button>
          </div>
        </div>
      </template>

      <div class="toolbar">
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 120px">
          <el-option label="待审核" :value="1" />
          <el-option label="已发布" :value="2" />
          <el-option label="驳回" :value="3" />
        </el-select>
        <el-select v-model="query.categoryId" placeholder="分类" clearable style="width: 160px">
          <el-option v-for="cat in categoryOptions" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
        <el-select v-model="query.tagId" placeholder="标签" clearable style="width: 160px">
          <el-option v-for="tag in tagOptions" :key="tag.id" :label="tag.name" :value="tag.id" />
        </el-select>
        <el-input v-model="query.keyword" placeholder="标题" clearable style="width: 220px" />
        <el-button type="primary" @click="onSearch">搜索</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="authorName" label="作者" width="120" show-overflow-tooltip />
        <el-table-column label="分类" width="120">
          <template #default="{ row }">
            <el-tag v-if="getCategoryName(row.categoryId)" size="small" type="info">
              {{ getCategoryName(row.categoryId) }}
            </el-tag>
            <span v-else style="color:#94a3b8">--</span>
          </template>
        </el-table-column>
        <el-table-column label="标签" min-width="180">
          <template #default="{ row }">
            <div v-if="row.tags && row.tags.length" class="tag-cell">
              <el-tag v-for="tag in row.tags" :key="`${row.id}-${tag.id}`" size="small" effect="plain">{{ tag.name }}</el-tag>
            </div>
            <span v-else style="color:#94a3b8">--</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="info">草稿</el-tag>
            <el-tag v-else-if="row.status === 1" type="warning">待审核</el-tag>
            <el-tag v-else-if="row.status === 2" type="success">已发布</el-tag>
            <el-tag v-else type="danger">驳回</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reviewRemark" label="驳回原因" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-if="row.status === 3">{{ row.reviewRemark || '--' }}</span>
            <span v-else style="color:#94a3b8">--</span>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="90" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" type="success" link @click="audit(row, 2)">通过</el-button>
            <el-button v-if="row.status === 1" type="danger" link @click="openReject(row)">驳回</el-button>
            <el-button type="primary" link @click="viewDetail(row)">查看</el-button>
            <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="pager" background layout="prev, pager, next"
                     :current-page="query.pageNum" :page-size="query.pageSize" :total="total"
                     @current-change="p => { query.pageNum = p; loadData(); }" />
    </el-card>

    <el-dialog v-model="detailVisible" width="860px" title="新闻详情">
      <div v-if="currentNews" class="detail-card">
        <h2 class="detail-title">{{ currentNews.title }}</h2>
        <div class="detail-meta">
          <span>作者：{{ currentNews.authorName || '--' }}</span>
          <span>分类：{{ getCategoryName(currentNews.categoryId) || '--' }}</span>
          <span>状态：{{ statusText(currentNews.status) }}</span>
          <span>发布时间：{{ currentNews.publishTime || '--' }}</span>
        </div>
        <div v-if="currentNews.status === 3" style="margin-top:8px;color:#dc2626;">驳回原因：{{ currentNews.reviewRemark || '--' }}</div>
        <div class="tag-cell" style="margin: 8px 0 12px;">
          <el-tag v-for="tag in (currentNews.tags || [])" :key="`d-${tag.id}`" size="small" effect="plain">{{ tag.name }}</el-tag>
        </div>
        <el-divider />
        <div class="detail-block">
          <div class="detail-block-title">摘要</div>
          <div class="detail-summary">{{ currentNews.summary || '暂无摘要' }}</div>
        </div>
        <div class="detail-block">
          <div class="detail-block-title">内容</div>
          <div class="detail-content" v-html="currentNews.content || '暂无内容'"></div>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="createVisible" width="760px" title="发布新闻">
      <el-form :model="createForm" label-width="80px">
        <el-form-item label="标题"><el-input v-model="createForm.title" /></el-form-item>
        <el-form-item label="作者ID"><el-input v-model.number="createForm.authorId" type="number" /></el-form-item>
        <el-form-item label="摘要"><el-input v-model="createForm.summary" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="createForm.content"
            type="textarea"
            :rows="8"
            maxlength="20000"
            show-word-limit
            placeholder="可输入文本，或通过下方按钮插入图片/GIF/视频"
          />
          <div class="media-upload-row">
            <el-upload
              action="/api/upload/image"
              :show-file-list="false"
              accept="image/*"
              :before-upload="beforeImageUpload"
              :on-success="onCreateImageUploadSuccess"
            >
              <el-button size="small" plain>插入图片/GIF</el-button>
            </el-upload>
            <el-upload
              action="/api/upload/video"
              :show-file-list="false"
              accept="video/mp4,video/webm,video/ogg"
              :before-upload="beforeVideoUpload"
              :on-success="onCreateVideoUploadSuccess"
            >
              <el-button size="small" plain>插入视频</el-button>
            </el-upload>
          </div>
          <div class="content-preview" v-if="createForm.content.trim()">
            <div class="content-preview-title">内容预览</div>
            <div class="content-preview-body" v-html="createForm.content"></div>
          </div>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="createForm.categoryId" clearable style="width:100%">
            <el-option v-for="cat in categoryOptions" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="createForm.tagIds" multiple collapse-tags style="width:100%">
            <el-option v-for="tag in tagOptions" :key="tag.id" :label="tag.name" :value="tag.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="createForm.status" style="width:160px">
            <el-option label="待审核" :value="1" />
            <el-option label="已发布" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitCreate">发布</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editVisible" width="760px" title="编辑新闻">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="标题"><el-input v-model="editForm.title" /></el-form-item>
        <el-form-item label="作者ID"><el-input v-model.number="editForm.authorId" type="number" /></el-form-item>
        <el-form-item label="摘要"><el-input v-model="editForm.summary" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="内容">
          <el-input
            v-model="editForm.content"
            type="textarea"
            :rows="8"
            maxlength="20000"
            show-word-limit
            placeholder="可输入文本，或通过下方按钮插入图片/GIF/视频"
          />
          <div class="media-upload-row">
            <el-upload
              action="/api/upload/image"
              :show-file-list="false"
              accept="image/*"
              :before-upload="beforeImageUpload"
              :on-success="onEditImageUploadSuccess"
            >
              <el-button size="small" plain>插入图片/GIF</el-button>
            </el-upload>
            <el-upload
              action="/api/upload/video"
              :show-file-list="false"
              accept="video/mp4,video/webm,video/ogg"
              :before-upload="beforeVideoUpload"
              :on-success="onEditVideoUploadSuccess"
            >
              <el-button size="small" plain>插入视频</el-button>
            </el-upload>
          </div>
          <div class="content-preview" v-if="editForm.content.trim()">
            <div class="content-preview-title">内容预览</div>
            <div class="content-preview-body" v-html="editForm.content"></div>
          </div>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="editForm.categoryId" clearable style="width:100%">
            <el-option v-for="cat in categoryOptions" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-select v-model="editForm.tagIds" multiple collapse-tags style="width:100%">
            <el-option v-for="tag in tagOptions" :key="tag.id" :label="tag.name" :value="tag.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" style="width:160px">
            <el-option label="待审核" :value="1" />
            <el-option label="已发布" :value="2" />
            <el-option label="驳回" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="updating" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="importVisible" width="760px" title="导入新闻（JSON数组）">
      <el-alert type="info" :closable="false" style="margin-bottom:10px" title="格式示例：[{title, content, summary, authorId, categoryId, status}]" />
      <el-input v-model="importText" type="textarea" :rows="12" placeholder="请粘贴JSON数组" />
      <template #footer>
        <el-button @click="importVisible = false">取消</el-button>
        <el-button type="primary" :loading="submittingImport" @click="submitImport">开始导入</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="rejectVisible" width="520px" title="驳回新闻">
      <el-form label-width="90px">
        <el-form-item label="驳回原因">
          <el-input v-model="rejectRemark" type="textarea" :rows="4" maxlength="255" show-word-limit placeholder="请输入驳回原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" @click="submitReject">确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { adminNews, adminNewsCategory } from '../api/admin';

const loading = ref(false);
const tableData = ref([]);
const total = ref(0);
const tagOptions = ref([]);
const categoryOptions = ref([]);

const detailVisible = ref(false);
const currentNews = ref(null);
const createVisible = ref(false);
const submitting = ref(false);
const editVisible = ref(false);
const updating = ref(false);
const importVisible = ref(false);
const importText = ref('');
const submittingImport = ref(false);
const rejectVisible = ref(false);
const rejectRemark = ref('');
const rejectTargetId = ref(null);

const query = reactive({ keyword: '', status: null, categoryId: null, tagId: null, pageNum: 1, pageSize: 10 });
const createForm = reactive({ title: '', authorId: null, summary: '', content: '', categoryId: null, tagIds: [], status: 1 });
const editForm = reactive({ id: null, title: '', authorId: null, summary: '', content: '', categoryId: null, tagIds: [], status: 1, reviewRemark: '' });

const loadTags = async () => {
  try { tagOptions.value = await adminNews.listTags(); } catch { tagOptions.value = []; }
};

const loadCategories = async () => {
  try { categoryOptions.value = await adminNewsCategory.list(); } catch { categoryOptions.value = []; }
};

const loadData = async () => {
  loading.value = true;
  try {
    const res = await adminNews.page(query);
    const records = res.records || [];
    await Promise.all(records.map(async (item) => {
      try { item.tags = await adminNews.getTags(item.id); } catch { item.tags = []; }
    }));
    tableData.value = records;
    total.value = res.total || 0;
  } finally {
    loading.value = false;
  }
};

const onSearch = () => {
  query.pageNum = 1;
  loadData();
};

const getCategoryName = (id) => {
  const cat = categoryOptions.value.find((c) => c.id === id);
  return cat ? cat.name : '';
};

const statusText = (s) => (s === 0 ? '草稿' : s === 1 ? '待审核' : s === 2 ? '已发布' : '驳回');

const viewDetail = async (row) => {
  try {
    const detail = await adminNews.get(row.id);
    currentNews.value = {
      ...row,
      ...detail,
      tags: detail?.tags?.length ? detail.tags : (row.tags || [])
    };
  } catch (e) {
    currentNews.value = row;
    ElMessage.warning(e.message || '新闻详情加载失败，已展示列表中的缓存内容');
  }
  detailVisible.value = true;
};

const openCreate = () => {
  createForm.title = '';
  createForm.authorId = null;
  createForm.summary = '';
  createForm.content = '';
  createForm.categoryId = null;
  createForm.tagIds = [];
  createForm.status = 1;
  createVisible.value = true;
};

const appendMediaToForm = (form, markup) => {
  form.content = `${form.content || ''}\n${markup}\n`;
};

const beforeImageUpload = (file) => {
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片/GIF 文件');
    return false;
  }
  if (file.size / 1024 / 1024 > 8) {
    ElMessage.error('图片/GIF 大小不能超过 8MB');
    return false;
  }
  return true;
};

const beforeVideoUpload = (file) => {
  const allow = ['video/mp4', 'video/webm', 'video/ogg'];
  if (!allow.includes(file.type)) {
    ElMessage.error('仅支持mp4/webm/ogg 视频');
    return false;
  }
  if (file.size / 1024 / 1024 > 50) {
    ElMessage.error('视频大小不能超过 50MB');
    return false;
  }
  return true;
};

const onCreateImageUploadSuccess = (res) => {
  if (res.code !== 0 || !res.data) return ElMessage.error(res.message || '图片上传失败');
  appendMediaToForm(createForm, `<img src="${res.data}" alt="image" style="max-width:100%;border-radius:8px;" />`);
  ElMessage.success('图片已插入内容');
};

const onCreateVideoUploadSuccess = (res) => {
  if (res.code !== 0 || !res.data) return ElMessage.error(res.message || '视频上传失败');
  appendMediaToForm(createForm, `<video src="${res.data}" controls style="max-width:100%;border-radius:8px;"></video>`);
  ElMessage.success('视频已插入内容');
};

const onEditImageUploadSuccess = (res) => {
  if (res.code !== 0 || !res.data) return ElMessage.error(res.message || '图片上传失败');
  appendMediaToForm(editForm, `<img src="${res.data}" alt="image" style="max-width:100%;border-radius:8px;" />`);
  ElMessage.success('图片已插入内容');
};

const onEditVideoUploadSuccess = (res) => {
  if (res.code !== 0 || !res.data) return ElMessage.error(res.message || '视频上传失败');
  appendMediaToForm(editForm, `<video src="${res.data}" controls style="max-width:100%;border-radius:8px;"></video>`);
  ElMessage.success('视频已插入内容');
};

const submitCreate = async () => {
  if (!createForm.title.trim() || !createForm.content.trim() || !createForm.authorId) return ElMessage.warning("标题、内容、作者ID必填");
  submitting.value = true;
  try {
    await adminNews.create({ ...createForm });
    ElMessage.success('发布成功');
    createVisible.value = false;
    loadData();
  } catch (error) {
    void error;
  } finally {
    submitting.value = false;
  }
};

const openEdit = async (row) => {
  editForm.id = row.id;
  editForm.title = row.title || '';
  editForm.authorId = row.authorId || null;
  editForm.summary = row.summary || '';
  editForm.content = row.content || '';
  editForm.categoryId = row.categoryId ?? null;
  editForm.status = row.status ?? 1;
  editForm.reviewRemark = row.reviewRemark || '';
  try {
    const tags = await adminNews.getTags(row.id);
    editForm.tagIds = (tags || []).map(t => t.id);
  } catch {
    editForm.tagIds = [];
  }
  editVisible.value = true;
};

const submitEdit = async () => {
  if (!editForm.title.trim() || !editForm.content.trim() || !editForm.authorId) return ElMessage.warning("标题、内容、作者ID必填");
  updating.value = true;
  try {
    await adminNews.update(editForm.id, {
      title: editForm.title,
      authorId: editForm.authorId,
      summary: editForm.summary,
      content: editForm.content,
      categoryId: editForm.categoryId,
      tagIds: editForm.tagIds,
      status: editForm.status,
      reviewRemark: editForm.reviewRemark
    });
    ElMessage.success('更新成功');
    editVisible.value = false;
    loadData();
  } catch (error) {
    void error;
  } finally {
    updating.value = false;
  }
};

const openImport = () => {
  importText.value = '';
  importVisible.value = true;
};

const submitImport = async () => {
  let parsed;
  try { parsed = JSON.parse(importText.value || '[]'); } catch { return ElMessage.error('JSON格式错误'); }
  if (!Array.isArray(parsed) || parsed.length === 0) return ElMessage.warning("请提供非空数组");
  submittingImport.value = true;
  try {
    const count = await adminNews.importBatch(parsed);
    ElMessage.success(`导入完成：${count} 条`);
    importVisible.value = false;
    loadData();
  } catch (error) {
    void error;
  } finally {
    submittingImport.value = false;
  }
};

const audit = async (row, status) => {
  try {
    await adminNews.updateStatus(row.id, status);
    ElMessage.success('操作成功');
    loadData();
  } catch (error) {
    void error;
  }
};

const openReject = (row) => {
  rejectTargetId.value = row.id;
  rejectRemark.value = '';
  rejectVisible.value = true;
};

const submitReject = async () => {
  if (!rejectRemark.value.trim()) return ElMessage.warning("请填写驳回原因");
  try {
    await adminNews.updateStatus(rejectTargetId.value, 3, rejectRemark.value.trim());
    ElMessage.success('驳回成功');
    rejectVisible.value = false;
    loadData();
  } catch (error) {
    void error;
  }
};

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm("确定删除？", "提示", { type: 'warning' });
    await adminNews.delete(row.id);
    ElMessage.success('删除成功');
    loadData();
  } catch (e) {
    if (e === 'cancel' || e === 'close') return;
    void e;
  }
};

onMounted(async () => {
  await loadTags();
  await loadCategories();
  await loadData();
});
</script>

<style scoped>
.header-row { display:flex; justify-content:space-between; align-items:center; }
.toolbar { margin-bottom: 16px; display: flex; gap: 12px; }
.pager { margin-top: 16px; }
.tag-cell { display: flex; flex-wrap: wrap; gap: 6px; }
.detail-card { background: var(--app-surface-raised); border: 1px solid var(--app-border); border-radius: 10px; padding: 16px; }
.detail-title { margin: 0; font-size: 22px; color: var(--app-text-main); }
.detail-meta { margin-top: 8px; color: var(--app-text-sub); display: flex; gap: 16px; flex-wrap: wrap; }
.detail-block { margin-top: 10px; }
.detail-block-title {
  display: inline-block;
  font-size: 13px;
  font-weight: 600;
  color: var(--app-accent);
  background: rgba(147, 176, 234, 0.12);
  border: 1px solid rgba(147, 176, 234, 0.2);
  border-radius: 6px;
  padding: 2px 8px;
  margin-bottom: 8px;
}
.detail-summary {
  color: var(--app-text-sub);
  margin-bottom:10px;
  padding: 10px 12px;
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  border-radius: 8px;
  line-height: 1.7;
}
.detail-content {
  line-height:1.8;
  color: var(--app-text-main);
  padding: 12px;
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  border-radius: 8px;
}

.detail-content :deep(img),
.detail-content :deep(video) {
  display: block;
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 12px 0;
  object-fit: contain;
}

.media-upload-row {
  margin-top: 10px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.content-preview {
  margin-top: 12px;
  border: 1px solid var(--app-border);
  border-radius: 8px;
  background: var(--app-surface-raised);
}

.content-preview-title {
  font-size: 13px;
  color: var(--app-text-sub);
  font-weight: 600;
  padding: 8px 10px;
  border-bottom: 1px solid var(--app-border);
}

.content-preview-body {
  padding: 10px;
  color: var(--app-text-main);
  line-height: 1.8;
}

.content-preview-body :deep(img),
.content-preview-body :deep(video) {
  max-width: 100%;
  border-radius: 8px;
  margin: 8px 0;
}
</style>
