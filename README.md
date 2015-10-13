# Next Android 库总览

Next 是一个快速开发Android应用的框架. 提供 EventBus, Click Proxy, Inject view 等非常有用的库.

## Events - 事件总线

类似 Google Guava 的 EventBus, NextEvents 提供一个基于注解的事件订阅及发送功能. 
它可以订阅单个事件,也可以订阅多个事件. 可以在UI线程中回调事件处理接口, 也可以在多线程中回调.
 
基于 NextEvents, Next 库提供了 ClickProxy 库和 Flux 开发模式支持基础库.
 
## Click Proxy - 点击代理

只需添加 @EmitClick 即可实现控件点击事件代理, 通过 NextEvents 实现同步或异步处理事件.

## Inject View - 自动注入View

只需要添加 @AutoView 即可实现控件自动注入.

## Flux - 比MVC更清晰的开发模式

Activity, Fragment 处理界面显示;

Store 处理逻辑;

## Storage - KeyValue储存

Key Value 储存
