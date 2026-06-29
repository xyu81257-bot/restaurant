// views/OrderCreate.vue
<template>
  <div class="order-create">
    <el-card>
      <div slot="header">
        <span>创建订单</span>
        <el-button size="small" @click="$router.back()" style="float:right">返回</el-button>
      </div>

      <el-form :model="form" label-width="80px">
        <el-form-item label="桌台">
          <el-select v-model="form.deskId" placeholder="请选择桌台">
            <el-option
              v-for="item in deskList"
              :key="item.id"
              :label="item.number"
              :value="item.id"
              :disabled="item.status === '已占用'"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="菜品">
          <div class="dish-select">
            <el-select v-model="selectedDishId" placeholder="选择菜品" size="small" style="width:200px">
              <el-option
                v-for="item in dishList"
                :key="item.id"
                :label="`${item.dishName} (¥${item.price})`"
                :value="item.id"
                :disabled="item.status !== 1"
              />
            </el-select>
            <el-input-number
              v-model="addCount"
              :min="1"
              :max="99"
              size="small"
              style="width:100px;margin-left:10px"
            />
            <el-button type="primary" size="small" @click="addDish">添加</el-button>
          </div>
        </el-form-item>

        <el-table :data="form.details" border size="small" style="margin-top:16px">
          <el-table-column prop="dishName" label="菜品名称" />
          <el-table-column prop="price" label="单价" width="100">
            <template slot-scope="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column prop="count" label="数量" width="100" />
          <el-table-column prop="amount" label="小计" width="120">
            <template slot-scope="{ row }">¥{{ row.amount }}</template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template slot-scope="{ $index }">
              <el-button type="danger" size="mini" @click="removeDish($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="total-amount">
          合计：<span class="amount">¥{{ totalAmount }}</span>
        </div>

        <el-form-item style="margin-top:20px">
          <el-button type="primary" @click="handleSubmit" :loading="submitting">确认下单</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { deskApi } from '../api/desk'
import { dishApi } from '../api/dish'
import { orderApi } from '../api/order'

export default {
  name: 'OrderCreate',
  data() {
    return {
      form: {
        deskId: null,
        userId: 1,
        details: []
      },
      deskList: [],
      dishList: [],
      selectedDishId: null,
      addCount: 1,
      submitting: false
    }
  },
  computed: {
    totalAmount() {
      let total = 0
      this.form.details.forEach(item => {
        total += item.amount
      })
      return total.toFixed(2)
    }
  },
  mounted() {
    this.loadDesks()
    this.loadDishes()
  },
  methods: {
    async loadDesks() {
      try {
        const res = await deskApi.getList()
        this.deskList = res.data || []
      } catch (error) {
        this.$message.error('加载桌台失败')
      }
    },
    async loadDishes() {
      try {
        const res = await dishApi.getList()
        this.dishList = res.data || []
      } catch (error) {
        this.$message.error('加载菜品失败')
      }
    },
    addDish() {
      if (!this.selectedDishId) {
        this.$message.warning('请选择菜品')
        return
      }
      const dish = this.dishList.find(d => d.id === this.selectedDishId)
      if (!dish) return

      // 检查是否已添加
      const exist = this.form.details.find(d => d.dishId === dish.id)
      if (exist) {
        exist.count += this.addCount
        exist.amount = exist.count * exist.price
      } else {
        this.form.details.push({
          dishId: dish.id,
          dishName: dish.dishName,
          price: dish.price,
          count: this.addCount,
          amount: dish.price * this.addCount
        })
      }
      this.selectedDishId = null
      this.addCount = 1
    },
    removeDish(index) {
      this.form.details.splice(index, 1)
    },
    async handleSubmit() {
      if (!this.form.deskId) {
        this.$message.warning('请选择桌台')
        return
      }
      if (this.form.details.length === 0) {
        this.$message.warning('请添加菜品')
        return
      }
      try {
        this.submitting = true
        const orderData = {
          deskId: this.form.deskId,
          userId: 1,
          details: this.form.details.map(d => ({
            dishId: d.dishId,
            count: d.count
          }))
        }
        const res = await orderApi.create(orderData)
        this.$message.success('订单创建成功')
        this.$router.push('/order')
      } catch (error) {
        this.$message.error(error.message || '创建订单失败')
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.dish-select {
  display: flex;
  align-items: center;
}

.total-amount {
  text-align: right;
  font-size: 18px;
  margin-top: 16px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

.total-amount .amount {
  color: #f56c6c;
  font-weight: bold;
  font-size: 24px;
}
</style>