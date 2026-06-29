<template>
  <div class="order">
    <el-card>
      <div slot="header">
        <span>订单管理</span>
        <!-- 已移除创建订单按钮 -->
      </div>

      <!-- 搜索条件 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.orderStatus" placeholder="请选择状态" clearable>
            <el-option label="待支付" value="待支付" />
            <el-option label="已支付" value="已支付" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" border stripe style="width:100%">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="deskId" label="桌台" width="100" />
        <el-table-column prop="totalAmount" label="总金额" width="120">
          <template slot-scope="{ row }">
            ¥{{ row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="订单状态" width="100">
          <template slot-scope="{ row }">
            <el-tag :type="getStatusType(row.orderStatus)">
              {{ row.orderStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payStatus" label="支付状态" width="100">
          <template slot-scope="{ row }">
            <el-tag :type="row.payStatus === '已支付' ? 'success' : 'warning'">
              {{ row.payStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" fixed="right" width="120">
          <template slot-scope="{ row }">
            <el-button size="mini" @click="viewDetail(row.id)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        class="pagination"
        :current-page="pageNum"
        :page-sizes="[10, 20, 50]"
        :page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="onSizeChange"
        @current-change="onPageChange"
      />
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog title="订单详情" :visible.sync="detailVisible" width="600px">
      <div v-if="detailData">
        <p><strong>订单号：</strong>{{ detailData.orderNo }}</p>
        <p><strong>状态：</strong>{{ detailData.orderStatus }}</p>
        <p><strong>总金额：</strong>¥{{ detailData.totalAmount }}</p>
        <p><strong>创建时间：</strong>{{ detailData.createTime }}</p>
        <el-divider>菜品明细</el-divider>
        <el-table :data="detailData.details || []" border size="small">
          <el-table-column prop="dishId" label="菜品ID" width="80" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="price" label="单价" width="100">
            <template slot-scope="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column prop="amount" label="小计" width="100">
            <template slot-scope="{ row }">¥{{ row.amount }}</template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { orderApi } from '../api/order'

export default {
  name: 'Order',
  data() {
    return {
      searchForm: {
        orderNo: '',
        orderStatus: ''
      },
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      detailVisible: false,
      detailData: null
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      try {
        const params = {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          ...this.searchForm
        }
        const res = await orderApi.getList(params)
        this.tableData = res.data.records
        this.total = res.data.total
      } catch (error) {
        this.$message.error('加载订单列表失败')
      }
    },
    resetSearch() {
      this.searchForm = { orderNo: '', orderStatus: '' }
      this.pageNum = 1
      this.loadData()
    },
    onSizeChange(val) {
      this.pageSize = val
      this.loadData()
    },
    onPageChange(val) {
      this.pageNum = val
      this.loadData()
    },
    getStatusType(status) {
      const map = {
        '待支付': 'warning',
        '已支付': 'success',
        '已完成': 'success',
        '已取消': 'danger'
      }
      return map[status] || 'info'
    },
    async viewDetail(id) {
      try {
        const res = await orderApi.getDetail(id)
        this.detailData = res.data
        this.detailVisible = true
      } catch (error) {
        this.$message.error('加载订单详情失败')
      }
    }
  }
}
</script>

<style scoped>
.search-form {
  margin-bottom: 16px;
}

.pagination {
  margin-top: 16px;
  float: right;
}
</style>