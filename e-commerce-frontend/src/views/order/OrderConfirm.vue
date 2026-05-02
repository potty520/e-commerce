<template>
  <MainLayout>
    <div class="order-confirm-container">
      <el-card>
        <template #header>
          <h2>确认订单</h2>
        </template>
        <div class="address-section" v-if="defaultAddress">
          <h3>收货地址</h3>
          <el-radio-group v-model="selectedAddressId">
            <el-radio :label="defaultAddress.id">
              {{ defaultAddress.receiverName }} {{ defaultAddress.receiverPhone }}
              {{ defaultAddress.province }} {{ defaultAddress.city }} {{ defaultAddress.district }} {{ defaultAddress.detailAddress }}
              <el-tag v-if="defaultAddress.isDefault === 1" size="small">默认</el-tag>
            </el-radio>
          </el-radio-group>
          <el-button type="text" @click="$router.push('/user/address')">管理收货地址</el-button>
        </div>
        <el-divider />
        <div class="products-section">
          <h3>商品清单</h3>
          <el-table :data="selectedItems">
            <el-table-column label="商品" min-width="300">
              <template #default="{ row }">
                <div class="product-cell">
                  <img :src="row.product?.mainImage" class="product-image" />
                  <span>{{ row.product?.name }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="单价" width="120">
              <template #default="{ row }">
                ¥{{ row.product?.price }}
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="120" />
            <el-table-column label="小计" width="120">
              <template #default="{ row }">
                <span class="subtotal">¥{{ ((row.product?.price || 0) * row.quantity).toFixed(2) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <el-divider />
        <div class="pay-section">
          <h3>支付方式</h3>
          <el-radio-group v-model="payType">
            <el-radio :label="1">微信支付</el-radio>
            <el-radio :label="2">支付宝</el-radio>
            <el-radio :label="3">银行卡</el-radio>
          </el-radio-group>
        </div>
        <el-divider />
        <div class="order-summary">
          <div class="summary-row">
            <span>商品金额:</span>
            <span>¥{{ subtotal }}</span>
          </div>
          <div class="summary-row">
            <span>运费:</span>
            <span>¥0.00</span>
          </div>
          <div class="summary-row total">
            <span>应付总额:</span>
            <span class="total-amount">¥{{ subtotal }}</span>
          </div>
        </div>
        <div class="action-section">
          <el-button size="large" @click="$router.push('/cart')">返回购物车</el-button>
          <el-button type="primary" size="large" :loading="loading" @click="handleSubmit">提交订单</el-button>
        </div>
      </el-card>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import MainLayout from '@/components/common/MainLayout.vue'
import { getCartList } from '@/api/cart'
import { getDefaultAddress } from '@/api/user'
import { createOrder } from '@/api/order'

const router = useRouter()

const cartList = ref([])
const defaultAddress = ref(null)
const selectedAddressId = ref(null)
const payType = ref(1)
const loading = ref(false)

const selectedItems = computed(() => {
  return cartList.value.filter(item => item.selected === 1)
})

const subtotal = computed(() => {
  return selectedItems.value
    .reduce((sum, item) => sum + (item.product?.price || 0) * item.quantity, 0)
    .toFixed(2)
})

const handleSubmit = async () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择商品')
    return
  }
  if (!defaultAddress.value) {
    ElMessage.warning('请添加收货地址')
    return
  }
  loading.value = true
  try {
    const items = selectedItems.value.map(item => ({
      productId: item.productId,
      quantity: item.quantity
    }))
    const res = await createOrder({
      addressId: selectedAddressId.value || defaultAddress.value.id,
      payType: payType.value,
      items
    })
    ElMessage.success('订单提交成功')
    router.push(`/order/detail/${res.data.orderId}`)
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    const [cartRes, addressRes] = await Promise.all([
      getCartList(),
      getDefaultAddress()
    ])
    cartList.value = cartRes.data || []
    defaultAddress.value = addressRes.data
    selectedAddressId.value = defaultAddress.value?.id
  } catch (e) {
    console.error(e)
  }
})
</script>

<style scoped>
.order-confirm-container {
  max-width: 900px;
  margin: 0 auto;
}

.address-section {
  padding: 20px 0;
}

.address-section h3 {
  margin-bottom: 15px;
}

.products-section h3 {
  margin-bottom: 15px;
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

.subtotal {
  color: #f56c6c;
  font-weight: bold;
}

.pay-section {
  padding: 20px 0;
}

.pay-section h3 {
  margin-bottom: 15px;
}

.order-summary {
  padding: 20px;
  background: #f5f5f5;
  margin-bottom: 20px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.summary-row.total {
  font-size: 18px;
  font-weight: bold;
  padding-top: 10px;
  border-top: 1px solid #ddd;
}

.total-amount {
  color: #f56c6c;
  font-size: 24px;
}

.action-section {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
}
</style>
