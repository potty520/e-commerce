<template>
  <MainLayout>
    <div class="product-detail-container" v-if="product">
      <el-card>
        <el-row :gutter="40">
          <el-col :span="10">
            <div class="image-section">
              <el-image :src="product.mainImage" style="width: 100%; height: 400px" fit="cover" />
            </div>
          </el-col>
          <el-col :span="14">
            <div class="product-info">
              <h1 class="product-name">{{ product.name }}</h1>
              <p class="product-subtitle">{{ product.subtitle }}</p>
              <div class="price-section">
                <span class="current-price">¥{{ product.price }}</span>
                <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
              </div>
              <div class="info-row">
                <span class="label">销量:</span>
                <span>{{ product.sales }}</span>
              </div>
              <div class="info-row">
                <span class="label">库存:</span>
                <span>{{ product.stock }}</span>
              </div>
              <div class="quantity-section">
                <span class="label">数量:</span>
                <el-input-number v-model="quantity" :min="1" :max="product.stock" />
              </div>
              <div class="action-section">
                <el-button type="primary" size="large" @click="handleAddToCart">加入购物车</el-button>
                <el-button type="danger" size="large" @click="handleBuyNow">立即购买</el-button>
              </div>
            </div>
          </el-col>
        </el-row>
        <el-divider />
        <div class="product-description">
          <h3>商品详情</h3>
          <div v-html="product.description"></div>
        </div>
        <el-divider />
        <div class="reviews-section">
          <h3>用户评价</h3>
          <div v-if="userStore.isLoggedIn" class="review-form">
            <el-rate v-model="reviewForm.rating" show-score score-template="{value} 分" />
            <el-input v-model="reviewForm.content" type="textarea" :rows="3" maxlength="1000" show-word-limit placeholder="写几句评价（每个商品仅能评价一次）" />
            <el-button type="primary" :loading="reviewSubmitting" @click="submitReview">发表评价</el-button>
          </div>
          <p v-else class="login-hint"><router-link to="/login">登录</router-link> 后可发表评价</p>
          <el-empty v-if="!reviews.length && !reviewsLoading" description="暂无评价" />
          <div v-else class="review-list">
            <div v-for="r in reviews" :key="r.id" class="review-item">
              <div class="review-head">
                <span class="nick">{{ r.nickname || '用户' }}</span>
                <el-rate :model-value="r.rating" disabled show-score score-template="{value}" />
                <span class="time">{{ formatTime(r.createTime) }}</span>
              </div>
              <p class="review-content">{{ r.content || '此用户没有填写文字' }}</p>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import MainLayout from '@/components/common/MainLayout.vue'
import { getProductDetail, listProductReviews, createProductReview } from '@/api/product'
import { addToCart } from '@/api/cart'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const product = ref(null)
const quantity = ref(1)
const reviews = ref([])
const reviewsLoading = ref(false)
const reviewSubmitting = ref(false)
const reviewForm = reactive({ rating: 5, content: '' })

const formatTime = (t) => (t ? new Date(t).toLocaleString() : '')

const loadReviews = async () => {
  if (!route.params.id) return
  reviewsLoading.value = true
  try {
    const res = await listProductReviews(route.params.id, { page: 1, pageSize: 20 })
    reviews.value = res.data?.list || []
  } catch (e) {
    console.error(e)
  } finally {
    reviewsLoading.value = false
  }
}

const submitReview = async () => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  reviewSubmitting.value = true
  try {
    await createProductReview({
      productId: Number(route.params.id),
      rating: reviewForm.rating,
      content: reviewForm.content
    })
    ElMessage.success('评价已提交')
    reviewForm.content = ''
    reviewForm.rating = 5
    await loadReviews()
  } catch (e) {
    console.error(e)
  } finally {
    reviewSubmitting.value = false
  }
}

const handleAddToCart = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await addToCart({ productId: product.value.id, quantity: quantity.value })
    ElMessage.success('已加入购物车')
  } catch (e) {
    console.error(e)
  }
}

const handleBuyNow = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await addToCart({ productId: product.value.id, quantity: quantity.value })
    router.push('/order/confirm')
  } catch (e) {
    console.error(e)
  }
}

onMounted(async () => {
  try {
    const res = await getProductDetail(route.params.id)
    product.value = res.data
    await loadReviews()
  } catch (e) {
    console.error(e)
  }
})
</script>

<style scoped>
.product-detail-container {
  max-width: 1200px;
  margin: 0 auto;
}

.image-section {
  border: 1px solid #eee;
}

.product-info {
  padding: 20px;
}

.product-name {
  font-size: 28px;
  margin-bottom: 10px;
}

.product-subtitle {
  font-size: 16px;
  color: #666;
  margin-bottom: 20px;
}

.price-section {
  background: #f5f5f5;
  padding: 20px;
  margin-bottom: 20px;
}

.current-price {
  font-size: 36px;
  color: #f56c6c;
  font-weight: bold;
  margin-right: 20px;
}

.original-price {
  font-size: 20px;
  color: #999;
  text-decoration: line-through;
}

.info-row {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.info-row .label {
  width: 80px;
  color: #666;
}

.quantity-section {
  margin-bottom: 30px;
  display: flex;
  align-items: center;
}

.quantity-section .label {
  margin-right: 15px;
  color: #666;
}

.action-section {
  display: flex;
  gap: 20px;
}

.product-description {
  margin-top: 30px;
}

.product-description h3 {
  margin-bottom: 20px;
}

.reviews-section h3 {
  margin-bottom: 16px;
}

.review-form {
  margin-bottom: 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-width: 560px;
}

.login-hint {
  margin-bottom: 16px;
  color: #909399;
}

.review-item {
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
}

.review-head {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.review-head .nick {
  font-weight: 600;
}

.review-head .time {
  font-size: 12px;
  color: #909399;
}

.review-content {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}
</style>
