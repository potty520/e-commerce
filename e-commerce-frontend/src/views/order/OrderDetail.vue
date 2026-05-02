<template>
  <MainLayout>
    <div class="order-detail-container" v-if="order">
      <el-card>
        <template #header>
          <div class="card-header">
            <h2>订单详情</h2>
            <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
          </div>
        </template>
        <div class="order-info">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="下单时间">{{ formatDate(order.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="支付方式">{{ getPayTypeText(order.payType) }}</el-descriptions-item>
            <el-descriptions-item label="订单金额">
              <span class="amount">¥{{ order.totalAmount }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="支付时间" v-if="order.payTime">{{ formatDate(order.payTime) }}</el-descriptions-item>
            <el-descriptions-item label="发货时间" v-if="order.deliverTime">{{ formatDate(order.deliverTime) }}</el-descriptions-item>
            <el-descriptions-item label="收货时间" v-if="order.receiveTime">{{ formatDate(order.receiveTime) }}</el-descriptions-item>
            <el-descriptions-item label="物流公司" v-if="order.logisticsCompany">{{ order.logisticsCompany }}</el-descriptions-item>
            <el-descriptions-item label="运单号" v-if="order.logisticsNo">{{ order.logisticsNo }}</el-descriptions-item>
            <el-descriptions-item label="支付流水" v-if="order.payTradeNo">{{ order.payTradeNo }}</el-descriptions-item>
            <el-descriptions-item label="退款原因" v-if="order.refundReason" :span="2">{{ order.refundReason }}</el-descriptions-item>
            <el-descriptions-item label="拒绝退款说明" v-if="order.refundRejectReason" :span="2">{{ order.refundRejectReason }}</el-descriptions-item>
            <el-descriptions-item label="管理员备注" v-if="order.adminRemark" :span="2">{{ order.adminRemark }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <el-divider content-position="left">支付网关（预留）</el-divider>
        <div class="gateway-section">
          <p class="section-tip">与「沙箱支付回调」并列：以下为正式对接微信/支付宝前的占位接口，便于联调数据结构。</p>
          <div class="gateway-actions">
            <template v-if="order.status === 0">
              <el-radio-group v-model="payChannel" size="small">
                <el-radio-button label="WECHAT_JSAPI">微信 JSAPI</el-radio-button>
                <el-radio-button label="ALIPAY_APP">支付宝 APP</el-radio-button>
              </el-radio-group>
              <el-button type="primary" plain size="small" :loading="prepayLoading" @click="handleGatewayPrepay">
                获取预支付参数
              </el-button>
            </template>
            <el-button size="small" :loading="loadingPaymentStatus" @click="fetchPaymentStatus">刷新支付状态</el-button>
          </div>
          <el-alert v-if="order.status !== 0" type="info" show-icon :closable="false" class="gateway-alert">
            订单已非待付款，仍可查看渠道占位状态（数据由本地订单推导）。
          </el-alert>
          <el-descriptions v-if="paymentStatusView" border size="small" :column="1" class="status-desc">
            <el-descriptions-item label="占位说明">{{ paymentStatusView.hint }}</el-descriptions-item>
            <el-descriptions-item label="交易状态">{{ paymentStatusView.tradeState }}</el-descriptions-item>
            <el-descriptions-item label="第三方单号">{{ paymentStatusView.thirdPartyTradeNo || '—' }}</el-descriptions-item>
            <el-descriptions-item label="stub">{{ paymentStatusView.stub }}</el-descriptions-item>
          </el-descriptions>
          <div v-if="prepayResult" class="prepay-block">
            <div class="prepay-title">预支付返回（stub={{ prepayResult.stub }}）</div>
            <pre class="json-block">{{ formatJson(prepayResult) }}</pre>
          </div>
        </div>

        <el-divider content-position="left">物流跟踪</el-divider>
        <div class="logistics-section">
          <div class="logistics-actions">
            <el-button size="small" :loading="loadingLogistics" @click="fetchLogistics">刷新物流信息</el-button>
            <el-tag v-if="logisticsTrack?.stub" type="info" size="small">预留接口</el-tag>
          </div>
          <p v-if="order.status < 2 && !order.logisticsNo" class="section-tip">商家发货并填写运单号后，可在此查看轨迹（对接三方物流后展示节点）。</p>
          <template v-if="logisticsTrack">
            <el-descriptions border size="small" :column="2" class="logistics-desc">
              <el-descriptions-item label="承运商">{{ logisticsTrack.logisticsCompany || '—' }}</el-descriptions-item>
              <el-descriptions-item label="运单号">{{ logisticsTrack.logisticsNo || '—' }}</el-descriptions-item>
            </el-descriptions>
            <el-timeline v-if="logisticsTrack.traces && logisticsTrack.traces.length" class="trace-timeline">
              <el-timeline-item
                v-for="(t, idx) in logisticsTrack.traces"
                :key="idx"
                :timestamp="t.time"
                placement="top"
              >
                <p class="trace-status">{{ t.status }}</p>
                <p class="trace-desc">{{ t.description }}</p>
              </el-timeline-item>
            </el-timeline>
            <el-empty v-else description="暂无轨迹节点（后端占位返回空列表）" :image-size="80" />
          </template>
          <p v-else class="section-tip">点击「刷新物流信息」加载承运商与运单号概要。</p>
        </div>

        <el-divider />
        <div class="products-section">
          <h3>商品清单</h3>
          <el-table :data="order.orderItems">
            <el-table-column label="商品" min-width="300">
              <template #default="{ row }">
                <div class="product-cell">
                  <img :src="row.productImage" class="product-image" />
                  <span>{{ row.productName }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="unitPrice" label="单价" width="120">
              <template #default="{ row }">
                ¥{{ row.unitPrice }}
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="120" />
            <el-table-column label="小计" width="120">
              <template #default="{ row }">
                <span class="subtotal">¥{{ row.totalPrice }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <el-divider />
        <div class="action-section">
          <el-button @click="$router.push('/order/list')">返回列表</el-button>
          <el-button v-if="order.status === 0" type="primary" @click="handlePay(order.id)">立即支付</el-button>
          <el-button v-if="order.status === 0" type="warning" @click="handleMockPay">沙箱支付（回调）</el-button>
          <el-button v-if="order.status === 0" type="success" plain @click="handleUnifiedPay">统一下单支付（回调）</el-button>
          <el-button v-if="order.status === 0" @click="handleCancel(order.id)">取消订单</el-button>
          <el-button v-if="[1, 2, 3].includes(order.status)" type="danger" plain @click="handleRefundApply">申请退款</el-button>
          <el-button v-if="order.status === 2" type="success" @click="handleConfirm(order.id)">确认收货</el-button>
        </div>
      </el-card>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import MainLayout from '@/components/common/MainLayout.vue'
import { getOrderDetail, cancelOrder, payOrder, confirmReceive, getMockPayData, getUnifiedPayData, applyRefund } from '@/api/order'
import { mockPayCallback, unifiedPayCallback } from '@/api/pay'
import { paymentPrepay, getPaymentStatus } from '@/api/payment'
import { getLogisticsByOrder } from '@/api/logistics'

const router = useRouter()
const route = useRoute()

const order = ref(null)
const payChannel = ref('WECHAT_JSAPI')
const prepayLoading = ref(false)
const prepayResult = ref(null)
const paymentStatusView = ref(null)
const loadingPaymentStatus = ref(false)
const logisticsTrack = ref(null)
const loadingLogistics = ref(false)

const formatJson = (obj) => JSON.stringify(obj, null, 2)

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

const getPayTypeText = (payType) => {
  const texts = { 1: '微信支付', 2: '支付宝', 3: '银行卡' }
  return texts[payType] || '未支付'
}

const handleCancel = async (id) => {
  try {
    await ElMessageBox.confirm('确定取消此订单吗？', '提示', { type: 'warning' })
    await cancelOrder(id)
    ElMessage.success('订单已取消')
    fetchOrderDetail()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const handlePay = async (id) => {
  try {
    await ElMessageBox.confirm('确认支付吗？', '提示', { type: 'warning' })
    await payOrder(id, 2)
    ElMessage.success('支付成功')
    fetchOrderDetail()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const handleConfirm = async (id) => {
  try {
    await ElMessageBox.confirm('确认收货吗？', '提示', { type: 'warning' })
    await confirmReceive(id)
    ElMessage.success('已确认收货')
    fetchOrderDetail()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const handleGatewayPrepay = async () => {
  prepayLoading.value = true
  try {
    const res = await paymentPrepay({
      orderId: Number(route.params.id),
      channel: payChannel.value
    })
    prepayResult.value = res.data
    ElMessage.success(res.data?.hint ? '已返回占位数据' : '请求成功')
  } catch (e) {
    console.error(e)
  } finally {
    prepayLoading.value = false
  }
}

const fetchPaymentStatus = async () => {
  loadingPaymentStatus.value = true
  try {
    const res = await getPaymentStatus(Number(route.params.id))
    paymentStatusView.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loadingPaymentStatus.value = false
  }
}

const fetchLogistics = async () => {
  loadingLogistics.value = true
  try {
    const res = await getLogisticsByOrder(Number(route.params.id))
    logisticsTrack.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loadingLogistics.value = false
  }
}

const handleMockPay = async () => {
  try {
    const id = Number(route.params.id)
    const dataRes = await getMockPayData(id)
    const p = dataRes.data
    await mockPayCallback({
      orderNo: p.orderNo,
      payTradeNo: p.payTradeNo,
      payType: p.payType || 2,
      sign: p.sign
    })
    ElMessage.success('沙箱回调成功，订单已支付')
    fetchOrderDetail()
  } catch (e) {
    console.error(e)
  }
}

const handleUnifiedPay = async () => {
  try {
    const id = Number(route.params.id)
    const dataRes = await getUnifiedPayData(id)
    const p = dataRes.data
    await unifiedPayCallback({
      orderNo: p.orderNo,
      payTradeNo: p.payTradeNo,
      payType: p.payType || 2,
      sign: p.sign
    })
    ElMessage.success('统一下单回调成功，订单已支付')
    fetchOrderDetail()
  } catch (e) {
    console.error(e)
  }
}

const handleRefundApply = async () => {
  try {
    const { value } = await ElMessageBox.prompt('请填写退款原因', '申请退款', {
      confirmButtonText: '提交',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '原因不能为空'
    })
    await applyRefund(Number(route.params.id), { reason: value })
    ElMessage.success('已提交退款申请')
    fetchOrderDetail()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const fetchOrderDetail = async () => {
  try {
    const res = await getOrderDetail(route.params.id)
    order.value = res.data
    prepayResult.value = null
    if (order.value?.status >= 2 || order.value?.logisticsNo) {
      await fetchLogistics()
    } else {
      logisticsTrack.value = null
    }
    await fetchPaymentStatus()
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchOrderDetail()
})
</script>

<style scoped>
.order-detail-container {
  max-width: 900px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
}

.amount, .subtotal {
  color: #f56c6c;
  font-weight: bold;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 15px;
}

.product-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
}

.action-section {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
}

.section-tip {
  font-size: 13px;
  color: #909399;
  margin: 0 0 12px;
  line-height: 1.5;
}

.gateway-section,
.logistics-section {
  margin-bottom: 8px;
}

.gateway-actions,
.logistics-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.gateway-alert {
  margin-bottom: 12px;
}

.status-desc,
.logistics-desc {
  margin-top: 8px;
  max-width: 100%;
}

.prepay-block {
  margin-top: 12px;
}

.prepay-title {
  font-size: 13px;
  color: #606266;
  margin-bottom: 6px;
}

.json-block {
  margin: 0;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 6px;
  font-size: 12px;
  line-height: 1.45;
  overflow: auto;
  max-height: 220px;
}

.trace-timeline {
  margin-top: 16px;
  padding-left: 4px;
}

.trace-status {
  margin: 0 0 4px;
  font-weight: 600;
  color: #303133;
}

.trace-desc {
  margin: 0;
  font-size: 13px;
  color: #606266;
}
</style>
