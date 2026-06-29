// views/User.vue
<template>
  <div class="user">
    <el-card>
      <div slot="header">
        <span>用户管理</span>
        <el-button type="primary" size="small" style="float:right" @click="handleAdd">添加用户</el-button>
      </div>

      <el-table :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="role" label="角色" width="120" />
        <el-table-column label="操作" width="200">
          <template slot-scope="{ row }">
            <el-button size="mini" @click="handleEdit(row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="400px">
      <el-form :model="form" label-width="80px" ref="formRef" :rules="rules">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="form.password" type="password" placeholder="至少6位" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option label="管理员" value="admin" />
            <el-option label="员工" value="employee" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { userApi } from '../api/user'

export default {
  name: 'User',
  data() {
    return {
      tableData: [],
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      form: { id: null, username: '', password: '', role: 'employee' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, min: 6, message: '密码至少6位', trigger: 'blur' }],
        role: [{ required: true, message: '请选择角色', trigger: 'change' }]
      }
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const res = await userApi.getList()
        this.tableData = res.data || []
      } catch (error) {
        this.$message.error('加载用户列表失败')
      }
    },
    handleAdd() {
      this.dialogTitle = '添加用户'
      this.isEdit = false
      this.form = { id: null, username: '', password: '', role: 'employee' }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑用户'
      this.isEdit = true
      this.form = { ...row, password: '' }
      this.dialogVisible = true
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
        if (this.isEdit) {
          const { username, role } = this.form
          await userApi.update({ id: this.form.id, username, role })
          this.$message.success('更新成功')
        } else {
          await userApi.add(this.form)
          this.$message.success('添加成功')
        }
        this.dialogVisible = false
        this.loadData()
      } catch (error) {
        if (typeof error === 'string' && error.includes('required')) {
          // 表单验证错误
        } else {
          this.$message.error(error.message || '操作失败')
        }
      }
    },
    async handleDelete(id) {
      try {
        await this.$confirm('确认删除该用户吗？', '提示')
        await userApi.delete(id)
        this.$message.success('删除成功')
        this.loadData()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    }
  }
}
</script>