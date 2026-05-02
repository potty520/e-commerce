import request from '@/utils/request'

/**
 * 创建预支付单（预留网关，与沙箱回调 /pay/mock/callback 区分）
 * @param {{ orderId: number, channel?: string }} data
 */
export function paymentPrepay(data) {
  return request({
    url: '/payment/prepay',
    method: 'post',
    data
  })
}

/** 查询支付状态（预留：当前多为本地订单推导） */
export function getPaymentStatus(orderId) {
  return request({
    url: '/payment/status',
    method: 'get',
    params: { orderId }
  })
}
