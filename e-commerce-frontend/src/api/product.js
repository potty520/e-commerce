import request from '@/utils/request'

export function getCategoryList() {
  return request({
    url: '/category/list',
    method: 'get'
  })
}

export function getProductList(params) {
  return request({
    url: '/product/list',
    method: 'get',
    params
  })
}

export function getProductDetail(id) {
  return request({
    url: `/product/detail/${id}`,
    method: 'get'
  })
}

export function listProductReviews(productId, params) {
  return request({
    url: `/product/review/list/${productId}`,
    method: 'get',
    params
  })
}

export function createProductReview(data) {
  return request({
    url: '/product/review',
    method: 'post',
    data
  })
}
