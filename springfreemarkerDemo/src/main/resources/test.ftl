<html>
    <head>
        <meta charset="utf-8">
        <title>freemarker入门小Demo</title>
    </head>
    <body>
            <#-- 嵌套模板 -->
            <#include "head.ftl">

            <#assign age = 25>
        年龄:${age}
            <#assign info={'address':'北京王府井','phone':'010-10112345'}>

        地址:${info.address}   电话:${info.phone}

        <#--条件判断-->
        <#if  success == false>
            未通过验证
        <#else >
            通过验证
        </#if>

        <br/>
        <hr>
        <#list goodsList as goods >
            ${goods_index+1}  ${goods.name} 价格: ${goods.price}
        </#list>
        </hr>
    一共${goodsList?size}条记录

        当前日期:${today?date} 当前时间:${today?time} 当前日期与时间:${today?datetime}
        格式化日期: ${today?string('yyyy年MM月')}

            <#-- 判断是否为空-->
        <#if info ??>
            info为空
        <#else>
            ${info}
        </#if>

        ${aaa !'aaa没定义'}

        <#if (age> 18)>
            贤者时间到了
        </#if>

    </body>

</html>