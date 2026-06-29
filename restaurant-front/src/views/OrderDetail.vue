<!-- src/views/OrderDetail.vue -->
<template>
  <div class="order-detail">
    <el-header class="header">
      <div class="logo" @click="$router.push('/')">🍽 美味餐厅</div>
      <div class="nav">
        <el-button type="text" @click="$router.push('/orders')">返回订单列表</el-button>
        <el-button type="text" @click="handleLogout">退出</el-button>
      </div>
    </el-header>

    <div class="container" v-if="order">
      <el-card>
        <h3>订单详情</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(order.orderStatus)">{{ order.orderStatus }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="支付状态">{{ order.payStatus }}</el-descriptions-item>
          <el-descriptions-item label="总金额">¥{{ order.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ order.createTime }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ order.payTime || '-' }}</el-descriptions-item>
        </el-descriptions>

        <h4 style="margin-top:20px;">菜品明细</h4>
        <el-table :data="order.details || []" border>
          <el-table-column prop="dishId" label="菜品ID" />
          <el-table-column prop="quantity" label="数量" />
          <el-table-column prop="price" label="单价" width="120">
            <template slot-scope="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column prop="amount" label="小计" width="120">
            <template slot-scope="{ row }">¥{{ row.amount }}</template>
          </el-table-column>
        </el-table>

        <div style="margin-top:20px;">
          <el-button
            type="success"
            v-if="order.orderStatus === '待支付'"
            @click="handlePay(order.id)"
          >
            支付
          </el-button>
          <el-button
            type="danger"
            v-if="order.orderStatus === '待支付'"
            @click="handleCancel(order.id)"
          >
            取消订单
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { orderApi } from '../api/order'

export default {
  name: 'OrderDetail',
  data() {
    return {
      order: null
    }
  },
  mounted() {
    this.loadDetail()
  },
  methods: {
    async loadDetail() {
      try {
        const id = this.$route.params.id
        const res = await orderApi.getDetail(id)
        this.order = res.data
      } catch (error) {
        this.$message.error('加载订单详情失败')
        this.$router.push('/orders')
      }
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
        await this.$confirm('确认支付该订单吗？', '提示')
        await orderApi.pay(id)
        this.$message.success('支付成功')
        this.loadDetail()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '支付失败')
        }
      }
    },
    async handleCancel(id) {
      try {
        await this.$confirm('确认取消该订单吗？', '提示')
        await orderApi.cancel(id)
        this.$message.success('已取消')
        this.loadDetail()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(error.message || '取消失败')
        }
      }
    },
    handleLogout() {
      localStorage.removeItem('token')
      localStorage.removeItem('cart')
      this.$message.success('已退出')
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.order-detail {
  min-height: 100vh;
  background: #f5f7fa;
}

.header {
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.logo {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  cursor: pointer;
}

.nav {
  display: flex;
  align-items: center;
  gap: 10px;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}
</style>