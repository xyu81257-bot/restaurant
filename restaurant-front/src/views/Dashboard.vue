// views/Dashboard.vue
<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-title">今日收入</div>
          <div class="stat-value">¥{{ statistics.todayIncome || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-title">今日订单</div>
          <div class="stat-value">{{ statistics.todayOrders || 0 }} 单</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-title">本月收入</div>
          <div class="stat-value">¥{{ statistics.monthIncome || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-title">总订单数</div>
          <div class="stat-value">{{ statistics.totalOrders || 0 }} 单</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 热销菜品 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header">
            <span>热销菜品 TOP 5</span>
          </div>
          <el-table :data="hotDish" style="width:100%">
            <el-table-column prop="dishName" label="菜品名称" />
            <el-table-column prop="totalSales" label="销量" align="center" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header">
            <span>日营业额趋势</span>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              size="small"
              style="float:right;width:240px;"
              @change="loadDailyTrend"
            />
          </div>
          <div id="trend-chart" style="height:280px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { statisticsApi } from '../api/statistics'
import * as echarts from 'echarts'

export default {
  name: 'Dashboard',
  data() {
    const now = new Date()
    const endDate = new Date(now)
    const startDate = new Date(now)
    startDate.setDate(startDate.getDate() - 7)
    return {
      statistics: {},
      hotDish: [],
      dateRange: [startDate, endDate],
      chart: null
    }
  },
  mounted() {
    this.loadStatistics()
    this.loadHotDish()
    this.loadDailyTrend()
  },
  methods: {
    async loadStatistics() {
      try {
        const res = await statisticsApi.getOverview()
        this.statistics = res.data
      } catch (error) {
        this.$message.error('加载统计数据失败')
      }
    },
    async loadHotDish() {
      try {
        const res = await statisticsApi.getHotDish(5)
        this.hotDish = res.data
      } catch (error) {
        this.$message.error('加载热销菜品失败')
      }
    },
    async loadDailyTrend() {
      if (!this.dateRange || this.dateRange.length !== 2) return
      const [start, end] = this.dateRange
      const startStr = this.formatDate(start)
      const endStr = this.formatDate(end)
      try {
        const res = await statisticsApi.getDailyTrend(startStr, endStr)
        this.renderChart(res.data)
      } catch (error) {
        this.$message.error('加载趋势数据失败')
      }
    },
    formatDate(date) {
      const d = new Date(date)
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${d.getFullYear()}-${month}-${day}`
    },
    renderChart(data) {
      const chartDom = document.getElementById('trend-chart')
      if (!chartDom) return
      if (!this.chart) {
        this.chart = echarts.init(chartDom)
      }
      const dates = data.map(item => item.date)
      const incomes = data.map(item => item.income || 0)
      this.chart.setOption({
        tooltip: {
          trigger: 'axis',
          formatter: params => {
            const p = params[0]
            return `${p.name}<br/>收入: ¥${p.value}`
          }
        },
        xAxis: {
          type: 'category',
          data: dates
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '¥{value}'
          }
        },
        series: [{
          data: incomes,
          type: 'line',
          smooth: true,
          lineStyle: {
            color: '#409EFF'
          },
          areaStyle: {
            color: 'rgba(64, 158, 255, 0.1)'
          }
        }]
      })
      window.addEventListener('resize', () => {
        this.chart && this.chart.resize()
      })
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-title {
  color: #909399;
  font-size: 14px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-top: 8px;
}

.chart-card {
  min-height: 320px;
}
</style>