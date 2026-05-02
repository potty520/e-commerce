<template>
  <el-card>
    <template #header>
      <div class="hdr">
        <h2>商品管理</h2>
        <el-button type="primary" @click="openEdit()">新增商品</el-button>
      </div>
    </template>
    <el-form :inline="true">
      <el-form-item label="关键词">
        <el-input v-model="keyword" clearable placeholder="名称/副标题" @keyup.enter="load" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="load">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="名称" min-width="160" show-overflow-tooltip />
      <el-table-column prop="categoryId" label="分类ID" width="90" />
      <el-table-column prop="price" label="价格" width="90" />
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="status" label="上架" width="70">
        <template #default="{ row }">{{ row.status === 1 ? '是' : '否' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total" layout="total, prev, pager, next" @current-change="load" class="pager" />

    <el-dialog v-model="visible" :title="form.id ? '编辑商品' : '新增商品'" width="560px" destroy-on-close @closed="resetForm">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="副标题"><el-input v-model="form.subtitle" /></el-form-item>
        <el-form-item label="分类ID"><el-input-number v-model="form.categoryId" :min="1" style="width: 100%" /></el-form-item>
        <el-form-item label="主图URL"><el-input v-model="form.mainImage" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" /></el-form-item>
        <el-form-item label="原价"><el-input-number v-model="form.originalPrice" :min="0" :precision="2" style="width: 100%" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" style="width: 100%" /></el-form-item>
        <el-form-item label="上架"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminProductList, adminProductCreate, adminProductUpdate, adminProductDelete } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const keyword = ref('')

const visible = ref(false)
const saving = ref(false)
const form = reactive({
  id: null,
  name: '',
  subtitle: '',
  categoryId: 101,
  mainImage: '',
  subImages: null,
  description: '',
  price: 0,
  originalPrice: null,
  stock: 0,
  status: 1
})

const resetForm = () => {
  form.id = null
  form.name = ''
  form.subtitle = ''
  form.categoryId = 101
  form.mainImage = 'https://picsum.photos/id/24/400/400'
  form.subImages = null
  form.description = ''
  form.price = 99
  form.originalPrice = null
  form.stock = 100
  form.status = 1
}

const load = async () => {
  loading.value = true
  try {
    const res = await adminProductList({ page: page.value, pageSize: pageSize.value, keyword: keyword.value || undefined })
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const openEdit = (row) => {
  resetForm()
  if (row) {
    Object.assign(form, JSON.parse(JSON.stringify(row)))
  }
  visible.value = true
}

const pickProduct = (f) => ({
  name: f.name,
  subtitle: f.subtitle,
  categoryId: f.categoryId,
  mainImage: f.mainImage,
  subImages: f.subImages,
  description: f.description,
  price: f.price,
  originalPrice: f.originalPrice,
  stock: f.stock,
  status: f.status
})

const save = async () => {
  saving.value = true
  try {
    if (form.id) {
      await adminProductUpdate(form.id, pickProduct(form))
      ElMessage.success('已更新')
    } else {
      await adminProductCreate(pickProduct(form))
      ElMessage.success('已创建')
    }
    visible.value = false
    load()
  } catch (e) {
    console.error(e)
  } finally {
    saving.value = false
  }
}

const remove = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该商品？', '提示', { type: 'warning' })
    await adminProductDelete(row.id)
    ElMessage.success('已删除')
    load()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

onMounted(load)
</script>

<style scoped>
.hdr {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.hdr h2 {
  margin: 0;
}
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
