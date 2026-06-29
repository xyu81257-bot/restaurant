<template>
  <div class="cart">
    <!-- 使用统一导航组件 -->
    <NavBar />

    <!-- 页面标题 -->
    <div class="page-header">
      <h2>🛒 我的购物车</h2>
      <p v-if="cartItems.length > 0">您有 {{ cartItems.length }} 件商品</p>
      <p v-else>购物车是空的</p>
    </div>

    <div class="container">
      <!-- 有商品时 -->
      <div v-if="cartItems.length > 0" class="cart-content">
        <el-card class="cart-card" shadow="never">
          <el-table :data="cartItems" border style="width:100%" class="cart-table">
            <el-table-column prop="dishName" label="菜品名称" min-width="120">
              <template slot-scope="{ row }">
                <span class="dish-name-cell">🍲 {{ row.dishName }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="price" label="单价" width="120" align="center">
              <template slot-scope="{ row }">¥{{ row.price }}</template>
            </el-table-column>
            <el-table-column label="数量" width="150" align="center">
              <template slot-scope="{ row }">
                <el-input-number
                  v-model="row.count"
                  :min="1"
                  :max="99"
                  size="small"
                  @change="updateCart"
                />
              </template>
            </el-table-column>
            <el-table-column label="小计" width="120" align="center">
              <template slot-scope="{ row }">¥{{ (row.price * row.count).toFixed(2) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template slot-scope="{ $index }">
                <el-button type="danger" size="mini" round @click="removeItem($index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 底部结算 -->
        <div class="cart-footer">
          <div class="total">
            <span class="total-label">合计</span>
            <span class="total-amount">¥{{ totalAmount }}</span>
          </div>
          <el-button type="primary" size="large" round @click="submitOrder" :loading="submitting">
            确认下单
          </el-button>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <el-empty description="购物车是空的，快去点餐吧！">
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
import { deskApi } from '../api/desk'
import { getUserId } from '../utils/auth'

export default {
  name: 'Cart',
  components: { NavBar },
  data() {
    return {
      cartItems: [],
      submitting: false,
      deskList: []
    }
  },
  computed: {
    totalAmount() {
      return this.cartItems.reduce((sum, item) => sum + item.price * item.count, 0).toFixed(2)
    }
  },
  mounted() {
    this.loadCart()
    this.loadDesks()
  },
  methods: {
    loadCart() {
      const cart = JSON.parse(localStorage.getItem('cart') || '[]')
      this.cartItems = cart
    },
    updateCart() {
      localStorage.setItem('cart', JSON.stringify(this.cartItems))
    },
    removeItem(index) {
      this.cartItems.splice(index, 1)
      this.updateCart()
    },
    async loadDesks() {
      try {
        const res = await deskApi.getList()
        this.deskList = res.data || []
      } catch (error) {
        console.error('加载桌台失败', error)
      }
    },
    async submitOrder() {
      if (this.cartItems.length === 0) {
        this.$message.warning('购物车为空')
        return
      }

      const desk = await this.selectDesk()
      if (!desk) return

      try {
        this.submitting = true
        const orderData = {
          deskId: desk.id,
          userId: getUserId() || 1,
          details: this.cartItems.map(item => ({
            dishId: item.dishId,
            count: item.count
          }))
        }
        await orderApi.create(orderData)
        this.$message.success('🎉 订单提交成功！')
        localStorage.removeItem('cart')
        this.cartItems = []
        this.$router.push('/orders')
      } catch (error) {
        this.$message.error(error.message || '下单失败')
      } finally {
        this.submitting = false
      }
    },
    selectDesk() {
      return new Promise((resolve) => {
        const available = this.deskList.filter(d => d.status !== '已占用')
        if (available.length === 0) {
          this.$message.warning('暂无可用桌台')
          resolve(null)
          return
        }
        this.$prompt('请选择桌台', '选择桌台', {
          inputType: 'select',
          inputOptions: available.reduce((map, d) => {
            map[d.id] = d.number
            return map
          }, {}),
          inputPlaceholder: '请选择桌台'
        }).then(({ value }) => {
          const desk = available.find(d => d.id === Number(value))
          resolve(desk || null)
        }).catch(() => {
          resolve(null)
        })
      })
    }
  }
}
</script>

<style scoped>
.cart {
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

.cart-card {
  border-radius: 16px;
  overflow: hidden;
  border: none;
}

.cart-table .dish-name-cell {
  font-weight: 500;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  padding: 20px 24px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
}

.total {
  display: flex;
  align-items: baseline;
  gap: 12px;
}
.total-label {
  font-size: 18px;
  color: #2d3436;
  font-weight: 500;
}
.total-amount {
  font-size: 32px;
  font-weight: 700;
  color: #e17055;
}

.cart-footer .el-button {
  padding: 14px 40px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #6c5ce7, #a29bfe);
  border: none;
}
.cart-footer .el-button:hover {
  background: linear-gradient(135deg, #5f3dc4, #845ef7);
  transform: scale(1.02);
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
  .cart-footer {
    flex-direction: column;
    gap: 16px;
    padding: 16px 20px;
  }
  .total-amount { font-size: 26px; }
  .cart-footer .el-button { width: 100%; }
  .cart-table .el-table__body-wrapper { overflow-x: auto; }
}
</style>