<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <div class="register-icon">📝</div>
        <h1>创建账号</h1>
        <p>加入美食大家庭，尽享美味</p>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="0" class="register-form">
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
            placeholder="请设置密码（至少6位）"
            prefix-icon="el-icon-lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="el-icon-check"
            size="large"
            show-password
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            @click="handleRegister"
            :loading="loading"
            class="register-btn"
            round
          >
            立即注册
          </el-button>
        </el-form-item>

        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login">去登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { userApi } from '../api/user'

export default {
  name: 'Register',
  data() {
    const validateConfirm = (rule, value, callback) => {
      if (value !== this.form.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }
    return {
      form: {
        username: '',
        password: '',
        confirmPassword: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码至少6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirm, trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    async handleRegister() {
      try {
        await this.$refs.formRef.validate()
        this.loading = true
        await userApi.register({
          username: this.form.username,
          password: this.form.password
        })
        this.$message.success('🎉 注册成功，请登录')
        this.$router.push('/login')
      } catch (error) {
        this.$message.error(error.message || '注册失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #00b894 0%, #00cec9 40%, #6c5ce7 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* 装饰性背景元素 */
.register-container::before {
  content: '🍣';
  position: absolute;
  font-size: 120px;
  opacity: 0.08;
  top: 10%;
  left: 5%;
  transform: rotate(-15deg);
}

.register-container::after {
  content: '🍰';
  position: absolute;
  font-size: 100px;
  opacity: 0.08;
  bottom: 10%;
  right: 5%;
  transform: rotate(15deg);
}

/* 注册卡片 */
.register-box {
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

.register-box:hover {
  transform: translateY(-4px);
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.register-icon {
  font-size: 48px;
  margin-bottom: 8px;
}

.register-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #2d3436;
  margin: 0 0 4px 0;
}

.register-header p {
  color: #636e72;
  font-size: 14px;
  margin: 0;
}

.register-form .el-form-item {
  margin-bottom: 20px;
}

.register-form .el-input {
  height: 48px;
}

.register-form .el-input__inner {
  height: 48px;
  border-radius: 12px;
  border: 1.5px solid #dfe6e9;
  padding-left: 16px;
  font-size: 15px;
  transition: border-color 0.3s;
}

.register-form .el-input__inner:focus {
  border-color: #6c5ce7;
  box-shadow: 0 0 0 3px rgba(108, 92, 231, 0.15);
}

.register-form .el-input__prefix {
  left: 12px;
  font-size: 18px;
  color: #b2bec3;
}

.register-btn {
  width: 100%;
  height: 48px;
  font-size: 18px;
  font-weight: 600;
  background: linear-gradient(135deg, #00b894, #00cec9);
  border: none;
  transition: all 0.3s;
  letter-spacing: 2px;
}

.register-btn:hover {
  background: linear-gradient(135deg, #00a381, #00b894);
  transform: scale(1.02);
  box-shadow: 0 8px 25px rgba(0, 184, 148, 0.4);
}

.register-footer {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #636e72;
}

.register-footer a {
  color: #00b894;
  font-weight: 600;
  text-decoration: none;
  margin-left: 4px;
}

.register-footer a:hover {
  text-decoration: underline;
}

/* 响应式 */
@media (max-width: 480px) {
  .register-box {
    padding: 28px 20px;
  }
  .register-header h1 {
    font-size: 24px;
  }
  .register-form .el-input {
    height: 44px;
  }
  .register-form .el-input__inner {
    height: 44px;
  }
  .register-btn {
    height: 44px;
    font-size: 16px;
  }
}
</style>