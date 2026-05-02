import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

/** 登录用图形验证码 */
export function getLoginCaptcha() {
  return request({
    url: '/user/captcha',
    method: 'get'
  })
}

/** 校验图形验证码后发送登录短信 */
export function sendLoginSms(data) {
  return request({
    url: '/user/sms/send-login',
    method: 'post',
    data
  })
}

export function sendForgotSms(data) {
  return request({
    url: '/user/sms/send-forgot',
    method: 'post',
    data
  })
}

/** 短信验证码登录 */
export function loginBySms(data) {
  return request({
    url: '/user/login/sms',
    method: 'post',
    data
  })
}

export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

export function updateUserInfo(data) {
  return request({
    url: '/user/info',
    method: 'put',
    data
  })
}

export function forgotPassword(data) {
  return request({
    url: '/user/forgot',
    method: 'post',
    data
  })
}

export function changePassword(data) {
  return request({
    url: '/user/password',
    method: 'put',
    data
  })
}

export function getAddressList() {
  return request({
    url: '/address/list',
    method: 'get'
  })
}

export function addAddress(data) {
  return request({
    url: '/address/add',
    method: 'post',
    data
  })
}

export function updateAddress(data) {
  return request({
    url: '/address/update',
    method: 'put',
    data
  })
}

export function deleteAddress(id) {
  return request({
    url: `/address/delete/${id}`,
    method: 'delete'
  })
}

export function setDefaultAddress(id) {
  return request({
    url: `/address/default/${id}`,
    method: 'put'
  })
}

export function getDefaultAddress() {
  return request({
    url: '/address/default',
    method: 'get'
  })
}
