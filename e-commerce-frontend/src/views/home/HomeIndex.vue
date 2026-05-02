<template>
  <MainLayout>
    <div class="home-container">
      <div class="banner">
        <el-carousel
          class="banner-carousel"
          height="400px"
          indicator-position="outside"
          :interval="4500"
          :autoplay="true"
          pause-on-hover
          arrow="hover"
          trigger="click"
        >
          <el-carousel-item v-for="(slide, i) in bannerSlides" :key="i">
            <div class="banner-slide">
              <img :src="slide.image" class="banner-img" :alt="slide.title" loading="eager" />
              <div class="banner-mask" />
              <div class="banner-caption">
                <h2>{{ slide.title }}</h2>
                <p>{{ slide.subtitle }}</p>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
      <div class="categories-section">
        <h2>商品分类</h2>
        <el-row :gutter="20">
          <el-col v-for="cat in displayCategories" :key="cat.id" :span="4">
            <el-card shadow="hover" class="category-card" @click="goToProductList(cat.id)">
              <div class="category-item">
                <div class="category-thumb">
                  <img :src="cat.image" :alt="cat.name" />
                </div>
                <span class="category-name">{{ cat.name }}</span>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      <div class="products-section">
        <h2>精选商品</h2>
        <el-row :gutter="20">
          <el-col v-for="product in productList" :key="product.id" :span="6">
            <el-card shadow="hover" @click="goToDetail(product.id)">
              <img :src="product.mainImage" class="product-image" />
              <div class="product-info">
                <h3 class="product-name">{{ product.name }}</h3>
                <p class="product-subtitle">{{ product.subtitle }}</p>
                <div class="product-price">
                  <span class="current-price">¥{{ product.price }}</span>
                  <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import MainLayout from '@/components/common/MainLayout.vue'
import { getCategoryList, getProductList } from '@/api/product'
import { mergeCategoriesForMenu } from '@/utils/categoryDisplay'

const router = useRouter()

const categoryList = ref([])
const productList = ref([])

const base = import.meta.env.BASE_URL || '/'

/** 轮播大图：使用 public/carousel 本地图片，自动切换 + 指示器/箭头 */
const bannerSlides = [
  {
    image: `${base}carousel/banner-1.jpg`,
    title: '欢迎来到电商平台',
    subtitle: '优质商品 放心之选'
  },
  {
    image: `${base}carousel/banner-2.jpg`,
    title: '每日上新 限时特惠',
    subtitle: '精选好物 超值直降'
  },
  {
    image: `${base}carousel/banner-3.jpg`,
    title: '品质生活 安心到家',
    subtitle: '正品保障 极速发货'
  }
]

/** 中文名 → 固定配图（picsum id，稳定不随机） */
const CATEGORY_ZH_IMAGE_ID = {
  数码家电: 180,
  服饰鞋包: 325,
  图书文娱: 24,
  家居生活: 366,
  运动户外: 82,
  食品生鲜: 312
}

function categoryCoverUrl(zhName, fallbackId) {
  const pid = CATEGORY_ZH_IMAGE_ID[zhName] || (Number(fallbackId) % 80) + 20
  return `https://picsum.photos/id/${pid}/400/280`
}

/** 与商品列表侧栏一致：中文展示 + 去重 */
const displayCategories = computed(() =>
  mergeCategoriesForMenu(categoryList.value).map((c) => ({
    id: c.id,
    name: c.displayName,
    image: categoryCoverUrl(c.displayName, c.id)
  }))
)

const goToProductList = (categoryId) => {
  router.push({ path: '/product/list', query: { categoryId } })
}

const goToDetail = (id) => {
  router.push(`/product/detail/${id}`)
}

onMounted(async () => {
  const [catOutcome, prodOutcome] = await Promise.allSettled([
    getCategoryList(),
    getProductList({ page: 1, pageSize: 8 })
  ])
  if (catOutcome.status === 'fulfilled') {
    categoryList.value = catOutcome.value.data || []
  } else {
    console.error(catOutcome.reason)
  }
  if (prodOutcome.status === 'fulfilled') {
    productList.value = prodOutcome.value.data || []
  } else {
    console.error(prodOutcome.reason)
  }
})
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
}

.banner {
  margin-bottom: 40px;
}

.banner-carousel :deep(.el-carousel__container) {
  border-radius: 8px;
  overflow: hidden;
}

.banner-carousel :deep(.el-carousel__item) {
  overflow: hidden;
}

.banner-slide {
  position: relative;
  height: 100%;
  overflow: hidden;
  border-radius: 8px;
}

.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.banner-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.45), rgba(0, 0, 0, 0.15));
  pointer-events: none;
}

.banner-caption {
  position: absolute;
  left: 0;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  text-align: center;
  color: #fff;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.35);
  pointer-events: none;
}

.banner-caption h2 {
  font-size: 40px;
  margin: 0 0 16px;
  font-weight: 700;
}

.banner-caption p {
  font-size: 20px;
  margin: 0;
  opacity: 0.95;
}

.categories-section,
.products-section {
  margin-bottom: 40px;
}

.categories-section h2,
.products-section h2 {
  margin-bottom: 20px;
  font-size: 24px;
}

.category-card :deep(.el-card__body) {
  padding: 12px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.category-thumb {
  width: 100%;
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
  background: #f2f2f2;
}

.category-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.25s ease;
}

.category-card:hover .category-thumb img {
  transform: scale(1.05);
}

.category-name {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
  text-align: center;
  line-height: 1.4;
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.product-info {
  padding: 10px 0;
}

.product-name {
  font-size: 16px;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-subtitle {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  display: flex;
  align-items: center;
  gap: 10px;
}

.current-price {
  font-size: 20px;
  color: #f56c6c;
  font-weight: bold;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
}
</style>
