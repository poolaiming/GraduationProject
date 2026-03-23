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



import { ref, onMounted, nextTick } from 'vue';



import * as echarts from 'echarts';



import { getDashboard, getInteractionTrend } from '../api/admin';







const stats = ref({});



const chartRef = ref();



const trendChartRef = ref();



const trendDays = ref(7);



const trendData = ref({ dates: [], likes: [], collects: [], comments: [] });







let chartInstance = null;



let trendChartInstance = null;







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



  chartInstance.setOption({



    tooltip: { trigger: 'item' },



    legend: { bottom: 0 },



    series: [{



      type: 'pie',



      radius: ['40%', '70%'],



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



  trendChartInstance.setOption({



    tooltip: { trigger: 'axis' },



    legend: { top: 8 },



    grid: { left: 24, right: 24, bottom: 24, top: 48, containLabel: true },



    xAxis: { type: 'category', data: data.dates || [] },



    yAxis: { type: 'value', minInterval: 1 },



    series: [



      { name: '点赞', type: 'line', smooth: true, data: data.likes || [] },



      { name: '收藏', type: 'line', smooth: true, data: data.collects || [] },



      { name: '评论', type: 'line', smooth: true, data: data.comments || [] }



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



  await loadData();



  await loadTrend();



});



</script>







<style scoped>



.admin-dashboard { max-width: 1200px; }



.stats-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }



.stat-card { text-align: center; }



.stat-value { font-size: 28px; font-weight: 600; color: #001b44; }



.stat-label { margin-top: 4px; font-size: 14px; color: #747781; }



.chart-card { margin-top: 16px; }



</style>
