<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="sidebar">
      <div class="logo">🍽 餐厅管理</div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#1f2d3d"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/admin/dashboard">
          <i class="el-icon-s-home"></i>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/admin/order">
          <i class="el-icon-s-order"></i>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/dish">
          <i class="el-icon-food"></i>
          <span>菜品管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/category">
          <i class="el-icon-menu"></i>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/desk">
          <i class="el-icon-s-home"></i>
          <span>桌台管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/inventory">
          <i class="el-icon-s-data"></i>
          <span>库存管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/user" v-if="isAdmin">
          <i class="el-icon-user"></i>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span>{{ currentTitle }}</span>
        </div>
        <div class="header-right">
          <span style="margin-right:16px;color:#606266;">{{ username }}</span>
          <el-button type="text" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { getUserRole } from '../utils/auth'

export default {
  name: 'Layout',
  data() {
    return {
      username: '管理员'
    }
  },
  computed: {
    activeMenu() {
      return this.$route.path
    },
    currentTitle() {
      return this.$route.meta.title || ''
    },
    isAdmin() {
      return getUserRole() === 'admin'
    }
  },
  mounted() {
    const token = localStorage.getItem('token')
    if (token) {
      try {
        const payload = token.split('.')[1]
        const decoded = JSON.parse(atob(payload))
        this.username = decoded.username || '管理员'
      } catch (e) {
        console.error('解析token失败', e)
      }
    }
  },
  methods: {
    // 按图片要求修改退出逻辑，跳转前台首页 /，不再跳登录页
    handleLogout() {
      localStorage.removeItem('token')
      this.$message.success('已退出')
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.sidebar {
  background-color: #1f2d3d;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #2d3a4a;
}

.header {
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid #e6e6e6;
}

.header-left {
  font-size: 16px;
  font-weight: 500;
}

.el-main {
  background: #f5f7fa;
  padding: 20px;
}
</style>