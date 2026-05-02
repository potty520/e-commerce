import request from '@/utils/request'

/** 按订单查询物流（与后端 LogisticsController 对齐） */
export function getLogisticsByOrder(orderId) {
  return request({
    url: `/logistics/order/${orderId}`,
    method: 'get'
  })
}

/** 按承运商编码 + 运单号查询（预留） */
export function queryLogisticsByNumber(params) {
  return request({
    url: '/logistics/query',
    method: 'get',
    params
  })
}
