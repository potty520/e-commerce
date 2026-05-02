<template>
  <div class="layout-container">
    <header class="header">
      <div class="container header-inner">
        <div class="header-left">
          <router-link to="/home" class="logo">电商平台</router-link>
          <nav class="main-nav" aria-label="主导航">
            <router-link to="/home" class="nav-item" :class="{ active: isHomeActive }">首页</router-link>
            <router-link
              to="/product/list"
              class="nav-item"
              :class="{ active: isProductNavActive }"
            >
              商品
            </router-link>
          </nav>
        </div>
        <div class="header-right">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索商品"
            class="search-input"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="handleSearch" />
            </template>
          </el-input>
          <router-link to="/cart" class="cart-link">
            <el-badge :value="cartCount" :hidden="cartCount === 0">
              <el-button :icon="ShoppingCart">购物车</el-button>
            </el-badge>
          </router-link>
          <router-link v-if="isAdmin" to="/admin/orders" class="admin-link">管理后台</router-link>
          <template v-if="isLoggedIn">
            <el-dropdown @command="handleUserCommand">
              <span class="user-dropdown">
                <el-avatar :size="32" :icon="UserFilled" />
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                  <el-dropdown-item command="address">收货地址</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button @click="$router.push('/login')">登录</el-button>
            <el-button @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </header>
    <main class="main-content">
      <slot />
    </main>
    <footer class="footer">
      <div class="container">
        <p>&copy; 2024 电商平台 版权所有</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search, ShoppingCart, UserFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getCartCount } from '@/api/cart'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const searchKeyword = ref('')
const cartCount = ref(0)

const isLoggedIn = computed(() => userStore.isLoggedIn)
const isAdmin = computed(() => userStore.userInfo?.role === 1)

const isHomeActive = computed(() => route.path === '/home' || route.path === '/')
const isProductNavActive = computed(() => route.path.startsWith('/product'))

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/product/list', query: { keyword: searchKeyword.value } })
  }
}

const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/user/profile')
      break
    case 'orders':
      router.push('/order/list')
      break
    case 'address':
      router.push('/user/address')
      break
    case 'logout':
      userStore.logout()
      router.push('/login')
      break
  }
}

const fetchCartCount = async () => {
  if (isLoggedIn.value) {
    try {
      const res = await getCartCount()
      cartCount.value = res.data || 0
    } catch (e) {
      console.error(e)
    }
  }
}

onMounted(async () => {
  if (isLoggedIn.value && !userStore.userInfo) {
    try {
      await userStore.getUserInfoAction()
    } catch {
      /* ignore */
    }
  }
  fetchCartCount()
})
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  min-height: 60px;
  flex-wrap: nowrap;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px 24px;
  flex-shrink: 0;
}

.logo {
  font-size: 22px;
  font-weight: 700;
  color: #409eff;
  text-decoration: none;
  letter-spacing: 0.02em;
  white-space: nowrap;
}

.logo:hover {
  color: #66b1ff;
}

.main-nav {
  display: flex;
  align-items: center;
  gap: 4px;
}

.nav-item {
  display: inline-flex;
  align-items: center;
  padding: 8px 18px;
  font-size: 15px;
  font-weight: 500;
  color: #606266;
  text-decoration: none;
  border-radius: 8px;
  transition: color 0.2s, background 0.2s;
  white-space: nowrap;
}

.nav-item:hover {
  color: #409eff;
  background: #ecf5ff;
}

.nav-item.active {
  color: #409eff;
  background: #ecf5ff;
  box-shadow: inset 0 -2px 0 #409eff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  justify-content: flex-end;
  min-width: 0;
}

.search-input {
  width: min(300px, 100%);
  max-width: 36vw;
  flex-shrink: 1;
}

@media (max-width: 768px) {
  .header-inner {
    flex-wrap: wrap;
    padding-top: 8px;
    padding-bottom: 8px;
  }

  .header-left {
    width: 100%;
    justify-content: space-between;
  }

  .header-right {
    width: 100%;
    flex-wrap: wrap;
    justify-content: flex-start;
  }

  .search-input {
    flex: 1;
    max-width: none;
    min-width: 140px;
  }
}

.cart-link {
  display: inline-block;
}

.user-dropdown {
  cursor: pointer;
}

.admin-link {
  font-size: 14px;
  color: #409eff;
  white-space: nowrap;
}

.main-content {
  flex: 1;
  padding: 20px 0;
}

.footer {
  background: #f5f5f5;
  padding: 20px 0;
  text-align: center;
  color: #666;
}
</style>
