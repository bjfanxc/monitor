import request from "@/utils/request"

export function getAppOverview() {
  return request({
    url: "/monitor/app/overview",
    method: "get"
  })
}

export function listApp(query) {
  return request({
    url: "/monitor/app/list",
    method: "get",
    params: query
  })
}

export function getAppFormOptions() {
  return request({
    url: "/monitor/app/options",
    method: "get"
  })
}

export function addApp(data) {
  return request({
    url: "/monitor/app",
    method: "post",
    data
  })
}

export function updateApp(data) {
  return request({
    url: "/monitor/app",
    method: "put",
    data
  })
}

export function assignAppAlertChannels(data) {
  return request({
    url: "/monitor/app/assignChannels",
    method: "put",
    data
  })
}

export function changeAppStatus(data) {
  return request({
    url: "/monitor/app/status",
    method: "put",
    data
  })
}

export function delApp(id) {
  return request({
    url: "/monitor/app/" + id,
    method: "delete"
  })
}
