import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const routes = [
  { path: '/', redirect: '/home' },
  { path: '/home', name: 'Home', component: () => import('@/views/home/HomeIndex.vue') },
  { path: '/login', name: 'Login', component: () => import('@/views/user/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('@/views/user/Register.vue') },
  { path: '/forgot', name: 'ForgotPassword', component: () => import('@/views/user/ForgotPassword.vue') },
  { path: '/product/list', name: 'ProductList', component: () => import('@/views/product/ProductList.vue') },
  { path: '/product/detail/:id', name: 'ProductDetail', component: () => import('@/views/product/ProductDetail.vue') },
  { path: '/cart', name: 'Cart', component: () => import('@/views/cart/CartIndex.vue') },
  { path: '/order/confirm', name: 'OrderConfirm', component: () => import('@/views/order/OrderConfirm.vue') },
  { path: '/order/list', name: 'OrderList', component: () => import('@/views/order/OrderList.vue') },
  { path: '/order/detail/:id', name: 'OrderDetail', component: () => import('@/views/order/OrderDetail.vue') },
  { path: '/user/profile', name: 'UserProfile', component: () => import('@/views/user/UserProfile.vue') },
  { path: '/user/address', name: 'UserAddress', component: () => import('@/views/user/UserAddress.vue') },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      { path: '', redirect: '/admin/orders' },
      { path: 'orders', name: 'AdminOrders', component: () => import('@/views/admin/AdminOrders.vue') },
      { path: 'products', name: 'AdminProducts', component: () => import('@/views/admin/AdminProducts.vue') },
      { path: 'categories', name: 'AdminCategories', component: () => import('@/views/admin/AdminCategories.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const publicPaths = ['/login', '/register', '/home', '/product/list', '/forgot']

router.beforeEach(async (to, from, next) => {
  const token = localStorage.getItem('token')
  const isPublic =
    publicPaths.includes(to.path) || to.path.startsWith('/product/detail/')

  if (!token && !isPublic) {
    next('/login')
    return
  }

  if (to.meta.requiresAdmin) {
    if (!token) {
      next('/login')
      return
    }
    const userStore = useUserStore()
    if (!userStore.userInfo) {
      try {
        await userStore.getUserInfoAction()
      } catch {
        next('/login')
        return
      }
    }
    if (userStore.userInfo?.role !== 1) {
      ElMessage.warning('需要管理员账号登录')
      next('/home')
      return
    }
  }

  next()
})

export default router
