// views/Dish.vue
<template>
  <div class="dish">
    <el-card>
      <div slot="header">
        <span>菜品管理</span>
        <el-button type="primary" size="small" style="float:right" @click="handleAdd">添加菜品</el-button>
      </div>

      <el-table :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="dishName" label="菜品名称" />
        <el-table-column prop="categoryId" label="分类ID" width="100" />
        <el-table-column prop="price" label="价格" width="120">
          <template slot-scope="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="{ row }">
            <el-button size="mini" @click="handleEdit(row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 表单弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="80px" ref="formRef">
        <el-form-item label="菜品名称" prop="dishName">
          <el-input v-model="form.dishName" />
        </el-form-item>
        <el-form-item label="分类ID" prop="categoryId">
          <el-input-number v-model="form.categoryId" :min="1" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
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
import { dishApi } from '../api/dish'

export default {
  name: 'Dish',
  data() {
    return {
      tableData: [],
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      form: {
        dishName: '',
        categoryId: 1,
        price: 0,
        stock: 0,
        status: 1
      }
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const res = await dishApi.getList()
        this.tableData = res.data || []
      } catch (error) {
        this.$message.error('加载菜品列表失败')
      }
    },
    handleAdd() {
      this.dialogTitle = '添加菜品'
      this.isEdit = false
      this.form = { dishName: '', categoryId: 1, price: 0, stock: 0, status: 1 }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑菜品'
      this.isEdit = true
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleSubmit() {
      try {
        if (this.isEdit) {
          await dishApi.update(this.form)
          this.$message.success('更新成功')
        } else {
          await dishApi.add(this.form)
          this.$message.success('添加成功')
        }
        this.dialogVisible = false
        this.loadData()
      } catch (error) {
        this.$message.error(error.message || '操作失败')
      }
    },
    async handleDelete(id) {
      try {
        await this.$confirm('确认删除该菜品吗？', '提示')
        await dishApi.delete(id)
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