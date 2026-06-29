// views/Desk.vue
<template>
  <div class="desk">
    <el-card>
      <div slot="header">
        <span>桌台管理</span>
        <el-button type="primary" size="small" style="float:right" @click="handleAdd">添加桌台</el-button>
      </div>

      <el-row :gutter="20">
        <el-col :span="6" v-for="item in tableData" :key="item.id" style="margin-bottom:16px">
          <el-card class="desk-card" :class="{'desk-occupied': item.status === '已占用'}">
            <div class="desk-number">{{ item.number }}</div>
            <div class="desk-status">
              <el-tag :type="item.status === '已占用' ? 'danger' : 'success'">
                {{ item.status || '空闲' }}
              </el-tag>
            </div>
            <div class="desk-actions">
              <el-button size="mini" @click="handleEdit(item)">编辑</el-button>
              <el-button size="mini" type="danger" @click="handleDelete(item.id)">删除</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="桌台号">
          <el-input v-model="form.number" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="空闲" value="空闲" />
            <el-option label="已占用" value="已占用" />
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
import { deskApi } from '../api/desk'

export default {
  name: 'Desk',
  data() {
    return {
      tableData: [],
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      form: { id: null, number: '', status: '空闲' }
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const res = await deskApi.getList()
        this.tableData = res.data || []
      } catch (error) {
        this.$message.error('加载桌台列表失败')
      }
    },
    handleAdd() {
      this.dialogTitle = '添加桌台'
      this.isEdit = false
      this.form = { id: null, number: '', status: '空闲' }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑桌台'
      this.isEdit = true
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleSubmit() {
      try {
        if (this.isEdit) {
          await deskApi.update(this.form)
          this.$message.success('更新成功')
        } else {
          await deskApi.add(this.form)
          this.$message.success('添加成功')
        }
        this.dialogVisible = false
        this.loadData()
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    async handleDelete(id) {
      try {
        await this.$confirm('确认删除该桌台吗？', '提示')
        await deskApi.delete(id)
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

<style scoped>
.desk-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.desk-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.desk-occupied {
  border-color: #f56c6c;
}

.desk-number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.desk-status {
  margin: 8px 0;
}

.desk-actions {
  margin-top: 8px;
}
</style>