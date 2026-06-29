<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <div class="login-icon">🍽️</div>
        <h1>餐厅点餐</h1>
        <p>登录您的账号，享受美食之旅</p>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="0" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
            size="large"
            clearable
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            @click="handleLogin"
            :loading="loading"
            class="login-btn"
            round
          >
            登录
          </el-button>
        </el-form-item>

        <div class="login-footer">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { userApi } from '../api/user'
import { getUserRole } from '../utils/auth'

export default {
  name: 'Login',
  data() {
    return {
      form: {
        username: '',
        password: ''
      },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    async handleLogin() {
      try {
        await this.$refs.formRef.validate()
        this.loading = true
        const res = await userApi.login(this.form)
        localStorage.setItem('token', res.data)

        const role = getUserRole()
        if (role === 'admin') {
          this.$router.push('/admin/dashboard')
        } else {
          this.$router.push('/')
        }
        this.$message.success('🎉 登录成功')
      } catch (error) {
        this.$message.error(error.message || '登录失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #6c5ce7 0%, #a29bfe 40%, #fd79a8 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* 装饰性背景元素 */
.login-container::before {
  content: '🍕';
  position: absolute;
  font-size: 120px;
  opacity: 0.08;
  top: 10%;
  left: 5%;
  transform: rotate(-15deg);
}

.login-container::after {
  content: '🍔';
  position: absolute;
  font-size: 100px;
  opacity: 0.08;
  bottom: 10%;
  right: 5%;
  transform: rotate(15deg);
}

/* 登录卡片 */
.login-box {
  width: 420px;
  max-width: 100%;
  padding: 40px 32px 32px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  z-index: 1;
  transition: transform 0.3s;
}

.login-box:hover {
  transform: translateY(-4px);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-icon {
  font-size: 48px;
  margin-bottom: 8px;
}

.login-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #2d3436;
  margin: 0 0 4px 0;
}

.login-header p {
  color: #636e72;
  font-size: 14px;
  margin: 0;
}

.login-form .el-form-item {
  margin-bottom: 20px;
}

.login-form .el-input {
  height: 48px;
}

.login-form .el-input__inner {
  height: 48px;
  border-radius: 12px;
  border: 1.5px solid #dfe6e9;
  padding-left: 16px;
  font-size: 15px;
  transition: border-color 0.3s;
}

.login-form .el-input__inner:focus {
  border-color: #6c5ce7;
  box-shadow: 0 0 0 3px rgba(108, 92, 231, 0.15);
}

.login-form .el-input__prefix {
  left: 12px;
  font-size: 18px;
  color: #b2bec3;
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 18px;
  font-weight: 600;
  background: linear-gradient(135deg, #6c5ce7, #a29bfe);
  border: none;
  transition: all 0.3s;
  letter-spacing: 2px;
}

.login-btn:hover {
  background: linear-gradient(135deg, #5f3dc4, #845ef7);
  transform: scale(1.02);
  box-shadow: 0 8px 25px rgba(108, 92, 231, 0.4);
}

.login-footer {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #636e72;
}

.login-footer a {
  color: #6c5ce7;
  font-weight: 600;
  text-decoration: none;
  margin-left: 4px;
}

.login-footer a:hover {
  text-decoration: underline;
}

/* 响应式 */
@media (max-width: 480px) {
  .login-box {
    padding: 28px 20px;
  }
  .login-header h1 {
    font-size: 24px;
  }
  .login-form .el-input {
    height: 44px;
  }
  .login-form .el-input__inner {
    height: 44px;
  }
  .login-btn {
    height: 44px;
    font-size: 16px;
  }
}
</style>