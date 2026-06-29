// src/router/index.js
import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '../views/Layout.vue'

Vue.use(VueRouter)

// 公共路由（无需登录的只有登录页和注册页）
const publicRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { title: '注册' }
  }
]

// 顾客路由（需登录）
const customerRoutes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { title: '首页', requiresAuth: true }
  },
  {
    path: '/menu',
    name: 'MenuList',
    component: () => import('../views/MenuList.vue'),
    meta: { title: '菜品列表', requiresAuth: true }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('../views/Cart.vue'),
    meta: { title: '购物车', requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'MyOrders',
    component: () => import('../views/MyOrders.vue'),
    meta: { title: '我的订单', requiresAuth: true }
  },
  {
    path: '/order/detail/:id',
    name: 'OrderDetail',
    component: () => import('../views/OrderDetail.vue'),
    meta: { title: '订单详情', requiresAuth: true }
  }
]

// 管理后台路由（无需登录，直接访问）
const adminRoutes = [
  {
    path: '/admin',
    component: Layout,
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'order',
        name: 'AdminOrder',
        component: () => import('../views/Order.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'order/create',
        name: 'AdminOrderCreate',
        component: () => import('../views/OrderCreate.vue'),
        meta: { title: '创建订单' }
      },
      {
        path: 'dish',
        name: 'AdminDish',
        component: () => import('../views/Dish.vue'),
        meta: { title: '菜品管理' }
      },
      {
        path: 'category',
        name: 'AdminCategory',
        component: () => import('../views/Category.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'desk',
        name: 'AdminDesk',
        component: () => import('../views/Desk.vue'),
        meta: { title: '桌台管理' }
      },
      {
        path: 'inventory',
        name: 'AdminInventory',
        component: () => import('../views/Inventory.vue'),
        meta: { title: '库存管理' }
      },
      {
        path: 'user',
        name: 'AdminUser',
        component: () => import('../views/User.vue'),
        meta: { title: '用户管理' }
      }
    ]
  }
]

const routes = [...publicRoutes, ...customerRoutes, ...adminRoutes]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  // 已登录用户访问登录/注册页 → 跳转到首页
  if (token && (to.path === '/login' || to.path === '/register')) {
    next('/')
    return
  }

  // 需要登录的页面 → 未登录则跳转登录
  if (to.meta.requiresAuth) {
    if (!token) {
      next('/login')
      return
    }
  }

  // 管理后台路由直接放行（无需登录）
  next()
})

export default router