<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="200px" class="aside">
        <div class="brand">管理后台</div>
        <el-menu router :default-active="$route.path">
          <el-menu-item index="/admin/orders">订单管理</el-menu-item>
          <el-menu-item index="/admin/products">商品管理</el-menu-item>
          <el-menu-item index="/admin/categories">分类管理</el-menu-item>
        </el-menu>
        <div class="aside-footer">
          <el-button type="primary" link @click="$router.push('/home')">返回商城</el-button>
        </div>
      </el-aside>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<style scoped>
.admin-layout {
  min-height: 100vh;
}
.aside {
  background: #fff;
  border-right: 1px solid #eee;
}
.brand {
  padding: 16px;
  font-weight: bold;
  font-size: 16px;
}
.aside-footer {
  padding: 12px;
}
.main {
  background: #f5f5f5;
  min-height: 100vh;
}
</style>
