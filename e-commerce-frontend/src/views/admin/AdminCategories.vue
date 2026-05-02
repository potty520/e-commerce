<template>
  <el-card>
    <template #header>
      <div class="hdr">
        <h2>分类管理</h2>
        <el-button type="primary" @click="openEdit()">新增分类</el-button>
      </div>
    </template>
    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="parentId" label="父级ID" width="100" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="status" label="启用" width="80">
        <template #default="{ row }">{{ row.status === 1 ? '是' : '否' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="remove(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="visible" :title="form.id ? '编辑分类' : '新增分类'" width="480px" destroy-on-close @closed="resetForm">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="父级ID"><el-input-number v-model="form.parentId" :min="0" style="width: 100%" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" style="width: 100%" /></el-form-item>
        <el-form-item label="启用"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
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
import { adminCategoryList, adminCategoryCreate, adminCategoryUpdate, adminCategoryDelete } from '@/api/admin'

const loading = ref(false)
const list = ref([])
const visible = ref(false)
const saving = ref(false)
const form = reactive({ id: null, name: '', parentId: null, sort: 0, status: 1 })

const resetForm = () => {
  form.id = null
  form.name = ''
  form.parentId = null
  form.sort = 0
  form.status = 1
}

const load = async () => {
  loading.value = true
  try {
    const res = await adminCategoryList()
    list.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const openEdit = (row) => {
  resetForm()
  if (row) Object.assign(form, JSON.parse(JSON.stringify(row)))
  visible.value = true
}

const save = async () => {
  saving.value = true
  try {
    if (form.id) {
      await adminCategoryUpdate(form.id, { ...form })
      ElMessage.success('已更新')
    } else {
      await adminCategoryCreate({ name: form.name, parentId: form.parentId, sort: form.sort, status: form.status })
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
    await ElMessageBox.confirm('确定删除？', '提示', { type: 'warning' })
    await adminCategoryDelete(row.id)
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
</style>
