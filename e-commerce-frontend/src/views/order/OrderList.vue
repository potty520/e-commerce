<template>
  <MainLayout>
    <div class="order-list-container">
      <el-card>
        <template #header>
          <h2>我的订单</h2>
        </template>
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="全部" name="" />
          <el-tab-pane label="待付款" name="0" />
          <el-tab-pane label="已付款" name="1" />
          <el-tab-pane label="已发货" name="2" />
          <el-tab-pane label="已完成" name="4" />
        </el-tabs>
        <el-table :data="orderList" v-loading="loading">
          <el-table-column label="订单信息" min-width="200">
            <template #default="{ row }">
              <div>
                <p>订单号: {{ row.orderNo }}</p>
                <p>下单时间: {{ formatDate(row.createTime) }}</p>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="totalAmount" label="金额" width="120">
            <template #default="{ row }">
              <span class="amount">¥{{ row.totalAmount }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button type="text" @click="goToDetail(row.id)">查看详情</el-button>
              <el-button v-if="row.status === 0" type="text" @click="handleCancel(row.id)">取消</el-button>
              <el-button v-if="row.status === 0" type="primary" @click="handlePay(row.id)">去支付</el-button>
              <el-button v-if="row.status === 2" type="success" @click="handleConfirm(row.id)">确认收货</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          v-model:current-page="page"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchOrderList"
          style="margin-top: 20px; justify-content: center"
        />
      </el-card>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import MainLayout from '@/components/common/MainLayout.vue'
import { getOrderList, cancelOrder, payOrder, confirmReceive } from '@/api/order'

const router = useRouter()

const orderList = ref([])
const loading = ref(false)
const activeTab = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'primary', 4: 'info', 5: 'danger', 6: 'warning', 7: 'info' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待付款', 1: '已付款', 2: '已发货', 3: '已收货', 4: '已完成', 5: '已关闭', 6: '退款中', 7: '已退款' }
  return texts[status] || '未知'
}

const handleTabChange = () => {
  page.value = 1
  fetchOrderList()
}

const goToDetail = (id) => {
  router.push(`/order/detail/${id}`)
}

const handleCancel = async (id) => {
  try {
    await ElMessageBox.confirm('确定取消此订单吗？', '提示', { type: 'warning' })
    await cancelOrder(id)
    ElMessage.success('订单已取消')
    fetchOrderList()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const handlePay = async (id) => {
  try {
    await ElMessageBox.confirm('确认支付吗？', '提示', { type: 'warning' })
    await payOrder(id, 2)
    ElMessage.success('支付成功')
    fetchOrderList()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const handleConfirm = async (id) => {
  try {
    await ElMessageBox.confirm('确认收货吗？', '提示', { type: 'warning' })
    await confirmReceive(id)
    ElMessage.success('已确认收货')
    fetchOrderList()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const fetchOrderList = async () => {
  loading.value = true
  try {
    const params = { page: page.value, pageSize: pageSize.value }
    if (activeTab.value !== '' && activeTab.value != null) {
      params.status = Number(activeTab.value)
    }
    const res = await getOrderList(params)
    orderList.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchOrderList()
})
</script>

<style scoped>
.order-list-container {
  max-width: 1000px;
  margin: 0 auto;
}

.amount {
  color: #f56c6c;
  font-weight: bold;
}
</style>
