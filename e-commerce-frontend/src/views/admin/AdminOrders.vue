<template>
  <el-card>
    <template #header><h2>订单管理</h2></template>
    <el-form :inline="true" class="filter">
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable placeholder="全部" style="width: 120px">
          <el-option label="待付款" :value="0" />
          <el-option label="已付款" :value="1" />
          <el-option label="已发货" :value="2" />
          <el-option label="退款中" :value="6" />
          <el-option label="已退款" :value="7" />
        </el-select>
      </el-form-item>
      <el-form-item label="用户ID">
        <el-input v-model.number="query.userId" clearable placeholder="可选" style="width: 140px" />
      </el-form-item>
      <el-form-item label="订单号">
        <el-input v-model="query.orderNo" clearable placeholder="模糊" style="width: 180px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="load">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="orderNo" label="订单号" min-width="160" />
      <el-table-column prop="userId" label="用户" width="80" />
      <el-table-column prop="totalAmount" label="金额" width="100" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">{{ statusText(row.status) }}</template>
      </el-table-column>
      <el-table-column prop="adminRemark" label="备注" min-width="120" show-overflow-tooltip />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openRemark(row)">备注</el-button>
          <el-button v-if="row.status === 0 || row.status === 1" type="danger" link @click="openClose(row)">关闭</el-button>
          <el-button v-if="row.status === 1" type="primary" link @click="openShip(row)">发货</el-button>
          <el-button v-if="row.status === 6" type="warning" link @click="approveRefund(row.id)">同意退款</el-button>
          <el-button v-if="row.status === 6" type="danger" link @click="openReject(row)">拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="query.page"
      :page-size="query.pageSize"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="load"
      class="pager"
    />

    <el-dialog v-model="shipVisible" title="发货" width="420px" @closed="shipForm.logisticsCompany = ''; shipForm.logisticsNo = ''">
      <el-form :model="shipForm" label-width="100px">
        <el-form-item label="物流公司" required>
          <el-input v-model="shipForm.logisticsCompany" placeholder="如：顺丰速运" />
        </el-form-item>
        <el-form-item label="运单号" required>
          <el-input v-model="shipForm.logisticsNo" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipVisible = false">取消</el-button>
        <el-button type="primary" @click="submitShip">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="remarkVisible" title="管理员备注" width="480px" @closed="remarkText = ''">
      <el-input v-model="remarkText" type="textarea" :rows="4" maxlength="500" show-word-limit placeholder="写入或覆盖备注" />
      <template #footer>
        <el-button @click="remarkVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRemark">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="closeVisible" title="关闭订单" width="440px" @closed="closeReason = ''">
      <p class="dlg-tip">仅「待付款」「已付款且未发货」可关闭；将回滚库存。</p>
      <el-input v-model="closeReason" type="textarea" :rows="3" maxlength="500" show-word-limit placeholder="关闭说明（写入备注）" />
      <template #footer>
        <el-button @click="closeVisible = false">取消</el-button>
        <el-button type="danger" @click="submitClose">确认关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="rejectVisible" title="拒绝退款" width="440px" @closed="rejectReason = ''">
      <el-input v-model="rejectReason" type="textarea" :rows="4" maxlength="500" show-word-limit placeholder="拒绝原因（必填）" />
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReject">提交</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  adminOrderList,
  adminShipOrder,
  adminApproveRefund,
  adminRejectRefund,
  adminOrderRemark,
  adminCloseOrder
} from '@/api/admin'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ status: null, userId: null, orderNo: '', page: 1, pageSize: 10 })

const shipVisible = ref(false)
const shipTargetId = ref(null)
const shipForm = reactive({ logisticsCompany: '', logisticsNo: '' })

const remarkVisible = ref(false)
const remarkTargetId = ref(null)
const remarkText = ref('')

const closeVisible = ref(false)
const closeTargetId = ref(null)
const closeReason = ref('')

const rejectVisible = ref(false)
const rejectTargetId = ref(null)
const rejectReason = ref('')

const statusText = (s) =>
  ({ 0: '待付款', 1: '已付款', 2: '已发货', 3: '已收货', 4: '已完成', 5: '已关闭', 6: '退款中', 7: '已退款' }[s] || s)

const load = async () => {
  loading.value = true
  try {
    const params = { page: query.page, pageSize: query.pageSize }
    if (query.status !== null && query.status !== '') params.status = query.status
    if (query.userId) params.userId = query.userId
    if (query.orderNo) params.orderNo = query.orderNo
    const res = await adminOrderList(params)
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const openShip = (row) => {
  shipTargetId.value = row.id
  shipVisible.value = true
}

const submitShip = async () => {
  if (!shipForm.logisticsCompany || !shipForm.logisticsNo) {
    ElMessage.warning('请填写物流信息')
    return
  }
  try {
    await adminShipOrder(shipTargetId.value, { ...shipForm })
    ElMessage.success('已发货')
    shipVisible.value = false
    load()
  } catch (e) {
    console.error(e)
  }
}

const approveRefund = async (id) => {
  try {
    await ElMessageBox.confirm('确认同意退款？', '提示', { type: 'warning' })
    await adminApproveRefund(id)
    ElMessage.success('已标记为已退款')
    load()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const openRemark = (row) => {
  remarkTargetId.value = row.id
  remarkText.value = row.adminRemark || ''
  remarkVisible.value = true
}

const submitRemark = async () => {
  try {
    await adminOrderRemark(remarkTargetId.value, { adminRemark: remarkText.value })
    ElMessage.success('已保存')
    remarkVisible.value = false
    load()
  } catch (e) {
    console.error(e)
  }
}

const openClose = (row) => {
  closeTargetId.value = row.id
  closeReason.value = ''
  closeVisible.value = true
}

const submitClose = async () => {
  try {
    await adminCloseOrder(closeTargetId.value, { reason: closeReason.value || undefined })
    ElMessage.success('订单已关闭')
    closeVisible.value = false
    load()
  } catch (e) {
    console.error(e)
  }
}

const openReject = (row) => {
  rejectTargetId.value = row.id
  rejectReason.value = ''
  rejectVisible.value = true
}

const submitReject = async () => {
  if (!rejectReason.value?.trim()) {
    ElMessage.warning('请填写拒绝原因')
    return
  }
  try {
    await adminRejectRefund(rejectTargetId.value, { reason: rejectReason.value.trim() })
    ElMessage.success('已拒绝退款')
    rejectVisible.value = false
    load()
  } catch (e) {
    console.error(e)
  }
}

onMounted(load)
</script>

<style scoped>
.filter {
  margin-bottom: 12px;
}
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
.dlg-tip {
  font-size: 13px;
  color: #909399;
  margin: 0 0 12px;
}
</style>
