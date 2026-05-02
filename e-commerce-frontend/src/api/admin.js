import request from '@/utils/request'

export function adminOrderList(params) {
  return request({ url: '/admin/orders', method: 'get', params })
}

export function adminShipOrder(id, data) {
  return request({ url: `/admin/orders/${id}/ship`, method: 'put', data })
}

export function adminApproveRefund(id) {
  return request({ url: `/admin/orders/${id}/refund/approve`, method: 'put' })
}

export function adminRejectRefund(id, data) {
  return request({ url: `/admin/orders/${id}/refund/reject`, method: 'put', data })
}

export function adminOrderRemark(id, data) {
  return request({ url: `/admin/orders/${id}/remark`, method: 'put', data })
}

export function adminCloseOrder(id, data) {
  return request({ url: `/admin/orders/${id}/close`, method: 'put', data })
}

export function adminProductList(params) {
  return request({ url: '/admin/products', method: 'get', params })
}

export function adminProductDetail(id) {
  return request({ url: `/admin/products/${id}`, method: 'get' })
}

export function adminProductCreate(data) {
  return request({ url: '/admin/products', method: 'post', data })
}

export function adminProductUpdate(id, data) {
  return request({ url: `/admin/products/${id}`, method: 'put', data })
}

export function adminProductDelete(id) {
  return request({ url: `/admin/products/${id}`, method: 'delete' })
}

export function adminCategoryList() {
  return request({ url: '/admin/categories', method: 'get' })
}

export function adminCategoryDetail(id) {
  return request({ url: `/admin/categories/${id}`, method: 'get' })
}

export function adminCategoryCreate(data) {
  return request({ url: '/admin/categories', method: 'post', data })
}

export function adminCategoryUpdate(id, data) {
  return request({ url: `/admin/categories/${id}`, method: 'put', data })
}

export function adminCategoryDelete(id) {
  return request({ url: `/admin/categories/${id}`, method: 'delete' })
}
