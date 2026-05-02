<template>
  <MainLayout>
    <div class="product-list-container">
      <div class="filter-section">
        <el-card>
          <el-row :gutter="20">
            <el-col :span="4">
              <h3>分类</h3>
              <el-menu :default-active="String(categoryId)" @select="handleCategorySelect">
                <el-menu-item index="">全部</el-menu-item>
                <el-menu-item v-for="cat in displayCategoryList" :key="cat.id" :index="String(cat.id)">
                  {{ cat.displayName }}
                </el-menu-item>
              </el-menu>
            </el-col>
            <el-col :span="20">
              <div class="filter-bar">
                <el-input v-model="keyword" placeholder="搜索商品" class="search-input" @keyup.enter="handleSearch">
                  <template #append>
                    <el-button :icon="Search" @click="handleSearch" />
                  </template>
                </el-input>
                <el-select v-model="sort" placeholder="排序" style="width: 150px" @change="handleSearch">
                  <el-option label="默认" value="" />
                  <el-option label="价格从低到高" value="price_asc" />
                  <el-option label="价格从高到低" value="price_desc" />
                  <el-option label="销量" value="sales" />
                </el-select>
              </div>
              <el-row :gutter="20" class="product-grid">
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
                      <div class="product-meta">
                        <span>销量: {{ product.sales }}</span>
                        <span>库存: {{ product.stock }}</span>
                      </div>
                    </div>
                  </el-card>
                </el-col>
              </el-row>
              <el-pagination
                v-model:current-page="page"
                :page-size="pageSize"
                :total="total"
                layout="total, prev, pager, next"
                @current-change="fetchProducts"
                style="margin-top: 20px; justify-content: center"
              />
            </el-col>
          </el-row>
        </el-card>
      </div>
    </div>
  </MainLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import MainLayout from '@/components/common/MainLayout.vue'
import { getCategoryList, getProductList } from '@/api/product'
import { mergeCategoriesForMenu, toChineseCategoryName } from '@/utils/categoryDisplay'

const router = useRouter()
const route = useRoute()

const categoryList = ref([])
const displayCategoryList = computed(() => mergeCategoriesForMenu(categoryList.value))
const productList = ref([])
const categoryId = ref('')
const keyword = ref('')
const sort = ref('')
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)

const goToDetail = (id) => {
  router.push(`/product/detail/${id}`)
}

const handleCategorySelect = (key) => {
  categoryId.value = key
  page.value = 1
  fetchProducts()
}

const handleSearch = () => {
  page.value = 1
  fetchProducts()
}

const normalizeCategorySelection = () => {
  if (categoryId.value === '' || categoryId.value == null) return
  const qid = String(categoryId.value)
  if (displayCategoryList.value.some((c) => String(c.id) === qid)) return
  const raw = categoryList.value.find((c) => String(c.id) === qid)
  if (!raw) return
  const zh = toChineseCategoryName(raw.name)
  const canon = displayCategoryList.value.find((c) => c.displayName === zh)
  if (canon) categoryId.value = String(canon.id)
}

const fetchCategories = async () => {
  try {
    const res = await getCategoryList()
    categoryList.value = res.data || []
    normalizeCategorySelection()
  } catch (e) {
    console.error(e)
  }
}

const fetchProducts = async () => {
  try {
    const res = await getProductList({
      categoryId: categoryId.value || null,
      keyword: keyword.value || null,
      sort: sort.value || null,
      page: page.value,
      pageSize: pageSize.value
    })
    productList.value = res.data || []
    total.value = res.total || 0
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  if (route.query.categoryId) {
    categoryId.value = route.query.categoryId
  }
  if (route.query.keyword) {
    keyword.value = route.query.keyword
  }
  fetchCategories()
  fetchProducts()
})
</script>

<style scoped>
.product-list-container {
  max-width: 1200px;
  margin: 0 auto;
}

.filter-section h3 {
  margin-bottom: 15px;
}

.filter-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.product-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.product-info {
  padding: 10px 0;
}

.product-name {
  font-size: 14px;
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
  margin-bottom: 5px;
}

.current-price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.original-price {
  font-size: 12px;
  color: #999;
  text-decoration: line-through;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
}
</style>
