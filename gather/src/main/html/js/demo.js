/**
 * Created with IntelliJ IDEA.
 * User: AzraelX
 * Date: 13-10-22
 * Time: 下午4:33
 * To change this template use File | Settings | File Templates.
 */

function loadopt(channel){
    $.ajax({
        url: "/demo/getTimeSec",
        type: "GET",
        data: ({channel:channel}),
        dataType: "json",
        error: function (xhr, ajaxOptions, thrownError){
            alert("Http status: " + xhr.status + " " + xhr.statusText + "\najaxOptions: " + ajaxOptions + "\nthrownError:"+thrownError + "\n" +xhr.responseText);
        },
        success: function(data){
//            var jsonObj=eval("("+data.result+")");
            $.each(data.result, function (i, str) {
                if(document.getElementById("selectedTime").innerHTML.split(' ')[1] == str.split("--")[1]){
//                if(document.getElementsByClassName("time-info").item(0).innerText.split(' ')[1] == str.split("--")[1]){
                    $("#tsec").append("<option selected=\"selected\" value='"+str+"'>"+str.split('--')[1]+"</option>");
                } else {
                    $("#tsec").append("<option value='"+str+"'>"+str.split('--')[1]+"</option>");
                }
//                $("#tsec").append("<option value='"+str+"'>"+str.split('--')[1]+"</option>");
            });
//            for(str in data.result){
//                $("#tsec").append("<option value='"+str+"'>"+str+"</option>");
//            }
        }
    });
}

function visit(channel){
    var tsec = $("#tsec").val();
    var today = new Date();
    var tim =tsec.split('--');
//    var year = today.getFullYear().toString();
//    var mon = (today.getMonth()+1).toString();
//    if(mon.length<2){
//        mon = '0' + mon;
//    }
//    var day =today.getDate().toString();
//    var tdstr = $.formatDate(today,'yyyyMMdd');
//    var tdstr = year+mon+day;
    var tdstr = today.Format("yyyyMMdd");
//    var filename = tdstr+"/"+tdstr+tim[0].replace(/:/g,"")+"-"+tdstr+tim[1].replace(/:/g,"")+".html";
    var filename = "/"+channel+"/"+tdstr+"/"+tdstr+tim[0].replace(/:/g,"")+"-"+tdstr+tim[1].replace(/:/g,"")+".html";
    window.open(filename,"_self");
}

function jpage(){
    var tsec = $("#tsec").val();
    var today = new Date();
    var tim =tsec.split('--');
//    var year = today.getFullYear().toString();
//    var mon = (today.getMonth()+1).toString();
//    if(mon.length<2){
//        mon = '0' + mon;
//    }
//    var day =today.getDate().toString();
//    var tdstr = $.formatDate(today,'yyyyMMdd');
//    var tdstr = year+mon+day;
    var tdstr = today.Format("yyyyMMdd");
//    var filename = tdstr+"/"+tdstr+tim[0].replace(/:/g,"")+"-"+tdstr+tim[1].replace(/:/g,"")+".html";
    var filename = tdstr+tim[0].replace(/:/g,"")+"-"+tdstr+tim[1].replace(/:/g,"")+".html";
    window.open(filename,"_self");
}

// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
// 例子：
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}