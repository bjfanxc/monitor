import request from "@/utils/request"

export function listAlertChannel(query) {
  return request({
    url: "/monitor/alert/channel/telegram/list",
    method: "get",
    params: query
  })
}

export function getAlertBindingInfo() {
  return request({
    url: "/monitor/alert/channel/telegram/bindingInfo",
    method: "get"
  })
}

export function discoverTelegramChats(data) {
  return request({
    url: "/monitor/alert/channel/telegram/discoverChats",
    method: "post",
    headers: {
      repeatSubmit: false,
      interval: 300
    },
    data
  })
}

export function addAlertChannel(data) {
  return request({
    url: "/monitor/alert/channel/telegram",
    method: "post",
    data
  })
}

export function updateAlertChannel(data) {
  return request({
    url: "/monitor/alert/channel/telegram",
    method: "put",
    data
  })
}

export function delAlertChannel(id) {
  return request({
    url: "/monitor/alert/channel/telegram/" + id,
    method: "delete"
  })
}
