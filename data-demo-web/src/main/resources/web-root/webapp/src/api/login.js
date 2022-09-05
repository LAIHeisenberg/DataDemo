import request from '@/utils/request'

export function login(userInfo) {
  return request({
    url: 'auth/login',
    method: 'post',
    data: userInfo
  })
}

export function getInfo() {
  return request({
    url: 'auth/info',
    method: 'get'
  })
}

export function getCodeImg() {
  return request({
    url: 'auth/code',
    method: 'get'
  })
}

export function logout() {
  return request({
    url: 'auth/logout',
    method: 'delete'
  })
}

export function generatePreSignCode() {
  return request({
    url : 'auth/preSign-code',
    method : 'get'
  })
}
