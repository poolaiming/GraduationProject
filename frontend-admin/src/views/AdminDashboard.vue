<template>



  <div class="admin-dashboard">



    <div class="stats-row">



      <el-card class="stat-card">



        <div class="stat-value">{{ stats.userCount ?? '-' }}</div>



        <div class="stat-label">用户总数</div>



      </el-card>



      <el-card class="stat-card">



        <div class="stat-value">{{ stats.newsCount ?? '-' }}</div>



        <div class="stat-label">新闻数量</div>



      </el-card>



      <el-card class="stat-card">



        <div class="stat-value">{{ stats.categoryCount ?? '-' }}</div>



        <div class="stat-label">新闻分类数</div>



      </el-card>



      <el-card class="stat-card">



        <div class="stat-value">{{ stats.forumPostCount ?? '-' }}</div>



        <div class="stat-label">论坛帖子数</div>



      </el-card>



    </div>







    <el-card class="chart-card">



      <template #header><span>数据概览</span></template>



      <div ref="chartRef" style="height: 320px"></div>



    </el-card>







    <el-card class="chart-card" style="margin-top: 16px;">



      <template #header>



        <div style="display: flex; justify-content: space-between; align-items: center;">



          <span>互动趋势（点赞/收藏/评论）</span>



          <div style="display:flex; gap:8px; align-items:center;">



            <el-radio-group v-model="trendDays" size="small" @change="loadTrend">



              <el-radio-button :label="7">7天</el-radio-button>



              <el-radio-button :label="14">14天</el-radio-button>



            </el-radio-group>



            <el-button size="small" @click="exportTrendCsv">导出CSV</el-button>



          </div>



        </div>



      </template>



      <div ref="trendChartRef" style="height: 360px"></div>



    </el-card>



  </div>



</template>







<script setup>



import { ref, onBeforeUnmount, onMounted, nextTick } from 'vue';



import * as echarts from 'echarts';



import { getDashboard, getInteractionTrend } from '../api/admin';
import { THEME_CHANGE_EVENT } from '../utils/theme';







const stats = ref({});



const chartRef = ref();



const trendChartRef = ref();



const trendDays = ref(7);



const trendData = ref({ dates: [], likes: [], collects: [], comments: [] });







let chartInstance = null;



let trendChartInstance = null;



const getCssVar = (name, fallback) => {



  if (typeof window === 'undefined') return fallback;



  const value = getComputedStyle(document.documentElement).getPropertyValue(name).trim();



  return value || fallback;



};



const getChartTheme = () => ({



  textPrimary: getCssVar('--app-text-main', '#e7eef9'),



  textSecondary: getCssVar('--app-text-sub', '#a3b2c9'),



  border: getCssVar('--app-border', '#2f435d'),



  surface: getCssVar('--app-surface-raised', '#16243c'),



  accent: getCssVar('--app-accent', '#93b0ea')



});

const refreshCharts = () => {



  if (chartInstance) {



    initChart();



  }



  if (trendChartInstance) {



    initTrendChart(trendData.value);



  }



};







const loadData = async () => {



  try {



    stats.value = await getDashboard();



    await nextTick();



    initChart();



  } catch {



    stats.value = {};



  }



};







const initChart = () => {



  if (!chartRef.value) return;



  if (!chartInstance) chartInstance = echarts.init(chartRef.value);



  const theme = getChartTheme();



  chartInstance.setOption({



    tooltip: { trigger: 'item', backgroundColor: theme.surface, borderColor: theme.border, textStyle: { color: theme.textPrimary } },



    legend: { bottom: 0, textStyle: { color: theme.textSecondary } },



    series: [{



      type: 'pie',



      radius: ['40%', '70%'],



      label: { color: theme.textPrimary },



      labelLine: { lineStyle: { color: theme.border } },



      itemStyle: { borderColor: theme.surface, borderWidth: 2 },



      data: [



        { value: stats.value.userCount || 0, name: '用户' },



        { value: stats.value.newsCount || 0, name: '新闻' },



        { value: stats.value.categoryCount || 0, name: '分类' },



        { value: stats.value.forumPostCount || 0, name: '论坛帖子' }



      ]



    }]



  });



};







const loadTrend = async () => {



  try {



    trendData.value = await getInteractionTrend(trendDays.value);



    await nextTick();



    initTrendChart(trendData.value);



  } catch {



    trendData.value = { dates: [], likes: [], collects: [], comments: [] };



    initTrendChart(trendData.value);



  }



};







const initTrendChart = (data) => {



  if (!trendChartRef.value) return;



  if (!trendChartInstance) trendChartInstance = echarts.init(trendChartRef.value);



  const theme = getChartTheme();



  trendChartInstance.setOption({



    tooltip: { trigger: 'axis', backgroundColor: theme.surface, borderColor: theme.border, textStyle: { color: theme.textPrimary } },



    legend: { top: 8, textStyle: { color: theme.textSecondary } },



    grid: { left: 24, right: 24, bottom: 24, top: 48, containLabel: true },



    xAxis: { type: 'category', data: data.dates || [], axisLabel: { color: theme.textSecondary }, axisLine: { lineStyle: { color: theme.border } } },



    yAxis: { type: 'value', minInterval: 1, axisLabel: { color: theme.textSecondary }, splitLine: { lineStyle: { color: theme.border, opacity: 0.45 } } },



    series: [



      { name: '点赞', type: 'line', smooth: true, data: data.likes || [], lineStyle: { color: '#93b0ea', width: 3 }, itemStyle: { color: '#93b0ea' } },



      { name: '收藏', type: 'line', smooth: true, data: data.collects || [], lineStyle: { color: '#59de9b', width: 3 }, itemStyle: { color: '#59de9b' } },



      { name: '评论', type: 'line', smooth: true, data: data.comments || [], lineStyle: { color: '#fbbc00', width: 3 }, itemStyle: { color: '#fbbc00' } }



    ]



  });



};







const exportTrendCsv = () => {



  const dates = trendData.value.dates || [];



  const likes = trendData.value.likes || [];



  const collects = trendData.value.collects || [];



  const comments = trendData.value.comments || [];







  const rows = [['日期', '点赞', '收藏', '评论']];



  for (let i = 0; i < dates.length; i++) {



    rows.push([dates[i], likes[i] || 0, collects[i] || 0, comments[i] || 0]);



  }







  const csv = '\uFEFF' + rows.map((r) => r.join(',')).join('\n');



  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });



  const url = URL.createObjectURL(blob);



  const a = document.createElement('a');



  a.href = url;



  a.download = `interaction-trend-${trendDays.value}d.csv`;



  a.click();



  URL.revokeObjectURL(url);



};







onMounted(async () => {



  window.addEventListener(THEME_CHANGE_EVENT, refreshCharts);



  await loadData();



  await loadTrend();



});



onBeforeUnmount(() => {



  window.removeEventListener(THEME_CHANGE_EVENT, refreshCharts);



  chartInstance?.dispose();



  trendChartInstance?.dispose();



});



</script>







<style scoped>



.admin-dashboard { max-width: 1200px; }



.stats-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }



.stat-card { text-align: center; }



.stat-value { font-size: 28px; font-weight: 600; color: var(--app-accent); }



.stat-label { margin-top: 4px; font-size: 14px; color: var(--app-text-sub); }



.chart-card { margin-top: 16px; }



</style>
