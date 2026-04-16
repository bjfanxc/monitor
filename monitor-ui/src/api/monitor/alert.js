import request from "@/utils/request"

export function listAlert(query) {
  return request({
    url: "/monitor/alert/list",
    method: "get",
    params: query
  })
}
