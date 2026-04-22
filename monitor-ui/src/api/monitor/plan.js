import request from "@/utils/request"

export function listPlan(query) {
  return request({
    url: "/monitor/plan/list",
    method: "get",
    params: query
  })
}

export function getPlan(id) {
  return request({
    url: "/monitor/plan/" + id,
    method: "get"
  })
}

export function addPlan(data) {
  return request({
    url: "/monitor/plan",
    method: "post",
    data
  })
}

export function updatePlan(data) {
  return request({
    url: "/monitor/plan",
    method: "put",
    data
  })
}

export function delPlan(id) {
  return request({
    url: "/monitor/plan/" + id,
    method: "delete"
  })
}

export function getCurrentQuota() {
  return request({
    url: "/monitor/plan/currentQuota",
    method: "get"
  })
}
