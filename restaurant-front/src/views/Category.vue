// views/Category.vue
<template>
  <div class="category">
    <el-card>
      <div slot="header">
        <span>分类管理</span>
        <el-button type="primary" size="small" style="float:right" @click="handleAdd">添加分类</el-button>
      </div>

      <el-table :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" />
        <el-table-column label="操作" width="200">
          <template slot-scope="{ row }">
            <el-button size="mini" @click="handleEdit(row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="分类名称">
          <el-input v-model="form.name" />
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
import { categoryApi } from '../api/category'

export default {
  name: 'Category',
  data() {
    return {
      tableData: [],
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      form: { id: null, name: '' }
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const res = await categoryApi.getList()
        this.tableData = res.data || []
      } catch (error) {
        this.$message.error('加载分类列表失败')
      }
    },
    handleAdd() {
      this.dialogTitle = '添加分类'
      this.isEdit = false
      this.form = { id: null, name: '' }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑分类'
      this.isEdit = true
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleSubmit() {
      try {
        if (this.isEdit) {
          await categoryApi.update(this.form)
          this.$message.success('更新成功')
        } else {
          await categoryApi.add(this.form)
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
        await this.$confirm('确认删除该分类吗？', '提示')
        await categoryApi.delete(id)
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