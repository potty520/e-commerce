import request from '@/utils/request'

/** 沙箱支付回调（无需登录，由前端代发） */
export function mockPayCallback(data) {
  return request({
    url: '/pay/mock/callback',
    method: 'post',
    data
  })
}

/** 统一下单回调（验签密钥 pay.unified.secret） */
export function unifiedPayCallback(data) {
  return request({
    url: '/payment/unified/callback',
    method: 'post',
    data
  })
}
