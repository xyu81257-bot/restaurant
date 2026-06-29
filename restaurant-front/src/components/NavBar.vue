<!-- src/components/NavBar.vue -->
<template>
  <el-header class="navbar">
    <div class="logo" @click="goHome">🍽 美味餐厅</div>
    <div class="nav">
      <el-button type="text" @click="$router.push('/')">首页</el-button>
      <el-button type="text" @click="$router.push('/menu')">点餐</el-button>
      <el-button type="text" @click="$router.push('/cart')">
        购物车 <el-badge :value="cartCount" class="badge" />
      </el-button>
      <el-button type="text" @click="$router.push('/orders')" v-if="isLogin">我的订单</el-button>
      <el-button type="text" @click="handleLogout" v-if="isLogin">退出</el-button>
      <el-button type="primary" size="small" @click="$router.push('/login')" v-else>登录</el-button>
      <el-button type="success" size="small" @click="$router.push('/register')" v-else>注册</el-button>
    </div>
  </el-header>
</template>

<script>
export default {
  name: 'NavBar',
  data() {
    return {
      cartCount: 0
    }
  },
  computed: {
    isLogin() {
      return !!localStorage.getItem('token')
    }
  },
  mounted() {
    this.updateCartCount()
  },
  methods: {
    updateCartCount() {
      const cart = JSON.parse(localStorage.getItem('cart') || '[]')
      this.cartCount = cart.reduce((sum, item) => sum + item.count, 0)
    },
    goHome() {
      this.$router.push('/')
    },
    handleLogout() {
      localStorage.removeItem('token')
      localStorage.removeItem('cart')
      this.$message.success('已退出')
      this.$router.push('/login')
    }
  },
  watch: {
    '$route'() {
      this.updateCartCount()
    }
  }
}
</script>

<style scoped>
.navbar {
  background: #ffffff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
  height: 64px;
}

.logo {
  font-size: 24px;
  font-weight: 700;
  color: #2d3436;
  cursor: pointer;
  letter-spacing: 1px;
}

.nav {
  display: flex;
  align-items: center;
  gap: 8px;
}

.badge {
  margin-left: 4px;
}

/* 响应式 */
@media (max-width: 768px) {
  .navbar {
    padding: 0 16px;
    flex-wrap: wrap;
    height: auto;
    padding-top: 8px;
    padding-bottom: 8px;
  }
  .logo {
    font-size: 20px;
  }
  .nav {
    gap: 4px;
    flex-wrap: wrap;
  }
  .nav .el-button {
    padding: 6px 10px;
    font-size: 12px;
  }
}
</style>