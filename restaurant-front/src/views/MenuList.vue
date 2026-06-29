<template>
  <div class="menu-list">
    <!-- 使用统一导航组件 -->
    <NavBar />

    <!-- 页面标题 -->
    <div class="page-header">
      <h2>📋 精选菜单</h2>
      <p>用心烹饪每一道佳肴</p>
    </div>

    <div class="container">
      <!-- 分类筛选 -->
      <div class="filters">
        <el-button
          v-for="cat in categories"
          :key="cat.id"
          :type="activeCategory === cat.id ? 'primary' : 'default'"
          size="medium"
          round
          @click="activeCategory = cat.id; loadDishes()"
          class="filter-btn"
        >
          {{ cat.name }}
        </el-button>
        <el-button
          :type="activeCategory === null ? 'primary' : 'default'"
          size="medium"
          round
          @click="activeCategory = null; loadDishes()"
          class="filter-btn"
        >
          🍽 全部
        </el-button>
      </div>

      <!-- 菜品列表 -->
      <el-row :gutter="24" class="dish-grid">
        <el-col :xs="12" :sm="12" :md="6" v-for="item in dishList" :key="item.id" class="dish-col">
          <el-card class="dish-card" shadow="hover">
            <div class="dish-image" :style="{ background: getColor(item.id) }">
              <span class="dish-emoji">🍲</span>
              <div class="dish-status-badge">
                <el-tag :type="item.status === 1 ? 'success' : 'danger'" size="mini" effect="dark">
                  {{ item.status === 1 ? '在售' : '已下架' }}
                </el-tag>
              </div>
            </div>
            <div class="dish-info">
              <div class="dish-name">{{ item.dishName }}</div>
              <div class="dish-price">¥{{ item.price }}</div>
            </div>
            <el-button
              size="small"
              type="primary"
              round
              class="add-btn"
              :disabled="item.status !== 1"
              @click="addToCart(item)"
            >
              {{ item.status === 1 ? '加入购物车' : '已下架' }}
            </el-button>
          </el-card>
        </el-col>
      </el-row>

      <el-empty v-if="dishList.length === 0" description="暂无菜品，敬请期待" />
    </div>
  </div>
</template>

<script>
import NavBar from '../components/NavBar.vue'
import { dishApi } from '../api/dish'
import { categoryApi } from '../api/category'

export default {
  name: 'MenuList',
  components: { NavBar },
  data() {
    return {
      dishList: [],
      categories: [],
      activeCategory: null,
      cartCount: 0
    }
  },
  mounted() {
    this.loadCategories()
    this.loadDishes()
  },
  methods: {
    async loadCategories() {
      try {
        const res = await categoryApi.getList()
        this.categories = res.data || []
      } catch (error) {
        console.error('加载分类失败', error)
      }
    },
    async loadDishes() {
      try {
        const res = await dishApi.getList()
        let list = res.data || []
        if (this.activeCategory) {
          list = list.filter(d => d.categoryId === this.activeCategory)
        }
        this.dishList = list
      } catch (error) {
        this.$message.error('加载菜品失败')
      }
    },
    addToCart(dish) {
      const token = localStorage.getItem('token')
      if (!token) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      let cart = JSON.parse(localStorage.getItem('cart') || '[]')
      const exist = cart.find(item => item.dishId === dish.id)
      if (exist) {
        exist.count += 1
      } else {
        cart.push({
          dishId: dish.id,
          dishName: dish.dishName,
          price: dish.price,
          count: 1
        })
      }
      localStorage.setItem('cart', JSON.stringify(cart))
      this.$message.success('已加入购物车')
    },
    getColor(id) {
      const colors = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#F9CA24', '#A29BFE', '#FD79A8', '#00B894', '#E17055']
      return colors[id % colors.length] || '#FF9FF3'
    }
  }
}
</script>

<style scoped>
.menu-list {
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
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 20px;
}

.filters {
  margin-bottom: 28px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.filter-btn {
  border-radius: 30px;
  padding: 8px 20px;
  font-weight: 500;
  transition: all 0.3s;
}
.filter-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.10);
}
.filter-btn.el-button--primary {
  background: linear-gradient(135deg, #6c5ce7, #a29bfe);
  border: none;
}

.dish-grid {
  margin-top: 10px;
}
.dish-col {
  margin-bottom: 24px;
}

.dish-card {
  border-radius: 16px;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  background: #ffffff;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 16px 24px;
}
.dish-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.10);
}

.dish-image {
  width: 100%;
  height: 140px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  position: relative;
  transition: transform 0.3s;
}
.dish-card:hover .dish-image {
  transform: scale(1.02);
}
.dish-emoji {
  font-size: 52px;
  line-height: 1;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.1));
}
.dish-status-badge {
  position: absolute;
  top: 10px;
  right: 10px;
}

.dish-info {
  width: 100%;
  text-align: center;
  margin-bottom: 12px;
}
.dish-name {
  font-size: 18px;
  font-weight: 600;
  color: #2d3436;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.dish-price {
  font-size: 22px;
  font-weight: 700;
  color: #e17055;
  margin-top: 4px;
}

.add-btn {
  width: 100%;
  border-radius: 30px;
  font-weight: 500;
  background: linear-gradient(135deg, #6c5ce7, #a29bfe);
  border: none;
  transition: all 0.3s;
}
.add-btn:not(.is-disabled):hover {
  background: linear-gradient(135deg, #5f3dc4, #845ef7);
  transform: scale(1.02);
}
.add-btn.is-disabled {
  background: #dfe6e9;
  color: #b2bec3;
}

@media (max-width: 768px) {
  .page-header h2 { font-size: 22px; }
  .filters { gap: 6px; }
  .filter-btn { padding: 6px 14px; font-size: 12px; }
  .dish-name { font-size: 16px; }
  .dish-price { font-size: 20px; }
}
</style>