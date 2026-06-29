// views/Inventory.vue
<template>
  <div class="inventory">
    <el-card>
      <div slot="header">
        <span>库存管理</span>
        <el-button type="primary" size="small" style="float:right" @click="handleAdd">添加库存</el-button>
      </div>

      <el-table :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="dishId" label="菜品ID" width="120" />
        <el-table-column prop="quantity" label="库存数量" width="120" />
        <el-table-column prop="warningLine" label="预警线" width="120" />
        <el-table-column prop="updateTime" label="更新时间" />
        <el-table-column label="状态" width="100">
          <template slot-scope="{ row }">
            <el-tag :type="row.quantity <= row.warningLine ? 'danger' : 'success'">
              {{ row.quantity <= row.warningLine ? '库存不足' : '正常' }}
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

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="菜品ID">
          <el-input-number v-model="form.dishId" :min="1" />
        </el-form-item>
        <el-form-item label="库存数量">
          <el-input-number v-model="form.quantity" :min="0" />
        </el-form-item>
        <el-form-item label="预警线">
          <el-input-number v-model="form.warningLine" :min="0" />
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
import { inventoryApi } from '../api/inventory'

export default {
  name: 'Inventory',
  data() {
    return {
      tableData: [],
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      form: { id: null, dishId: 1, quantity: 0, warningLine: 10 }
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const res = await inventoryApi.getList()
        this.tableData = res.data || []
      } catch (error) {
        this.$message.error('加载库存列表失败')
      }
    },
    handleAdd() {
      this.dialogTitle = '添加库存'
      this.isEdit = false
      this.form = { id: null, dishId: 1, quantity: 0, warningLine: 10 }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑库存'
      this.isEdit = true
      this.form = { ...row }
      this.dialogVisible = true
    },
    async handleSubmit() {
      try {
        if (this.isEdit) {
          await inventoryApi.update(this.form)
          this.$message.success('更新成功')
        } else {
          await inventoryApi.add(this.form)
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
        await this.$confirm('确认删除该库存记录吗？', '提示')
        await inventoryApi.delete(id)
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