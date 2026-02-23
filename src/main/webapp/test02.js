// document.getElementById("password").value

function checkIllegal(e) {
    if(e.value.length < 6) {
        e.setAttribute("class", "illegal-pwd")
    }else {
        e.removeAttribute("class")
    }
}

function http() {
    let xhr = new XMLHttpRequest();
    xhr.open('GET', 'https://www.baidu.com');
    xhr.send();
}

/**
 * 核心功能：通过AJAX异步请求后端接口，获取最新时间并更新到页面指定元素中
 * 适用场景：页面实时刷新时间、无刷新更新数据等场景
 */
function updateTime() {
    // 1. 创建XMLHttpRequest对象（AJAX的核心对象，用于在不刷新页面的情况下与服务器交互）
    let xhr = new XMLHttpRequest();

    // 2. 监听XMLHttpRequest的状态变化事件（readyState改变时触发）
    xhr.onreadystatechange = function() {
        // 3. 判断请求是否完成且成功：
        // readyState === 4 表示请求已完成（整个响应过程结束）
        // status === 200 表示HTTP响应状态码为成功（服务器正常返回数据）
        if (xhr.readyState === 4 && xhr.status === 200) {
            // 4. 将服务器返回的时间文本，更新到页面id为"time"的元素中
            document.getElementById("time").innerText = xhr.responseText
        }
    };

    // 5. 初始化AJAX请求：
    // 参数1：请求方式（GET，适合获取数据，无请求体）
    // 参数2：请求地址（"time"是相对路径，实际会拼接当前页面域名，比如 http://localhost:8081/time）
    // 参数3：是否异步（true表示异步，不阻塞页面其他操作；false为同步，已不推荐使用）
    xhr.open('GET', 'time', true);

    // 6. 发送请求（GET请求无请求体，所以括号内为空；POST请求需在这里传入请求参数）
    xhr.send();
}