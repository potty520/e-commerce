<template>
  <MainLayout>
    <div class="cart-container">
      <el-card>
        <template #header>
          <div class="card-header">
            <h2>购物车</h2>
            <el-button v-if="cartList.length > 0" type="text" @click="handleSelectAll">
              {{ isAllSelected ? '取消全选' : '全选' }}
            </el-button>
          </div>
        </template>
        <el-table :data="cartList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" />
          <el-table-column label="商品" min-width="300">
            <template #default="{ row }">
              <div class="product-cell">
                <img :src="row.product?.mainImage" class="product-image" />
                <div class="product-info">
                  <router-link :to="`/product/detail/${row.productId}`" class="product-name">
                    {{ row.product?.name }}
                  </router-link>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="单价" width="120">
            <template #default="{ row }">
              ¥{{ row.product?.price }}
            </template>
          </el-table-column>
          <el-table-column label="数量" width="180">
            <template #default="{ row }">
              <el-input-number
                v-model="row.quantity"
                :min="1"
                :max="row.product?.stock"
                @change="handleQuantityChange(row.id, row.quantity)"
              />
            </template>
          </el-table-column>
          <el-table-column label="小计" width="120">
            <template #default="{ row }">
              <span class="subtotal">¥{{ calculateSubtotal(row) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="text" @click="handleRemove(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="cart-footer" v-if="cartList.length > 0">
          <div class="total-section">
            <span class="total-label">合计 ({{ selectedCount }} 件商品):</span>
            <span class="total-amount">¥{{ totalAmount }}</span>
          </div>
          <el-button type="primary" size="large" :disabled="selectedCount === 0" @click="handleCheckout">
            去结算
          </el-button>
        </div>
        <el-empty v-else description="购物车是空的">
          <el-button type="primary" @click="$router.push('/product/list')">去购物</el-button>
        </el-empty>
      </el-card>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import MainLayout from '@/components/common/MainLayout.vue'
import { getCartList, updateCartQuantity, removeFromCart, selectAllCart, selectCartItem } from '@/api/cart'

const router = useRouter()

const cartList = ref([])
const selectedItems = ref([])

const isAllSelected = computed(() => {
  return cartList.value.length > 0 && cartList.value.every(item => item.selected === 1)
})

const selectedCount = computed(() => {
  return cartList.value.filter(item => item.selected === 1).length
})

const totalAmount = computed(() => {
  return cartList.value
    .filter(item => item.selected === 1)
    .reduce((sum, item) => {
      return sum + (item.product?.price || 0) * item.quantity
    }, 0)
    .toFixed(2)
})

const calculateSubtotal = (row) => {
  return ((row.product?.price || 0) * row.quantity).toFixed(2)
}

const handleSelectionChange = async (selection) => {
  for (const item of cartList.value) {
    const isSelected = selection.includes(item)
    if ((item.selected === 1) !== isSelected) {
      try {
        await selectCartItem(item.id, isSelected ? 1 : 0)
      } catch (e) {
        console.error(e)
      }
    }
  }
  fetchCartList()
}

const handleSelectAll = async () => {
  try {
    await selectAllCart(isAllSelected.value ? 0 : 1)
    fetchCartList()
  } catch (e) {
    console.error(e)
  }
}

const handleQuantityChange = async (id, quantity) => {
  try {
    await updateCartQuantity(id, quantity)
  } catch (e) {
    console.error(e)
    fetchCartList()
  }
}

const handleRemove = async (id) => {
  try {
    await ElMessageBox.confirm('确定从购物车移除此商品吗？', '提示', { type: 'warning' })
    await removeFromCart(id)
    ElMessage.success('已移除')
    fetchCartList()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const handleCheckout = () => {
  router.push('/order/confirm')
}

const fetchCartList = async () => {
  try {
    const res = await getCartList()
    cartList.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchCartList()
})
</script>

<style scoped>
.cart-container {
  max-width: 1000px;
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

.product-cell {
  display: flex;
  align-items: center;
  gap: 15px;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
}

.product-name {
  font-size: 14px;
  color: #333;
}

.product-name:hover {
  color: #409eff;
}

.subtotal {
  color: #f56c6c;
  font-weight: bold;
}

.cart-footer {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f5f5f5;
}

.total-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.total-label {
  font-size: 16px;
}

.total-amount {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}
</style>
