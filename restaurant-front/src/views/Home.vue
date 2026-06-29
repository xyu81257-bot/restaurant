<template>
  <div class="home">
    <!-- 使用统一导航组件 -->
    <NavBar />

    <!-- 美化后的 Banner -->
    <div class="banner">
      <div class="banner-content">
        <h1>欢迎来到美味餐厅</h1>
        <p>精选食材 · 匠心烹饪 · 每一道菜都是艺术品</p>
        <el-button type="primary" size="large" round @click="$router.push('/menu')">开始点餐</el-button>
      </div>
      <div class="banner-decoration">
        <span class="deco-emoji">🍕</span>
        <span class="deco-emoji">🍔</span>
        <span class="deco-emoji">🍣</span>
      </div>
    </div>

    <!-- 推荐菜品区域 -->
    <div class="section">
      <div class="section-header">
        <h2 class="section-title">🔥 今日推荐</h2>
        <span class="section-subtitle">主厨精选，不容错过</span>
      </div>

      <el-row :gutter="24" class="dish-grid">
        <el-col :xs="12" :sm="12" :md="6" v-for="item in recommendDishes" :key="item.id" class="dish-col">
          <el-card class="dish-card" shadow="hover" @click.native="viewDish(item.id)">
            <div class="dish-image" :style="{ background: getColor(item.id) }">
              <span class="dish-emoji">🍲</span>
            </div>
            <div class="dish-info">
              <div class="dish-name">{{ item.dishName }}</div>
              <div class="dish-price">¥{{ item.price }}</div>
            </div>
            <el-button size="small" type="primary" round class="add-btn" @click.stop="addToCart(item)">
              加入购物车
            </el-button>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="footer-deco">
      <span>❤️ 用心做好每一餐</span>
    </div>
  </div>
</template>

<script>
import NavBar from '../components/NavBar.vue'
import { dishApi } from '../api/dish'

export default {
  name: 'Home',
  components: { NavBar },
  data() {
    return {
      recommendDishes: [],
      cartCount: 0
    }
  },
  mounted() {
    this.loadRecommendDishes()
  },
  methods: {
    async loadRecommendDishes() {
      try {
        const res = await dishApi.getList()
        this.recommendDishes = (res.data || []).slice(0, 4)
      } catch (error) {
        console.error('加载推荐菜品失败', error)
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
    viewDish(id) {
      this.$router.push(`/menu?dishId=${id}`)
    },
    getColor(id) {
      const colors = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#F9CA24']
      return colors[id % colors.length] || '#FF9FF3'
    }
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: #f8f9fa;
}

.banner {
  background: linear-gradient(135deg, #6c5ce7 0%, #a29bfe 50%, #fd79a8 100%);
  padding: 60px 20px;
  text-align: center;
  color: #fff;
  position: relative;
  overflow: hidden;
  min-height: 280px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.banner-content h1 {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 12px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.banner-content p {
  font-size: 18px;
  opacity: 0.95;
  margin-bottom: 24px;
  letter-spacing: 2px;
}

.banner-content .el-button {
  background: #fff;
  color: #6c5ce7;
  border: none;
  font-weight: 600;
  padding: 12px 40px;
  font-size: 16px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  transition: all 0.3s;
}
.banner-content .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 25px rgba(0, 0, 0, 0.3);
}

.banner-decoration {
  position: absolute;
  right: 30px;
  top: 30px;
  font-size: 40px;
  opacity: 0.3;
  display: flex;
  gap: 15px;
  transform: rotate(-5deg);
}
.banner-decoration .deco-emoji {
  animation: float 3s ease-in-out infinite;
}
.banner-decoration .deco-emoji:nth-child(2) {
  animation-delay: 0.5s;
}
.banner-decoration .deco-emoji:nth-child(3) {
  animation-delay: 1s;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.section-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.section-title {
  font-size: 28px;
  font-weight: 700;
  color: #2d3436;
  margin: 0;
}

.section-subtitle {
  color: #636e72;
  font-size: 16px;
  font-weight: 300;
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
  cursor: pointer;
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
.add-btn:hover {
  background: linear-gradient(135deg, #5f3dc4, #845ef7);
  transform: scale(1.02);
}

.footer-deco {
  text-align: center;
  padding: 30px 0 20px;
  color: #b2bec3;
  font-size: 14px;
  letter-spacing: 1px;
  border-top: 1px solid #dfe6e9;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .banner-content h1 { font-size: 28px; }
  .banner-content p { font-size: 16px; }
  .banner-decoration { display: none; }
  .section-title { font-size: 22px; }
  .dish-name { font-size: 16px; }
  .dish-price { font-size: 20px; }
}
</style>