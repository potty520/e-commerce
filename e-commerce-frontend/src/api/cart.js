import request from '@/utils/request'

export function getCartList() {
  return request({
    url: '/cart/list',
    method: 'get'
  })
}

export function addToCart(data) {
  return request({
    url: '/cart/add',
    method: 'post',
    data
  })
}

export function updateCartQuantity(id, quantity) {
  return request({
    url: `/cart/update/${id}`,
    method: 'put',
    params: { quantity }
  })
}

export function removeFromCart(id) {
  return request({
    url: `/cart/delete/${id}`,
    method: 'delete'
  })
}

export function selectAllCart(selected) {
  return request({
    url: '/cart/selectAll',
    method: 'put',
    params: { selected }
  })
}

export function selectCartItem(id, selected) {
  return request({
    url: `/cart/select/${id}`,
    method: 'put',
    params: { selected }
  })
}

export function getCartCount() {
  return request({
    url: '/cart/count',
    method: 'get'
  })
}
