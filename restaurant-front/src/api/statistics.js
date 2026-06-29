// api/statistics.js
import api from './index'

export const statisticsApi = {
  getOverview() {
    return api.get('/api/statistics/overview')
  },
  getHotDish(topN = 5) {
    return api.get('/api/statistics/hot-dish', { params: { topN } })
  },
  getDailyTrend(startDate, endDate) {
    return api.get('/api/statistics/daily-trend', { params: { startDate, endDate } })
  }
}