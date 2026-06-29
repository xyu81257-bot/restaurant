<template>
  <div class="my-orders">
    <!-- 使用统一导航组件 -->
    <NavBar />

    <!-- 页面标题 -->
    <div class="page-header">
      <h2>📋 我的订单</h2>
      <p>查看您的所有订单记录</p>
    </div>

    <div class="container">
      <!-- 状态筛选 Tabs -->
      <el-tabs v-model="activeStatus" @tab-click="loadOrders" class="status-tabs">
        <el-tab-pane label="📊 全部" name="" />
        <el-tab-pane label="⏳ 待支付" name="待支付" />
        <el-tab-pane label="✅ 已支付" name="已支付" />
        <el-tab-pane label="🎉 已完成" name="已完成" />
        <el-tab-pane label="❌ 已取消" name="已取消" />
      </el-tabs>

      <!-- 订单列表 -->
      <div v-if="orderList.length > 0">
        <div v-for="order in orderList" :key="order.id" class="order-item">
          <el-card class="order-card" shadow="hover">
            <div class="order-header">
              <div class="order-left">
                <span class="order-no">📄 {{ order.orderNo }}</span>
                <span class="order-time">{{ order.createTime }}</span>
              </div>
              <el-tag :type="getStatusType(order.orderStatus)" size="medium" effect="dark">
                {{ order.orderStatus }}
              </el-tag>
            </div>

            <div class="order-body">
              <div v-for="detail in order.details" :key="detail.id" class="order-detail">
                <span class="detail-name">🍲 {{ detail.dishName || '菜品ID:' + detail.dishId }}</span>
                <span class="detail-qty">×{{ detail.quantity }}</span>
                <span class="detail-amount">¥{{ detail.amount }}</span>
              </div>
            </div>

            <div class="order-footer">
              <span class="total-amount">合计：¥{{ order.totalAmount }}</span>
              <div class="order-actions">
                <el-button
                  size="small"
                  type="success"
                  round
                  v-if="order.orderStatus === '待支付'"
                  @click="handlePay(order.id)"
                >
                  立即支付
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  round
                  plain
                  v-if="order.orderStatus === '待支付'"
                  @click="handleCancel(order.id)"
                >
                  取消订单
                </el-button>
                <el-button size="small" round @click="viewDetail(order.id)">
                  查看详情
                </el-button>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 分页 -->
        <el-pagination
          class="pagination"
          :current-page="pageNum"
          :page-sizes="[5, 10, 20]"
          :page-size="pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="onSizeChange"
          @current-change="onPageChange"
          background
        />
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <el-empty description="暂无订单，去点餐吧！">
          <el-button type="primary" round size="large" @click="$router.push('/menu')">
            🍽 去点餐
          </el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script>
import NavBar from '../components/NavBar.vue'
import { orderApi } from '../api/order'

export default {
  name: 'MyOrders',
  components: { NavBar },
  data() {
    return {
      orderList: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      activeStatus: ''
    }
  },
  mounted() {
    this.loadOrders()
  },
  methods: {
    async loadOrders() {
      try {
        const res = await orderApi.getList({
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          orderStatus: this.activeStatus || undefined
        })
        this.orderList = res.data.records || []
        this.total = res.data.total || 0
      } catch (error) {
        this.$message.error('加载订单失败')
      }
    },
    onSizeChange(val) {
      this.pageSize = val
      this.loadOrders()
    },
    onPageChange(val) {
      this.pageNum = val
      this.loadOrders()
    },
    getStatusType(status) {
      const map = {
        '待支付': 'warning',
        '已支付': 'success',
        '已完成': 'success',
        '已取消': 'danger'
      }
      return map[status] || 'info'
    },
    async handlePay(id) {
      try {
        await this.$confirm('确认支付该订单吗？', '提示', { type: 'info' })
        await orderApi.pay(id)
        this.$message.success('🎉 支付成功')
        this.loadOrders()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '支付失败')
        }
      }
    },
    async handleCancel(id) {
      try {
        await this.$confirm('确认取消该订单吗？', '提示', { type: 'warning' })
        await orderApi.cancel(id)
        this.$message.success('已取消订单')
        this.loadOrders()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '取消失败')
        }
      }
    },
    viewDetail(id) {
      this.$router.push(`/order/detail/${id}`)
    }
  }
}
</script>

<style scoped>
.my-orders {
  min-height: 100vh;
  background: #f8f9fa;
}

.page-header {
  background: linear-gradient(135deg, #6c5ce7 0%, #a29bfe 50%, #fd79a8 100%);
  padding: 30px 20px;
  text-align: center;
  color: #fff;
}

.page-header h2 {
  font-size: 28px;
  font-weight: 700;
  margin: 0;
}

.page-header p {
  font-size: 16px;
  opacity: 0.9;
  margin-top: 6px;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px 20px;
}

.status-tabs .el-tabs__item {
  font-size: 15px;
  font-weight: 500;
}
.status-tabs .el-tabs__item.is-active {
  color: #6c5ce7;
}
.status-tabs .el-tabs__active-bar {
  background: #6c5ce7;
}

.order-item {
  margin-bottom: 20px;
}

.order-card {
  border-radius: 16px;
  overflow: hidden;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  transition: transform 0.3s, box-shadow 0.3s;
}
.order-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}
.order-left {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.order-no {
  font-weight: 600;
  color: #2d3436;
  font-size: 15px;
}
.order-time {
  font-size: 13px;
  color: #b2bec3;
}

.order-body {
  margin: 8px 0;
}
.order-detail {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  color: #636e72;
  border-bottom: 1px dashed #f5f5f5;
}
.order-detail:last-child {
  border-bottom: none;
}
.detail-name {
  flex: 1;
  font-weight: 500;
}
.detail-qty {
  margin: 0 16px;
  color: #b2bec3;
}
.detail-amount {
  font-weight: 500;
  color: #2d3436;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}
.total-amount {
  font-size: 18px;
  font-weight: 700;
  color: #e17055;
}
.order-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.order-actions .el-button--success {
  background: linear-gradient(135deg, #00b894, #00cec9);
  border: none;
}
.order-actions .el-button--success:hover {
  background: linear-gradient(135deg, #00a381, #00b894);
}

.pagination {
  margin-top: 20px;
  text-align: center;
}

.empty-state {
  padding: 60px 0;
}
.empty-state .el-button {
  background: linear-gradient(135deg, #6c5ce7, #a29bfe);
  border: none;
  padding: 14px 40px;
  font-size: 16px;
}
.empty-state .el-button:hover {
  background: linear-gradient(135deg, #5f3dc4, #845ef7);
  transform: scale(1.02);
}

@media (max-width: 768px) {
  .page-header h2 { font-size: 22px; }
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  .order-footer {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  .order-actions { justify-content: center; }
  .order-actions .el-button { flex: 1; min-width: 80px; }
  .status-tabs .el-tabs__item { font-size: 13px; padding: 0 12px; }
}
</style>