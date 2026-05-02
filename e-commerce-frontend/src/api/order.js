import request from '@/utils/request'

export function createOrder(data) {
  return request({
    url: '/order/create',
    method: 'post',
    data
  })
}

export function getOrderList(params) {
  return request({
    url: '/order/list',
    method: 'get',
    params
  })
}

export function getOrderDetail(id) {
  return request({
    url: `/order/detail/${id}`,
    method: 'get'
  })
}

export function cancelOrder(id) {
  return request({
    url: `/order/cancel/${id}`,
    method: 'put'
  })
}

export function payOrder(id, payType) {
  return request({
    url: `/order/pay/${id}`,
    method: 'put',
    params: { payType }
  })
}

export function confirmReceive(id) {
  return request({
    url: `/order/confirm/${id}`,
    method: 'put'
  })
}

export function getMockPayData(orderId) {
  return request({
    url: `/order/pay/mock-data/${orderId}`,
    method: 'get'
  })
}

export function getUnifiedPayData(orderId) {
  return request({
    url: `/order/pay/unified-data/${orderId}`,
    method: 'get'
  })
}

export function applyRefund(orderId, data) {
  return request({
    url: `/order/refund/${orderId}`,
    method: 'post',
    data
  })
}
